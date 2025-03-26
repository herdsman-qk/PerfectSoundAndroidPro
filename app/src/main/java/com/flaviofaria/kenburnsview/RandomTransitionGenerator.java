package com.flaviofaria.kenburnsview;

import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.Random;

/* loaded from: classes.dex */
public class RandomTransitionGenerator implements TransitionGenerator {
    public static final int DEFAULT_TRANSITION_DURATION = 10000;
    private static final float MIN_RECT_FACTOR = 0.75f;
    private RectF mLastDrawableBounds;
    private Transition mLastGenTrans;
    private final Random mRandom;
    private long mTransitionDuration;
    private Interpolator mTransitionInterpolator;

    public RandomTransitionGenerator() {
        this(10000, new AccelerateDecelerateInterpolator());
    }

    public RandomTransitionGenerator(long j, Interpolator interpolator) {
        this.mRandom = new Random(System.currentTimeMillis());
        setTransitionDuration(j);
        setTransitionInterpolator(interpolator);
    }

    @Override // com.flaviofaria.kenburnsview.TransitionGenerator
    public Transition generateNextTransition(RectF rectF, RectF rectF2) {
        RectF rectF3;
        boolean z;
        Transition transition = this.mLastGenTrans;
        boolean z2 = true;
        if (transition == null) {
            rectF3 = null;
            z = true;
        } else {
            rectF3 = transition.getDestinyRect();
            boolean z3 = !rectF.equals(this.mLastDrawableBounds);
            z = true ^ MathUtils.haveSameAspectRatio(rectF3, rectF2);
            z2 = z3;
        }
        if (rectF3 == null || z2 || z) {
            rectF3 = generateRandomRect(rectF, rectF2);
        }
        this.mLastGenTrans = new Transition(rectF3, generateRandomRect(rectF, rectF2), this.mTransitionDuration, this.mTransitionInterpolator);
        this.mLastDrawableBounds = new RectF(rectF);
        return this.mLastGenTrans;
    }

    private RectF generateRandomRect(RectF rectF, RectF rectF2) {
        RectF rectF3;
        if (MathUtils.getRectRatio(rectF) > MathUtils.getRectRatio(rectF2)) {
            rectF3 = new RectF(0.0f, 0.0f, (rectF.height() / rectF2.height()) * rectF2.width(), rectF.height());
        } else {
            rectF3 = new RectF(0.0f, 0.0f, rectF.width(), (rectF.width() / rectF2.width()) * rectF2.height());
        }
        float truncate = (MathUtils.truncate(this.mRandom.nextFloat(), 2) * 0.25f) + 0.75f;
        float width = rectF3.width() * truncate;
        float height = truncate * rectF3.height();
        int width2 = (int) (rectF.width() - width);
        int height2 = (int) (rectF.height() - height);
        float nextInt = width2 > 0 ? this.mRandom.nextInt(width2) : 0;
        float nextInt2 = height2 > 0 ? this.mRandom.nextInt(height2) : 0;
        return new RectF(nextInt, nextInt2, width + nextInt, height + nextInt2);
    }

    public void setTransitionDuration(long j) {
        this.mTransitionDuration = j;
    }

    public void setTransitionInterpolator(Interpolator interpolator) {
        this.mTransitionInterpolator = interpolator;
    }
}
