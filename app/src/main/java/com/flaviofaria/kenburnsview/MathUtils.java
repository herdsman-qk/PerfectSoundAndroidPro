package com.flaviofaria.kenburnsview;

import android.graphics.RectF;

/* loaded from: classes.dex */
public final class MathUtils {
    /* JADX INFO: Access modifiers changed from: protected */
    public static float truncate(float f, int i) {
        float pow = (float) Math.pow(10.0d, i);
        return Math.round(f * pow) / pow;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean haveSameAspectRatio(RectF rectF, RectF rectF2) {
        return Math.abs(truncate(getRectRatio(rectF), 3) - truncate(getRectRatio(rectF2), 3)) <= 0.01f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static float getRectRatio(RectF rectF) {
        return rectF.width() / rectF.height();
    }
}
