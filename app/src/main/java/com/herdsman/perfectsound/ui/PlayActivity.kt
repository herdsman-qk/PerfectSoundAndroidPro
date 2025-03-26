/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 5/8/2023
 */

package com.herdsman.perfectsound.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herdsman.perfectsound.KeEIDB
import com.herdsman.perfectsound.R
import com.herdsman.perfectsound.databinding.ActivityPlayBinding
import com.herdsman.perfectsound.databinding.EffectAdapterLayoutBinding
import com.herdsman.perfectsound.datamodel.EffectPlayerSet
import com.herdsman.perfectsound.datamodel.MusicRepeatMode
import com.herdsman.perfectsound.datamodel.PlayState
import com.herdsman.perfectsound.provider.AppProvider
import com.herdsman.perfectsound.provider.DataProvider
import com.herdsman.perfectsound.provider.PVM
import com.herdsman.perfectsound.utils.Utils
import java.util.*
import kotlin.math.abs

class PlayActivity : AppCompatActivity() {
    companion object {
        const val TAG = "PlayActivity"
    }

    lateinit var binding: ActivityPlayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAll()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAll() {
        initListener()


        PVM.soundPos.observeForever {
            if (it != -1) {
                val sound = DataProvider.sounds[it]
                binding.titleView.text = sound.title
                binding.backView.setImageBitmap(KeEIDB.bitmap(sound.image))

                effectAdapter?.notifyDataSetChanged()

                if (sound.isFav()) {
                    binding.favBtn.text = getString(R.string.la_fav)
                } else {
                    binding.favBtn.text = getString(R.string.la_not_fav)
                }
            }
        }

        PVM.musicPos.observeForever {
            if (it != -1) {
                val music = DataProvider.musics[it]
                binding.musicTitleView.text = music.title
            }
        }

        PVM.musicPlayingState.observeForever {
            if (it == PlayState.PLAYING) {
                binding.playAndPauseMusicBtn.text = getString(R.string.la_pause)
            } else {
                binding.playAndPauseMusicBtn.text = getString(R.string.la_play)
            }
        }

        PVM.effectsChangedObserver.observeForever {
            initRecyclerView()
        }

        PVM.musicRepeatMode.observeForever {
            when (it) {
                MusicRepeatMode.NORMAL -> {
                    binding.repeatModeBtn.setImageResource(R.drawable.widget_btn_repeat_normal)
                }
                MusicRepeatMode.SINGLE -> {
                    binding.repeatModeBtn.setImageResource(R.drawable.widget_btn_repeat_once)
                }
                else -> {
                    binding.repeatModeBtn.setImageResource(R.drawable.widget_btn_random)
                }
            }
        }

        PVM.soundPlayingState.observeForever {
            if (it == PlayState.PLAYING) {
                binding.pauseAndPlayBtn.text = getString(R.string.la_pause)
                binding.timerRippleView.startRippleAnimation()
                binding.backView.resume()
            } else {
                binding.pauseAndPlayBtn.text = getString(R.string.la_play)
                binding.timerRippleView.stopRippleAnimation()
                binding.backView.pause()
            }
        }

        PVM.remainedTime.observeForever {
            if (it == -1) {
                binding.timeView.text = "Timer"
            } else {
                binding.timeView.text =
                    String.format("%02d:%02d:%02d", it / 3600, (it % 3600) / 60, it % 60)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.backView.pause()
        binding.timerRippleView.stopRippleAnimation()
    }

    override fun onResume() {
        super.onResume()
        if (PVM.soundPlayingState.value!! == PlayState.PLAYING) {
            binding.backView.resume()
            binding.timerRippleView.startRippleAnimation()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        binding.goBackBtn.callOnClick()
    }

    private fun initListener() {
        binding.timeView.setOnClickListener {
            startActivity(Intent(this, SetTimerActivity::class.java))
        }

        binding.goBackBtn.setOnClickListener {

            if (intent.hasExtra("EXTRA_OPENED_FROM_NOTIFICATION")) {
                val intent = Intent(baseContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
            finish()

        }

        binding.musicLayout.setOnClickListener {
            startActivity(Intent(this, MusicActivity::class.java))
        }

        binding.prevSoundBtn.setOnClickListener {
            PVM.soundPos.postValue(
                if (PVM.soundPos.value == 0) {
                    DataProvider.sounds.size - 1
                } else {
                    PVM.soundPos.value!! - 1
                }
            )
        }

        binding.nextSoundBtn.setOnClickListener {
            PVM.setSoundPos(
                if (PVM.soundPos.value == DataProvider.sounds.size - 1) {
                    0
                } else {
                    PVM.soundPos.value!! + 1
                }
            )
        }

        binding.systemVolumeSeekBar.max =
            AppProvider.provideAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        binding.systemVolumeSeekBar.progress =
            AppProvider.provideAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC)
        binding.systemVolumeText.text =
            AppProvider.provideAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC).toString()
        binding.systemVolumeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (AppProvider.provideAudioManager()
                        .getStreamVolume(AudioManager.STREAM_MUSIC) != p1
                ) {
                    AppProvider.provideAudioManager()
                        .setStreamVolume(AudioManager.STREAM_MUSIC, p1, 0)
                    binding.systemVolumeText.text = p1.toString()
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        binding.mediaVolumeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.mediaVolumeText.text = p1.toString()

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                PVM.mediaVolume.postValue(p0!!.progress)
            }

        })

//        binding.playAndPauseMusicBtn.text = getString(
//            if (PVM.musicPos.value != -1 && PVM.musicPlayingState.value == PlayState.PLAYING) {
//                R.string.la_pause
//            } else {
//                R.string.la_play
//            }
//        )

        binding.playAndPauseMusicBtn.setOnClickListener {
            if (PVM.musicPos.value != -1) {
                if (PVM.musicPlayingState.value == PlayState.STOPPED) {
                    PVM.musicPlayingState.postValue(PlayState.PLAYING)
                } else {
                    PVM.musicPlayingState.postValue(PlayState.STOPPED)
                }
            }
        }

        binding.repeatModeBtn.setOnClickListener {
            when (PVM.musicRepeatMode.value) {
                MusicRepeatMode.NORMAL -> {
                    PVM.setMusicRepeatMode(MusicRepeatMode.RANDOM)
                    Toast.makeText(baseContext, "Random", Toast.LENGTH_SHORT).show()
                }
                MusicRepeatMode.RANDOM -> {
                    PVM.setMusicRepeatMode(MusicRepeatMode.SINGLE)
                    Toast.makeText(baseContext, "Single", Toast.LENGTH_SHORT).show()
                }
                MusicRepeatMode.SINGLE -> {
                    PVM.setMusicRepeatMode(MusicRepeatMode.NORMAL)
                    Toast.makeText(baseContext, "Normal", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        binding.nextMusicBtn.setOnClickListener {
            with(PVM.musicRepeatMode.value!!) {
                if (this == MusicRepeatMode.RANDOM) {
                    val r = Random()
                    var p: Int
                    do {
                        p = abs(r.nextInt()) % DataProvider.musics.size
                    } while (p == PVM.musicPos.value)
                    PVM.setMusicPos(p)
                } else {
                    if (PVM.musicPos.value == DataProvider.musics.size - 1) {
                        PVM.setMusicPos(0)
                    } else {
                        PVM.setMusicPos(PVM.musicPos.value!! + 1)
                    }
                }
            }
        }

        binding.prevMusicBtn.setOnClickListener {
            with(PVM.musicRepeatMode.value!!) {
                if (this == MusicRepeatMode.RANDOM) {
                    val r = Random()
                    var p: Int
                    do {
                        p = abs(r.nextInt()) % DataProvider.musics.size
                    } while (p == PVM.musicPos.value)
                    PVM.setMusicPos(p)
                } else {
                    if (PVM.musicPos.value == 0) {
                        PVM.setMusicPos(DataProvider.musics.size - 1)
                    } else {
                        PVM.setMusicPos(PVM.musicPos.value!! - 1)
                    }
                }
            }
        }

        binding.favBtn.setOnClickListener {
            val sound = DataProvider.sounds[PVM.soundPos.value!!]
            if (sound.isFav()) {
                binding.favBtn.text = getString(R.string.la_not_fav)
                sound.unregisterFav()
                Toast.makeText(baseContext, "Removed In FavList", Toast.LENGTH_SHORT).show()
            } else {
                binding.favBtn.text = getString(R.string.la_fav)
                sound.registerFav()
                Toast.makeText(baseContext, "Added In FavList", Toast.LENGTH_SHORT).show()
            }
        }

        binding.pauseAndPlayBtn.setOnClickListener {
            if (PVM.soundPlayingState.value == PlayState.PLAYING) {
                PVM.soundPlayingState.postValue(PlayState.STOPPED)
            } else {
                PVM.soundPlayingState.postValue(PlayState.PLAYING)
            }
        }
    }

    private var effectAdapter: EffectAdapter? = null

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        val cnt = AppProvider.effectPlayer.data.size + 1

        val glm = GridLayoutManager(
            this,
            if (cnt >= 4) {
                4
            } else {
                cnt
            }
        )

        binding.recyclerView.layoutManager = glm

        if (effectAdapter != null) {
            effectAdapter?.notifyDataSetChanged()
        } else {
            effectAdapter = EffectAdapter(object : EffectAdapter.ItemClick {
                override fun invoke() {
                    startActivity(Intent(this@PlayActivity, EffectActivity::class.java))
                }
            })
            binding.recyclerView.adapter = effectAdapter
        }
    }


    private class EffectAdapter(var itemClick: ItemClick) :
        RecyclerView.Adapter<EffectAdapter.ViewHolder>() {
        interface ItemClick {
            operator fun invoke()
        }

        class ViewHolder(var binding: EffectAdapterLayoutBinding, var itemClick: ItemClick) :
            RecyclerView.ViewHolder(binding.root) {
            fun bindView(icon: String?, name: String, level: Int, cnt: Int, pos: Int) {
                Utils.setEffectIcon(itemView.context, binding.iconView, icon)
                binding.nameView.text = name
                with(binding.countView) {
                    if (cnt == 0) {
                        visibility = View.GONE
                    } else {
                        visibility = View.VISIBLE
                        text = "$cnt"
                    }
                }
                if (pos == -1) {
                    binding.root.setOnClickListener { itemClick() }
                    binding.seekBar.visibility = View.GONE
                } else {
                    binding.seekBar.visibility = View.VISIBLE
                    binding.seekBar.setOnSeekBarChangeListener(null)
                    binding.seekBar.progress = level
                    binding.seekBar.setOnSeekBarChangeListener(object :
                        SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                        }

                        override fun onStartTrackingTouch(p0: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(p0: SeekBar?) {
                            AppProvider.effectPlayer.changeVolume(pos, p0!!.progress, true)
                        }
                    })
                }
            }

            fun bindView(set: EffectPlayerSet, pos: Int) {
                val effect = DataProvider.effectsMap[set.effectSet.effectUid]!!
                bindView(effect.icon, effect.name, set.effectSet.level, 0, pos)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = EffectAdapterLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(view, itemClick)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == itemCount - 1) {
                holder.bindView(null, "Edit", 100, itemCount - 1, -1)
            } else {
                holder.bindView(AppProvider.effectPlayer.data[position], position)
            }
        }

        override fun getItemCount(): Int = AppProvider.effectPlayer.data.size + 1
    }
}