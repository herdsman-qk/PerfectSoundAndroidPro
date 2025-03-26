package com.flaviofaria.kenburnsview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class KenBurnsView extends androidx.appcompat.widget.AppCompatImageView {
    private static final long FRAME_DELAY = 16;
    private Transition mCurrentTrans;
    private RectF mDrawableRect;
    private long mElapsedTime;
    private boolean mInitialized;
    private long mLastFrameTime;
    private final Matrix mMatrix;
    private boolean mPaused;
    private TransitionGenerator mTransGen;
    private TransitionListener mTransitionListener;
    private final RectF mViewportRect;

    /* loaded from: classes.dex */
    public interface TransitionListener {
        void onTransitionEnd(Transition transition);

        void onTransitionStart(Transition transition);
    }

    @Override // android.widget.ImageView
    public void setScaleType(ScaleType scaleType) {
    }

    public KenBurnsView(Context context) {
        this(context, null);
    }

    public KenBurnsView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KenBurnsView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMatrix = new Matrix();
        this.mTransGen = new RandomTransitionGenerator();
        this.mViewportRect = new RectF();
        this.mInitialized = true;
        super.setScaleType(ScaleType.MATRIX);
    }

    @Override // android.widget.ImageView, android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0) {
            resume();
        } else {
            pause();
        }
    }

    @Override // android.widget.ImageView
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        handleImageChange();
    }

    @Override // android.widget.ImageView
    public void setImageResource(int i) {
        super.setImageResource(i);
        handleImageChange();
    }

    @Override // android.widget.ImageView
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        handleImageChange();
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        handleImageChange();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        restart();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            if (this.mDrawableRect.isEmpty()) {
                updateDrawableBounds();
            } else if (hasBounds()) {
                if (this.mCurrentTrans == null) {
                    startNewTransition();
                }
                if (this.mCurrentTrans.getDestinyRect() != null) {
                    long currentTimeMillis = this.mElapsedTime;
                    if (!this.mPaused) {
                        currentTimeMillis = this.mElapsedTime + (System.currentTimeMillis() - this.mLastFrameTime);
                    }
                    this.mElapsedTime = currentTimeMillis;
                    RectF interpolatedRect = this.mCurrentTrans.getInterpolatedRect(currentTimeMillis);
                    float min = Math.min(this.mDrawableRect.width() / interpolatedRect.width(), this.mDrawableRect.height() / interpolatedRect.height()) * Math.min(this.mViewportRect.width() / interpolatedRect.width(), this.mViewportRect.height() / interpolatedRect.height());
                    this.mMatrix.reset();
                    this.mMatrix.postTranslate((-this.mDrawableRect.width()) / 2.0f, (-this.mDrawableRect.height()) / 2.0f);
                    this.mMatrix.postScale(min, min);
                    this.mMatrix.postTranslate((this.mDrawableRect.centerX() - interpolatedRect.left) * min, (this.mDrawableRect.centerY() - interpolatedRect.top) * min);

                    setImageMatrix(this.mMatrix);
                    if (this.mElapsedTime >= this.mCurrentTrans.getDuration()) {
                        fireTransitionEnd(this.mCurrentTrans);
                        startNewTransition();
                    }
                } else {
                    fireTransitionEnd(this.mCurrentTrans);
                }
            }
            this.mLastFrameTime = System.currentTimeMillis();
            postInvalidateDelayed(16L);
        }
        super.onDraw(canvas);
    }

    private void startNewTransition() {
        if (hasBounds()) {
            this.mCurrentTrans = this.mTransGen.generateNextTransition(this.mDrawableRect, this.mViewportRect);
            this.mElapsedTime = 0L;
            this.mLastFrameTime = System.currentTimeMillis();
            fireTransitionStart(this.mCurrentTrans);
        }
    }

    public void restart() {
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) {
            return;
        }
        updateViewport(width, height);
        updateDrawableBounds();
        startNewTransition();
    }

    private boolean hasBounds() {
        return !this.mViewportRect.isEmpty();
    }

    private void fireTransitionStart(Transition transition) {
        TransitionListener transitionListener = this.mTransitionListener;
        if (transitionListener == null || transition == null) {
            return;
        }
        transitionListener.onTransitionStart(transition);
    }

    private void fireTransitionEnd(Transition transition) {
        TransitionListener transitionListener = this.mTransitionListener;
        if (transitionListener == null || transition == null) {
            return;
        }
        transitionListener.onTransitionEnd(transition);
    }

    public void setTransitionGenerator(TransitionGenerator transitionGenerator) {
        this.mTransGen = transitionGenerator;
        startNewTransition();
    }

    private void updateViewport(float f, float f2) {
        this.mViewportRect.set(0.0f, 0.0f, f, f2);
    }

    private void updateDrawableBounds() {
        if (this.mDrawableRect == null) {
            this.mDrawableRect = new RectF();
        }
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicHeight() <= 0 || drawable.getIntrinsicWidth() <= 0) {
            return;
        }
        this.mDrawableRect.set(0.0f, 0.0f, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    private void handleImageChange() {
        updateDrawableBounds();
        if (this.mInitialized) {
            startNewTransition();
        }
    }

    public void setTransitionListener(TransitionListener transitionListener) {
        this.mTransitionListener = transitionListener;
    }

    public void pause() {
        this.mPaused = true;
    }

    public void resume() {
        this.mPaused = false;
        this.mLastFrameTime = System.currentTimeMillis();
        invalidate();
    }
}
