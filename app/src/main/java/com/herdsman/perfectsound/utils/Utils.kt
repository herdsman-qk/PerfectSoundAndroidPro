/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 7/8/2023
 */

package com.herdsman.perfectsound.utils

import android.content.Context
import android.widget.ImageView
import com.herdsman.perfectsound.KeEIDB
import com.herdsman.perfectsound.R

object Utils {
    fun setEffectIcon(context: Context, iconView: ImageView, path: String?) {
        if (path == null) {
            iconView.setImageResource(R.drawable.vector_ic_edit)
        } else if (path.contains("drawable:")) {
            iconView.setImageResource(
                context.resources.getIdentifier(
                    path.replace("drawable:", ""),
                    "drawable",
                    context.packageName
                )
            )
        } else if (path.contains("png")) {
            iconView.setImageBitmap(KeEIDB.bitmap(path))
        }
    }

    fun getTimeString(d: Int): String {
        return String.format("%02d:%02d", d / 60000, (d % 60000) / 1000)
    }
}