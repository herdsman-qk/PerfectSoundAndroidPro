/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 14/8/2023
 */

package com.herdsman.perfectsound.provider

import android.media.MediaPlayer
import com.herdsman.perfectsound.KeEIDB
import com.herdsman.perfectsound.datamodel.EffectPlayerSet
import com.herdsman.perfectsound.datamodel.EffectSet
import org.apache.commons.io.FileUtils
import java.io.File

class EffectPlayer {
    companion object {
        const val TAG = "EffectPlayer"
    }

    val data = ArrayList<EffectPlayerSet>()

    var playing = true

    private fun clear() {
        data.forEach {
            try {
                it.player.reset()
                it.player.release()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        data.clear()
    }

    fun add(uid: String, level: Int) {
        val mp = MediaPlayer()
        val effect = DataProvider.effectsMap[uid]!!
        val dest = File(AppProvider.mContext.filesDir, "effect_${effect.getFileName()}")
        FileUtils.copyInputStreamToFile(KeEIDB.inputStream(effect.audio), dest)
        mp.setDataSource(dest.absolutePath)
        mp.setOnCompletionListener {
            mp.start()
        }
        mp.setOnPreparedListener {
            if (playing)
                mp.start()
        }

        mp.prepareAsync()
        val v = PVM.mediaVolume.value!! * level / 1500f
        mp.setVolume(v, v)
        data.add(EffectPlayerSet(EffectSet(uid, level), mp))
    }

    fun play() {
        data.forEach {
//            if (!it.player.isPlaying) {
//                it.player.reset()
//                it.player.release()
//
//                it.player = MediaPlayer()
//                it.player.setOnCompletionListener { tt ->
//                    tt.start()
//                }
//
//                val effect = DataProvider.effectsMap[it.effectSet.effectUid]!!
//                val dest = File(AppProvider.mContext.filesDir, "effect_${effect.getFileName()}")
//                it.player.setDataSource(dest.absolutePath)
            it.player.start()
//            }
        }

        playing = true
    }

    fun stop() {
        data.forEach {
            if (it.player.isPlaying) {
                it.player.pause()
            }
        }

        playing = false
    }

    fun set(es: ArrayList<EffectSet>) {
        clear()
        es.forEach {
            add(it.effectUid, it.level)
        }
        PVM.effectsChangedObserver.postValue(PVM.effectsChangedObserver.value!! + 1)
    }

    fun setVolume(vol: Int) {
        data.forEach {
            val v = vol * it.effectSet.level / 1500f
            it.player.setVolume(v, v)
        }
    }

    fun save() {
        val editor = AppProvider.preferences.edit()
        val d = ArrayList<EffectSet>()
        data.forEach {
            d.add(it.effectSet)
        }
        editor.putString(
            "effect_${DataProvider.sounds[PVM.soundPos.value!!].uid}",
            AppProvider.gson.toJson(d)
        )
        editor.apply()
    }

    fun changeVolume(pos: Int, level: Int, save: Boolean = false) {
        data[pos].effectSet.level = level
        val v = PVM.mediaVolume.value!! * level / 1500f
        data[pos].player.setVolume(v, v)
        if (save) {
            save()
        }
    }

    fun removeAt(pos: Int) {
        try {
            data[pos].player.reset()
            data[pos].player.release()
            data.removeAt(pos)
//            save()
        } catch (th: Throwable) {
            th.printStackTrace()
        }
    }
}