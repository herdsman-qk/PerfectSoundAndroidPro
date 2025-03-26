/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 3/8/2023
 */

package com.herdsman.perfectsound.provider

import com.herdsman.perfectsound.KeEIDB
import com.herdsman.perfectsound.datamodel.Effect
import com.herdsman.perfectsound.datamodel.Music
import com.herdsman.perfectsound.datamodel.Sound
import com.herdsman.perfectsound.provider.AppProvider.gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.IOUtils


object DataProvider {
    var effectsMap: HashMap<String, Effect> = HashMap()
    var sounds: ArrayList<Sound> = ArrayList()
    var effects: ArrayList<Effect> = ArrayList()
    var musics: ArrayList<Music> = ArrayList()

    fun loadData() {
        val type = object : TypeToken<ArrayList<Effect?>?>() {}.type
        effects = gson.fromJson(IOUtils.toString(KeEIDB.inputStream("effects.json")), type)
        effects.forEach {
            effectsMap[it.uid] = it
        }

        val type2 = object : TypeToken<ArrayList<Sound?>?>() {}.type
        sounds = gson.fromJson(IOUtils.toString(KeEIDB.inputStream("sounds.json")), type2)

        val type3 = object : TypeToken<ArrayList<Music?>?>() {}.type
        musics = gson.fromJson(IOUtils.toString(KeEIDB.inputStream("musics.json")), type3)
    }
}