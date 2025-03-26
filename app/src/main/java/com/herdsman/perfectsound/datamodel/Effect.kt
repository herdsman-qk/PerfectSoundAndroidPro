/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 3/8/2023
 */

package com.herdsman.perfectsound.datamodel

data class Effect(val uid: String, val name: String, val audio: String, val icon: String) {
    fun getFileName(): String {
        val tmp = audio.split("/")
        return tmp[tmp.size - 1]
    }
}
