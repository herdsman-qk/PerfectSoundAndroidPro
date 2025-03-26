package com.flaviofaria.kenburnsview;

import android.graphics.RectF;
import android.view.animation.Interpolator;

/* loaded from: classes.dex */
public class Transition {
    private float mCenterXDiff;
    private float mCenterYDiff;
    private final RectF mCurrentRect = new RectF();
    private RectF mDstRect;
    private long mDuration;
    private float mHeightDiff;
    private Interpolator mInterpolator;
    private RectF mSrcRect;
    private float mWidthDiff;

    public Transition(RectF rectF, RectF rectF2, long j, Interpolator interpolator) {
        if (!MathUtils.haveSameAspectRatio(rectF, rectF2)) {
            throw new IncompatibleRatioException();
        }
        this.mSrcRect = rectF;
        this.mDstRect = rectF2;
        this.mDuration = j;
        this.mInterpolator = interpolator;
        this.mWidthDiff = rectF2.width() - rectF.width();
        this.mHeightDiff = rectF2.height() - rectF.height();
        this.mCenterXDiff = rectF2.centerX() - rectF.centerX();
        this.mCenterYDiff = rectF2.centerY() - rectF.centerY();
    }

    public RectF getSourceRect() {
        return this.mSrcRect;
    }

    public RectF getDestinyRect() {
        return this.mDstRect;
    }

    public RectF getInterpolatedRect(long j) {
        float interpolation = this.mInterpolator.getInterpolation(Math.min(((float) j) / ((float) this.mDuration), 1.0f));
        float width = this.mSrcRect.width() + (this.mWidthDiff * interpolation);
        float height = this.mSrcRect.height() + (this.mHeightDiff * interpolation);
        float centerX = (this.mSrcRect.centerX() + (this.mCenterXDiff * interpolation)) - (width / 2.0f);
        float centerY = (this.mSrcRect.centerY() + (interpolation * this.mCenterYDiff)) - (height / 2.0f);
        this.mCurrentRect.set(centerX, centerY, width + centerX, height + centerY);
        return this.mCurrentRect;
    }

    public long getDuration() {
        return this.mDuration;
    }
}
