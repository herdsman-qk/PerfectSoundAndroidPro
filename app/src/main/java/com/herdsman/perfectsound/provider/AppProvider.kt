/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 13/8/2023
 */

package com.herdsman.perfectsound.provider

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.preference.PreferenceManager
import android.util.Log
import com.herdsman.perfectsound.KeEIDB
import com.herdsman.perfectsound.datamodel.MusicRepeatMode
import com.herdsman.perfectsound.datamodel.PlayState
import com.herdsman.perfectsound.notification.MainService
import com.google.gson.Gson
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*
import kotlin.math.abs

@SuppressLint("StaticFieldLeak")
object AppProvider {
    const val TAG = "AppProvider"
    lateinit var preferences: SharedPreferences
    private var audioManager: AudioManager? = null
    lateinit var mContext: Context
    var musicPlayer = MediaPlayer()
    var effectPlayer = EffectPlayer()

    private var tmpMusicPosition = 0

    //    var mediaPlayers = HashMap<String, MediaPlayer>()
    val gson = Gson()


    fun init(context: Context) {
        mContext = context
        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        PVM.musicPlayingState.observeForever {
            if (it == PlayState.STOPPED) {
                Log.d(TAG, "init: Music pause")
                tmpMusicPosition = musicPlayer.currentPosition
                musicPlayer.pause()
            } else {
                Log.d(TAG, "init: Music play")
                val dest = File(mContext.filesDir, "music.mp3")
                musicPlayer.reset()
                musicPlayer.setDataSource(dest.absolutePath)
                musicPlayer.prepare()
                musicPlayer.start()
                musicPlayer.seekTo(tmpMusicPosition)
            }
        }

        PVM.musicPos.observeForever {
            if (it != -1) {
                val music = DataProvider.musics[it]
                val dest = File(mContext.filesDir, "music.mp3")
                musicPlayer.reset()
                FileUtils.copyInputStreamToFile(KeEIDB.inputStream(music.path), dest)
                musicPlayer.setDataSource(dest.absolutePath)
                musicPlayer.prepare()
                PVM.musicDuration.postValue(musicPlayer.duration)
                if (PVM.musicPlayingState.value == PlayState.STOPPED) {
                    PVM.musicPlayingState.postValue(PlayState.PLAYING)
                }
                musicPlayer.start()
                Log.d(TAG, "init: play music <${music.path}>")
            }
        }

        musicPlayer.setOnCompletionListener {
            when (PVM.musicRepeatMode.value) {
                MusicRepeatMode.NORMAL -> {
                    if (PVM.musicPos.value == DataProvider.musics.size - 1) {
                        PVM.setMusicPos(0)
                    } else {
                        PVM.setMusicPos(PVM.musicPos.value!! + 1)
                    }
                }
                MusicRepeatMode.RANDOM -> {
                    val r = Random()
                    var p: Int
                    do {
                        p = abs(r.nextInt()) % DataProvider.musics.size
                    } while (p == PVM.musicPos.value)
                    PVM.setMusicPos(p)
                }
                MusicRepeatMode.SINGLE -> {
                    musicPlayer.start()
                }
                else -> {}
            }
            Log.d(TAG, "init: Music replaying.......")
        }


        PVM.mediaVolume.observeForever {
            val v = it * PVM.musicVolume.value!! / 225f
            musicPlayer.setVolume(v, v)
            effectPlayer.setVolume(it)
        }

        PVM.musicVolume.observeForever {
            val v = it * PVM.mediaVolume.value!! / 255f
            musicPlayer.setVolume(v, v)
        }

        PVM.soundPos.observeForever {
            if (it != -1) {
                effectPlayer.set(DataProvider.sounds[it].effect())
            }
        }

        PVM.soundPlayingState.observeForever {
            if (it == PlayState.PLAYING) {
                effectPlayer.play()
//                mContext.startService(Intent(mContext, MainService::class.java))
                Log.d(TAG, "init: Effect Player is plyaing.....")
            } else {
                effectPlayer.stop()
                Log.d(TAG, "init: Effect Player was stopped.")
            }
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (PVM.remainedTime.value!! != -1) {
                    if (PVM.soundPlayingState.value!! == PlayState.PLAYING) {
                        if (PVM.remainedTime.value!! > 0) {
                            PVM.remainedTime.postValue(PVM.remainedTime.value!! - 1)
                        } else {
                            PVM.soundPlayingState.postValue(PlayState.STOPPED)
                            PVM.remainedTime.postValue(0)
                        }
                    }
                }
            }
        }, 1000, 1000)
    }


    fun provideAudioManager(): AudioManager {
        if (audioManager == null) {
            audioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }
        return audioManager!!
    }
}