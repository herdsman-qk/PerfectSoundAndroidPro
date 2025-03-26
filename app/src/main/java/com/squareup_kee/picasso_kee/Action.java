/*
 * Copyright (c) 2023. YeHwi Kim (kee)
 * This source code was created only for CryptoSampleProject
 *  Author: YeHwi Kim (kee)
 *  Created Date: 12/7/2023.
 *  Version: 2.0.0.
 */
package com.squareup_kee.picasso_kee;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class Action<T> {
    static class RequestWeakReference<M> extends WeakReference<M> {
        final Action action;

        public RequestWeakReference(Action action, M referent, ReferenceQueue<? super M> q) {
            super(referent, q);
            this.action = action;
        }
    }

    final Picasso_kee picassoKee;
    final Request request;
    final WeakReference<T> target;
    final boolean noFade;
    final int memoryPolicy;
    final int networkPolicy;
    final int errorResId;
    final Drawable errorDrawable;
    final String key;
    final Object tag;

    boolean willReplay;
    boolean cancelled;

    Action(Picasso_kee picassoKee, T target, Request request, int memoryPolicy, int networkPolicy,
           int errorResId, Drawable errorDrawable, String key, Object tag, boolean noFade) {
        this.picassoKee = picassoKee;
        this.request = request;
        this.target =
                target == null ? null : new RequestWeakReference<T>(this, target, picassoKee.referenceQueue);
        this.memoryPolicy = memoryPolicy;
        this.networkPolicy = networkPolicy;
        this.noFade = noFade;
        this.errorResId = errorResId;
        this.errorDrawable = errorDrawable;
        this.key = key;
        this.tag = (tag != null ? tag : this);
    }

    abstract void complete(Bitmap result, Picasso_kee.LoadedFrom from);

    abstract void error();

    void cancel() {
        cancelled = true;
    }

    Request getRequest() {
        return request;
    }

    T getTarget() {
        return target == null ? null : target.get();
    }

    String getKey() {
        return key;
    }

    boolean isCancelled() {
        return cancelled;
    }

    boolean willReplay() {
        return willReplay;
    }

    int getMemoryPolicy() {
        return memoryPolicy;
    }

    int getNetworkPolicy() {
        return networkPolicy;
    }

    Picasso_kee getPicassoKee() {
        return picassoKee;
    }

    Picasso_kee.Priority getPriority() {
        return request.priority;
    }

    Object getTag() {
        return tag;
    }
}
