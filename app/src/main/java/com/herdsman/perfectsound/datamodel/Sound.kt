/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 3/8/2023
 */

package com.herdsman.perfectsound.datamodel

import com.herdsman.perfectsound.provider.AppProvider
import com.herdsman.perfectsound.provider.AppProvider.gson
import com.google.gson.reflect.TypeToken

data class Sound(
    val uid: String = "uid",
    val title: String = "no title",
    val audio: String = "no audio",
    val image: String = "no image",
    val effects: ArrayList<EffectSet> = ArrayList()
) {
    fun effect(): ArrayList<EffectSet> {
        val pref = AppProvider.preferences
        return if (pref.contains("effect_$uid")) {
            val s = pref.getString("effect_$uid", "")
            val type = object : TypeToken<ArrayList<EffectSet?>?>() {}.type
            gson.fromJson(s, type)
        } else {
            effects
        }
    }

    fun isFav(): Boolean {
        val pref = AppProvider.preferences
        return pref.getBoolean("fav_$uid", false)
    }

    fun registerFav() {
        val editor = AppProvider.preferences.edit()
        editor.putBoolean("fav_$uid", true)
        editor.apply()
    }

    fun unregisterFav() {
        val editor = AppProvider.preferences.edit()
        editor.putBoolean("fav_$uid", false)
        editor.apply()
    }
}
