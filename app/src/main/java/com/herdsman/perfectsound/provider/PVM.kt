/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 13/8/2023
 */

package com.herdsman.perfectsound.provider

import androidx.lifecycle.MutableLiveData
import com.herdsman.perfectsound.datamodel.MusicRepeatMode
import com.herdsman.perfectsound.datamodel.PlayState

object PVM {
    val soundPos = MutableLiveData<Int>().apply {
        value = 0
    }
    val musicPos = MutableLiveData<Int>().apply {
        value = -1
    }

    val musicDuration = MutableLiveData<Int>().apply {
        value = 0
    }

    val musicPlayingState = MutableLiveData<PlayState>().apply {
        value = PlayState.STOPPED
    }

    val soundPlayingState = MutableLiveData<PlayState>().apply {
        value = PlayState.PLAYING
    }

    val appRunning = MutableLiveData<Int>().apply {
        value = 1
    }

    val remainedTime = MutableLiveData<Int>().apply {
        value = -1
    }

    val mediaVolume = MutableLiveData<Int>().apply {
        value = 15
    }

    val musicVolume = MutableLiveData<Int>().apply {
        value = 15
    }

    val effectsChangedObserver = MutableLiveData<Int>().apply {
        value = 0
    }

    val musicRepeatMode = MutableLiveData<MusicRepeatMode>().apply {
        value = MusicRepeatMode.NORMAL
    }

    var recentSelectedTime = 600


    fun setMusicPos(pos: Int) {
        musicPos.postValue(pos)
        val editor = AppProvider.preferences.edit()
        editor.putInt(Constants.MUSIC_POS, pos)
        editor.apply()
    }

    fun setSoundPos(pos: Int) {
        soundPos.postValue(pos)
        val editor = AppProvider.preferences.edit()
        editor.putInt(Constants.SOUND_POS, pos)
        editor.apply()
    }

    fun changedEffect() {
        effectsChangedObserver.postValue(effectsChangedObserver.value!! + 1)
    }

    fun setMusicRepeatMode(mode: MusicRepeatMode) {
        musicRepeatMode.postValue(mode)
        val editor = AppProvider.preferences.edit()
        editor.putString(Constants.MUSIC_REPEAT_MODE, mode.toString())
        editor.apply()
    }

}