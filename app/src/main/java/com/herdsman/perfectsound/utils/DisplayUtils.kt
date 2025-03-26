/*
 * Copyright (c) 2023 YeHwi Kim (KeE)
 * This source code was created only for ScreenLockerProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 27/3/2023
 */

package com.herdsman.perfectsound.utils

import android.content.Context

object DisplayUtils {
    var sDensity = 1.0f
    var scale = 1f
    var widthPixels = 720
    var heightPixels = 1080
    fun dip2px(dipValue: Float): Int {
        return (sDensity * dipValue + 0.5f).toInt()
    }

    fun px2dip(pxValue: Float): Int {
        return (pxValue / sDensity + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        return (sDensity * spValue).toInt()
    }

    fun px2sp(pxValue: Float): Int {
        return (pxValue / sDensity).toInt()
    }

    fun resetDensity(context: Context?) {
        if (context != null && context.resources != null) {
            val metrics = context.resources.displayMetrics
            sDensity = metrics.density
            scale = context.resources.displayMetrics.widthPixels / 720f
            widthPixels = context.resources.displayMetrics.widthPixels
            heightPixels = context.resources.displayMetrics.heightPixels
        }

    }
}