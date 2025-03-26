/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import static com.squareup_kee.picasso_kee.Utils.getLogIdsForHunter;
import static com.squareup_kee.picasso_kee.Utils.log;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

class BitmapHunter implements Runnable {
    /**
     * Global lock for bitmap decoding to ensure that we are only are decoding one at a time. Since
     * this will only ever happen in background threads we help avoid excessive memory thrashing as
     * well as potential OOMs. Shamelessly stolen from Volley.
     */
    private static final Object DECODE_LOCK = new Object();

    private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(Utils.THREAD_PREFIX);
        }
    };

    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

    private static final RequestHandler ERRORING_HANDLER = new RequestHandler() {
        @Override
        public boolean canHandleRequest(Request data) {
            return true;
        }

        @Override
        public Result load(Request request, int networkPolicy) throws IOException {
            throw new IllegalStateException("Unrecognized type of request: " + request);
        }
    };

    final int sequence;
    final Picasso_kee picassoKee;
    final Dispatcher dispatcher;
    final Cache cache;
    final Stats stats;
    final String key;
    final Request data;
    final int memoryPolicy;
    int networkPolicy;
    final RequestHandler requestHandler;

    Action action;
    List<Action> actions;
    Bitmap result;
    Future<?> future;
    Picasso_kee.LoadedFrom loadedFrom;
    Exception exception;
    int exifRotation; // Determined during decoding of original resource.
    int retryCount;
    Picasso_kee.Priority priority;

    BitmapHunter(Picasso_kee picassoKee, Dispatcher dispatcher, Cache cache, Stats stats, Action action,
                 RequestHandler requestHandler) {
        this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
        this.picassoKee = picassoKee;
        this.dispatcher = dispatcher;
        this.cache = cache;
        this.stats = stats;
        this.action = action;
        this.key = action.getKey();
        this.data = action.getRequest();
        this.priority = action.getPriority();
        this.memoryPolicy = action.getMemoryPolicy();
        this.networkPolicy = action.getNetworkPolicy();
        this.requestHandler = requestHandler;
        this.retryCount = requestHandler.getRetryCount();
    }

    /**
     * Decode a byte stream into a Bitmap. This method will take into account additional information
     * about the supplied request in order to do the decoding efficiently (such as through leveraging
     * {@code inSampleSize}).
     */
    static Bitmap decodeStream(InputStream stream, Request request) throws IOException {
        MarkableInputStream markStream = new MarkableInputStream(stream);
        stream = markStream;

        long mark = markStream.savePosition(65536); // TODO fix this crap.

        final BitmapFactory.Options options = RequestHandler.createBitmapOptions(request);
        final boolean calculateSize = RequestHandler.requiresInSampleSize(options);

        boolean isWebPFile = Utils.isWebPFile(stream);
        markStream.reset(mark);
        // When decode WebP network stream, BitmapFactory throw JNI Exception and make app crash.
        // Decode byte array instead
        if (isWebPFile) {
            byte[] bytes = Utils.toByteArray(stream);
            if (calculateSize) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, options,
                        request);
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        } else {
            if (calculateSize) {
                BitmapFactory.decodeStream(stream, null, options);
                RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, options,
                        request);

                markStream.reset(mark);
            }
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
            if (bitmap == null) {
                // Treat null as an IO exception, we will eventually retry.
                throw new IOException("Failed to decode stream.");
            }
            return bitmap;
        }
    }

    @Override
    public void run() {
        try {
            updateThreadName(data);

            if (picassoKee.loggingEnabled) {
                Utils.log(Utils.OWNER_HUNTER, Utils.VERB_EXECUTING, Utils.getLogIdsForHunter(this));
            }

            result = hunt();

            if (result == null) {
                dispatcher.dispatchFailed(this);
            } else {
                dispatcher.dispatchComplete(this);
            }
        } catch (Downloader.ResponseException e) {
            if (!e.localCacheOnly || e.responseCode != 504) {
                exception = e;
            }
            dispatcher.dispatchFailed(this);
        } catch (NetworkRequestHandler.ContentLengthException e) {
            exception = e;
            dispatcher.dispatchRetry(this);
        } catch (IOException e) {
            exception = e;
            dispatcher.dispatchRetry(this);
        } catch (OutOfMemoryError e) {
            StringWriter writer = new StringWriter();
            stats.createSnapshot().dump(new PrintWriter(writer));
            exception = new RuntimeException(writer.toString(), e);
            dispatcher.dispatchFailed(this);
        } catch (Exception e) {
            exception = e;
            dispatcher.dispatchFailed(this);
        } finally {
            Thread.currentThread().setName(Utils.THREAD_IDLE_NAME);
        }
    }

    Bitmap hunt() throws IOException {
        Bitmap bitmap = null;
        if (MemoryPolicy.shouldReadFromMemoryCache(memoryPolicy)) {
            bitmap = cache.get(key);
            if (bitmap != null) {
                stats.dispatchCacheHit();
                loadedFrom = Picasso_kee.LoadedFrom.MEMORY;
                if (picassoKee.loggingEnabled) {
                    Utils.log(Utils.OWNER_HUNTER, Utils.VERB_DECODED, data.logId(), "from cache");
                }
                return bitmap;
            }
        }

        data.networkPolicy = retryCount == 0 ? NetworkPolicy.OFFLINE.index : networkPolicy;
        RequestHandler.Result result = requestHandler.load(data, networkPolicy);
        if (result != null) {
            loadedFrom = result.getLoadedFrom();
            exifRotation = result.getExifOrientation();

            bitmap = result.getBitmap();

            // If there was no Bitmap then we need to decode it from the stream.
            if (bitmap == null) {
                InputStream is = result.getStream();
                try {
                    bitmap = decodeStream(is, data);
                } finally {
                    Utils.closeQuietly(is);
                }
            }
        }

        if (bitmap != null) {
            if (picassoKee.loggingEnabled) {
                Utils.log(Utils.OWNER_HUNTER, Utils.VERB_DECODED, data.logId());
            }
            stats.dispatchBitmapDecoded(bitmap);
            if (data.needsTransformation() || exifRotation != 0) {
                synchronized (DECODE_LOCK) {
                    if (data.needsMatrixTransform() || exifRotation != 0) {
                        bitmap = transformResult(data, bitmap, exifRotation);
                        if (picassoKee.loggingEnabled) {
                            Utils.log(Utils.OWNER_HUNTER, Utils.VERB_TRANSFORMED, data.logId());
                        }
                    }
                    if (data.hasCustomTransformations()) {
                        bitmap = applyCustomTransformations(data.transformations, bitmap);
                        if (picassoKee.loggingEnabled) {
                            Utils.log(Utils.OWNER_HUNTER, Utils.VERB_TRANSFORMED, data.logId(), "from custom transformations");
                        }
                    }
                }
                if (bitmap != null) {
                    stats.dispatchBitmapTransformed(bitmap);
                }
            }
        }

        return bitmap;
    }

    void attach(Action action) {
        boolean loggingEnabled = picassoKee.loggingEnabled;
        Request request = action.request;

        if (this.action == null) {
            this.action = action;
            if (loggingEnabled) {
                if (actions == null || actions.isEmpty()) {
                    Utils.log(Utils.OWNER_HUNTER, Utils.VERB_JOINED, request.logId(), "to empty hunter");
                } else {
                    Utils.log(Utils.OWNER_HUNTER, Utils.VERB_JOINED, request.logId(), Utils.getLogIdsForHunter(this, "to "));
                }
            }
            return;
        }

        if (actions == null) {
            actions = new ArrayList<Action>(3);
        }

        actions.add(action);

        if (loggingEnabled) {
            Utils.log(Utils.OWNER_HUNTER, Utils.VERB_JOINED, request.logId(), Utils.getLogIdsForHunter(this, "to "));
        }

        Picasso_kee.Priority actionPriority = action.getPriority();
        if (actionPriority.ordinal() > priority.ordinal()) {
            priority = actionPriority;
        }
    }

    void detach(Action action) {
        boolean detached = false;
        if (this.action == action) {
            this.action = null;
            detached = true;
        } else if (actions != null) {
            detached = actions.remove(action);
        }

        // The action being detached had the highest priority. Update this
        // hunter's priority with the remaining actions.
        if (detached && action.getPriority() == priority) {
            priority = computeNewPriority();
        }

        if (picassoKee.loggingEnabled) {
            Utils.log(Utils.OWNER_HUNTER, Utils.VERB_REMOVED, action.request.logId(), Utils.getLogIdsForHunter(this, "from "));
        }
    }

    private Picasso_kee.Priority computeNewPriority() {
        Picasso_kee.Priority newPriority = Picasso_kee.Priority.LOW;

        boolean hasMultiple = actions != null && !actions.isEmpty();
        boolean hasAny = action != null || hasMultiple;

        // Hunter has no requests, low priority.
        if (!hasAny) {
            return newPriority;
        }

        if (action != null) {
            newPriority = action.getPriority();
        }

        if (hasMultiple) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, n = actions.size(); i < n; i++) {
                Picasso_kee.Priority actionPriority = actions.get(i).getPriority();
                if (actionPriority.ordinal() > newPriority.ordinal()) {
                    newPriority = actionPriority;
                }
            }
        }

        return newPriority;
    }

    boolean cancel() {
        return action == null
                && (actions == null || actions.isEmpty())
                && future != null
                && future.cancel(false);
    }

    boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        boolean hasRetries = retryCount > 0;
        if (!hasRetries) {
            return false;
        }
        retryCount--;
        return requestHandler.shouldRetry(airplaneMode, info);
    }

    boolean supportsReplay() {
        return requestHandler.supportsReplay();
    }

    Bitmap getResult() {
        return result;
    }

    String getKey() {
        return key;
    }

    int getMemoryPolicy() {
        return memoryPolicy;
    }

    Request getData() {
        return data;
    }

    Action getAction() {
        return action;
    }

    Picasso_kee getPicassoKee() {
        return picassoKee;
    }

    List<Action> getActions() {
        return actions;
    }

    Exception getException() {
        return exception;
    }

    Picasso_kee.LoadedFrom getLoadedFrom() {
        return loadedFrom;
    }

    Picasso_kee.Priority getPriority() {
        return priority;
    }

    static void updateThreadName(Request data) {
        String name = data.getName();

        StringBuilder builder = NAME_BUILDER.get();
        builder.ensureCapacity(Utils.THREAD_PREFIX.length() + name.length());
        builder.replace(Utils.THREAD_PREFIX.length(), builder.length(), name);

        Thread.currentThread().setName(builder.toString());
    }

    static BitmapHunter forRequest(Picasso_kee picassoKee, Dispatcher dispatcher, Cache cache, Stats stats,
                                   Action action) {
        Request request = action.getRequest();
        List<RequestHandler> requestHandlers = picassoKee.getRequestHandlers();

        // Index-based loop to avoid allocating an iterator.
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, count = requestHandlers.size(); i < count; i++) {
            RequestHandler requestHandler = requestHandlers.get(i);
            if (requestHandler.canHandleRequest(request)) {
                return new BitmapHunter(picassoKee, dispatcher, cache, stats, action, requestHandler);
            }
        }

        return new BitmapHunter(picassoKee, dispatcher, cache, stats, action, ERRORING_HANDLER);
    }

    static Bitmap applyCustomTransformations(List<Transformation> transformations, Bitmap result) {
        for (int i = 0, count = transformations.size(); i < count; i++) {
            final Transformation transformation = transformations.get(i);
            Bitmap newResult;
            try {
                newResult = transformation.transform(result);
            } catch (final RuntimeException e) {
                Picasso_kee.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        throw new RuntimeException(
                                "Transformation " + transformation.key() + " crashed with exception.", e);
                    }
                });
                return null;
            }

            if (newResult == null) {
                final StringBuilder builder = new StringBuilder() //
                        .append("Transformation ")
                        .append(transformation.key())
                        .append(" returned null after ")
                        .append(i)
                        .append(" previous transformation(s).\n\nTransformation list:\n");
                for (Transformation t : transformations) {
                    builder.append(t.key()).append('\n');
                }
                Picasso_kee.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        throw new NullPointerException(builder.toString());
                    }
                });
                return null;
            }

            if (newResult == result && result.isRecycled()) {
                Picasso_kee.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        throw new IllegalStateException("Transformation "
                                + transformation.key()
                                + " returned input Bitmap but recycled it.");
                    }
                });
                return null;
            }

            // If the transformation returned a new bitmap ensure they recycled the original.
            if (newResult != result && !result.isRecycled()) {
                Picasso_kee.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        throw new IllegalStateException("Transformation "
                                + transformation.key()
                                + " mutated input Bitmap but failed to recycle the original.");
                    }
                });
                return null;
            }

            result = newResult;
        }
        return result;
    }

    static Bitmap transformResult(Request data, Bitmap result, int exifRotation) {
        int inWidth = result.getWidth();
        int inHeight = result.getHeight();
        boolean onlyScaleDown = data.onlyScaleDown;

        int drawX = 0;
        int drawY = 0;
        int drawWidth = inWidth;
        int drawHeight = inHeight;

        Matrix matrix = new Matrix();

        if (data.needsMatrixTransform()) {
            int targetWidth = data.targetWidth;
            int targetHeight = data.targetHeight;

            float targetRotation = data.rotationDegrees;
            if (targetRotation != 0) {
                if (data.hasRotationPivot) {
                    matrix.setRotate(targetRotation, data.rotationPivotX, data.rotationPivotY);
                } else {
                    matrix.setRotate(targetRotation);
                }
            }

            if (data.centerCrop) {
                float widthRatio = targetWidth / (float) inWidth;
                float heightRatio = targetHeight / (float) inHeight;
                float scaleX, scaleY;
                if (widthRatio > heightRatio) {
                    int newSize = (int) Math.ceil(inHeight * (heightRatio / widthRatio));
                    drawY = (inHeight - newSize) / 2;
                    drawHeight = newSize;
                    scaleX = widthRatio;
                    scaleY = targetHeight / (float) drawHeight;
                } else {
                    int newSize = (int) Math.ceil(inWidth * (widthRatio / heightRatio));
                    drawX = (inWidth - newSize) / 2;
                    drawWidth = newSize;
                    scaleX = targetWidth / (float) drawWidth;
                    scaleY = heightRatio;
                }
                if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
                    matrix.preScale(scaleX, scaleY);
                }
            } else if (data.centerInside) {
                float widthRatio = targetWidth / (float) inWidth;
                float heightRatio = targetHeight / (float) inHeight;
                float scale = widthRatio < heightRatio ? widthRatio : heightRatio;
                if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
                    matrix.preScale(scale, scale);
                }
            } else if ((targetWidth != 0 || targetHeight != 0) //
                    && (targetWidth != inWidth || targetHeight != inHeight)) {
                // If an explicit target size has been specified and they do not match the results bounds,
                // pre-scale the existing matrix appropriately.
                // Keep aspect ratio if one dimension is set to 0.
                float sx =
                        targetWidth != 0 ? targetWidth / (float) inWidth : targetHeight / (float) inHeight;
                float sy =
                        targetHeight != 0 ? targetHeight / (float) inHeight : targetWidth / (float) inWidth;
                if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
                    matrix.preScale(sx, sy);
                }
            }
        }

        if (exifRotation != 0) {
            matrix.preRotate(exifRotation);
        }

        Bitmap newResult =
                Bitmap.createBitmap(result, drawX, drawY, drawWidth, drawHeight, matrix, true);
        if (newResult != result) {
            result.recycle();
            result = newResult;
        }

        return result;
    }

    private static boolean shouldResize(boolean onlyScaleDown, int inWidth, int inHeight,
                                        int targetWidth, int targetHeight) {
        return !onlyScaleDown || inWidth > targetWidth || inHeight > targetHeight;
    }
}
