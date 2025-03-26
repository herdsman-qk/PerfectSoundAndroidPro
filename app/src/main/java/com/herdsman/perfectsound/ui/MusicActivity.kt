/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 11/8/2023
 */

package com.herdsman.perfectsound.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herdsman.perfectsound.R
import com.herdsman.perfectsound.databinding.ActivityMusicBinding
import com.herdsman.perfectsound.databinding.MusicAdaperLayoutBinding
import com.herdsman.perfectsound.datamodel.Music
import com.herdsman.perfectsound.datamodel.MusicRepeatMode
import com.herdsman.perfectsound.datamodel.PlayState
import com.herdsman.perfectsound.provider.AppProvider
import com.herdsman.perfectsound.provider.DataProvider
import com.herdsman.perfectsound.provider.PVM
import com.herdsman.perfectsound.utils.Utils
import com.nineoldandroids.animation.Animator
import com.nineoldandroids.animation.ObjectAnimator
import java.util.*
import kotlin.math.abs

class MusicActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MusicActivity"
    }

    lateinit var binding: ActivityMusicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initAll()
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun initAll() {
        initRecyclerView()

        PVM.musicPos.observeForever {
            adapter.notifyDataSetChanged()

            if (it != -1) {
                val music = DataProvider.musics[it]
                binding.titleView.text = music.title
            }
        }
        PVM.musicDuration.observeForever {
            binding.totalDurationText.text = Utils.getTimeString(it)
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
//                Log.d(TAG, "initAll: MusicPlayer seeking...")
                if (AppProvider.musicPlayer.isPlaying) {
                    val c = AppProvider.musicPlayer.currentPosition
                    binding.seekBar.progress =
                        c * 1000 / AppProvider.musicPlayer.duration
                    runOnUiThread {
                        binding.currentDurationText.text = Utils.getTimeString(c)
                    }
                }
            }
        }, 500, 500)


        binding.goBackBtn.setOnClickListener {
            finish()
        }

//        binding.playAndPauseMusicBtn.text = getString(
//            if (PVM.musicPlayingState.value == PlayState.PLAYING) {
//                R.string.la_play
//            } else {
//                R.string.la_pause
//            }
//        )


        PVM.musicPlayingState.observeForever {
            if (PVM.musicPos.value != -1 && it == PlayState.PLAYING) {
                binding.playAndPauseMusicBtn.text = getString(R.string.la_pause)
            } else {
                binding.playAndPauseMusicBtn.text = getString(R.string.la_play)
            }
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

        binding.playAndPauseMusicBtn.setOnClickListener {
            if (PVM.musicPos.value != -1) {
                if (PVM.musicPlayingState.value == PlayState.STOPPED) {
                    PVM.musicPlayingState.postValue(PlayState.PLAYING)
                } else {
                    PVM.musicPlayingState.postValue(PlayState.STOPPED)
                }
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                AppProvider.musicPlayer
                    .seekTo(AppProvider.musicPlayer.duration * p0!!.progress / 1000)
            }

        })


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

        binding.volumeSeekBar.progress = PVM.musicVolume.value!!
        binding.volumeText.text = PVM.musicVolume.value!!.toString()

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.volumeText.text = p1.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                PVM.musicVolume.postValue(p0!!.progress)
            }
        })

        binding.volumeCloseBtn.setOnClickListener {
            val anim = ObjectAnimator.ofFloat(
                binding.volumeLayout, "translationY",
                0f,
                binding.volumeLayout.height.toFloat()
            )
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    binding.volumeLayout.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })
            anim.duration = 1000
            anim.start()
        }

        binding.moreBtn.setOnClickListener {
            val anim = ObjectAnimator.ofFloat(
                binding.volumeLayout,
                "translationY",
                binding.volumeLayout.height.toFloat(),
                0f
            )
            binding.volumeLayout.visibility = View.VISIBLE
            anim.duration = 1000
            anim.start()
        }

        binding.volumeLayout.setOnClickListener {

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
    }

    private val adapter = MusicAdapter()

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    class MusicAdapter() : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

        class ViewHolder(var binding: MusicAdaperLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bindView(music: Music, pos: Int) {
                binding.titleView.text = music.title
                val c = if (PVM.musicPos.value != -1 && pos == PVM.musicPos.value) {
                    binding.playingView.play()
                    ContextCompat.getColor(binding.root.context, R.color.kee_red)
                } else {
                    binding.playingView.stop()
                    Color.WHITE
                }
                binding.iconView.setTextColor(c)
                binding.titleView.setTextColor(c)

                binding.root.setOnClickListener {
                    PVM.setMusicPos(pos)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                MusicAdaperLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindView(DataProvider.musics[position], position)
        }

        override fun getItemCount(): Int = DataProvider.musics.size
    }
}