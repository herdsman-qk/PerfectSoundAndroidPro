/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 3/8/2023
 */

package com.herdsman.perfectsound.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herdsman.perfectsound.KeEIDB
import com.herdsman.perfectsound.R
import com.herdsman.perfectsound.databinding.ActivityMainBinding
import com.herdsman.perfectsound.datamodel.PlayState
import com.herdsman.perfectsound.datamodel.Sound
import com.herdsman.perfectsound.notification.MainService
import com.herdsman.perfectsound.provider.DataProvider
import com.herdsman.perfectsound.provider.PVM
import com.herdsman.perfectsound.utils.DisplayUtils

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()


        PVM.soundPos.observeForever {
            if (it != -1) {
                binding.bottomLayout.visibility = View.VISIBLE
                val sound = DataProvider.sounds[it]
                binding.bottomTitleView.text = sound.title

                KeEIDB.putBitmap(
                    this, binding.bottomThumbView, sound.image
                )
            } else {
                binding.bottomLayout.visibility = View.GONE
            }
        }

        PVM.soundPlayingState.observeForever {
            if (it == PlayState.PLAYING) {
                binding.playBtn.text = getString(R.string.la_pause)
            } else {
                binding.playBtn.text = getString(R.string.la_play)
            }
        }

        binding.playBtn.setOnClickListener {
            if (PVM.soundPlayingState.value == PlayState.PLAYING) {
                PVM.soundPlayingState.postValue(PlayState.STOPPED)
            } else {
                PVM.soundPlayingState.postValue(PlayState.PLAYING)
            }
        }
//        startService(Intent(this, MainService::class.java))

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.FOREGROUND_SERVICE),
                300
            )
        } else {
//            startService(Intent(this, MainService::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 300) {
//            startService(Intent(this, MainService::class.java))
        }
    }

    private var soundCardAdapter: SoundCardAdapter? = null

    private fun initRecyclerView() {
        val glm = GridLayoutManager(this, 2)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = if (position == 0) {
                2
            } else {
                1
            }
        }
        binding.recyclerView.layoutManager = glm
        soundCardAdapter = SoundCardAdapter(object : SoundCardAdapter.ItemClick {
            override fun invoke(pos: Int, sound: Sound) {
                PVM.setSoundPos(pos)
                startActivity(Intent(this@MainActivity, PlayActivity::class.java))
            }
        })
        binding.recyclerView.adapter = soundCardAdapter

        binding.favBtn.setOnClickListener {
            if (soundCardAdapter!!.isFav) {
                binding.favBtn.text = getString(R.string.la_fav)
                soundCardAdapter!!.isFav = false
            } else {
                binding.favBtn.text = getString(R.string.la_not_fav)
                soundCardAdapter!!.isFav = true
            }
        }

    }

    class SoundCardAdapter(private var itemClick: ItemClick) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var items = ArrayList<Sound>()

        var isFav = false
            @SuppressLint("NotifyDataSetChanged")
            set(value) {
                field = value
                items.clear()
                if (value) {
                    DataProvider.sounds.forEach {
                        if (it.isFav()) {
                            items.add(it)
                        }
                    }
                } else {
                    items.addAll(DataProvider.sounds)
                }
                notifyDataSetChanged()
            }

        init {
            isFav = false
        }

        interface ItemClick {
            operator fun invoke(pos: Int, sound: Sound)
        }

        companion object {
            const val VIEW_TYPE_HEADER = 0
            const val VIEW_TYPE_CARD = 1
            const val TAG = "SoundCardAdapter"
        }

        class CardViewHolder(view: View, var itemClick: ItemClick) : RecyclerView.ViewHolder(view) {
            fun bindView(pos: Int, sound: Sound) {
                with(sound) {
                    KeEIDB.putBitmap(itemView.context, itemView.findViewById(R.id.imgView), image)
                    itemView.findViewById<TextView>(R.id.titleView).text = title
                }
                itemView.findViewById<View>(R.id.rippleView)
                    .setOnClickListener { itemClick(pos, sound) }
            }
        }

        class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemViewType(position: Int): Int = if (position == 0) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_CARD
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == VIEW_TYPE_HEADER) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sound_card_adapter_header_layout, parent, false)
                view.layoutParams.height = DisplayUtils.widthPixels * 195 / 480
                HeaderViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sound_card_adapter_card_layout, parent, false)
                view.layoutParams.height = DisplayUtils.heightPixels / 3
                CardViewHolder(view, itemClick)
            }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == VIEW_TYPE_CARD) {
                (holder as CardViewHolder).bindView(position - 1, items[position - 1])
            }
        }

        override fun getItemCount(): Int = items.size + 1
    }
}