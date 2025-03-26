/*
 * Copyright (c) 2023-2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 3/8/2023
 */

package com.herdsman.perfectsound.utils

import android.util.Log

object LogUtils {
    private const val TAG = "kee_log"
    fun d(tag: String?, vararg args: Any) {
        if (args.size == 1) {
            Log.d(TAG, tag + " --> " + args[0])
        } else {
            val rlt = StringBuilder()
            for (i in args.indices) {
                rlt.append(if (i == 0) args[i] else ", " + args[i])
            }
            Log.d(TAG, "$tag --> $rlt")
        }
    }
}