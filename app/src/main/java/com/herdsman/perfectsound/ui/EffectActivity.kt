/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 7/8/2023
 */

package com.herdsman.perfectsound.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herdsman.perfectsound.R
import com.herdsman.perfectsound.databinding.ActivityEffectBinding
import com.herdsman.perfectsound.databinding.EffectSelectedAdapterLayoutBinding
import com.herdsman.perfectsound.datamodel.Effect
import com.herdsman.perfectsound.provider.AppProvider
import com.herdsman.perfectsound.provider.DataProvider
import com.herdsman.perfectsound.provider.PVM
import com.herdsman.perfectsound.utils.Utils

class EffectActivity : AppCompatActivity() {
    companion object {
        const val TAG = "EffectActivity"
    }

    private lateinit var binding: ActivityEffectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEffectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAll()
    }

    private var selectedEffectAdapter: SelectedEffectAdapter? = null

    @SuppressLint("NotifyDataSetChanged")
    private fun initAll() {
        binding.closeBtn.setOnClickListener {
            finish()
        }
        binding.allEffectsRecyclerView.layoutManager = GridLayoutManager(this, 4)
        binding.allEffectsRecyclerView.setHasFixedSize(true)
        binding.allEffectsRecyclerView.adapter =
            AllEffectAdapter(object : AllEffectAdapter.ItemClick {
                override fun invoke(effect: Effect) {
                    if (AppProvider.effectPlayer.data.size >= 7) {
                        Toast.makeText(AppProvider.mContext, "Error!", Toast.LENGTH_SHORT).show()
                    } else {
                        AppProvider.effectPlayer.add(effect.uid, 50)
                        PVM.changedEffect()
                    }
                }
            })


        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.HORIZONTAL
        binding.selectedEffectsRecyclerview.layoutManager = llm
        binding.selectedEffectsRecyclerview.itemAnimator = DefaultItemAnimator()

        PVM.effectsChangedObserver.observeForever {
            selectedEffectAdapter?.notifyDataSetChanged()
        }

        selectedEffectAdapter = SelectedEffectAdapter()
        binding.selectedEffectsRecyclerview.adapter = selectedEffectAdapter


        binding.applyBtn.setOnClickListener {
            AppProvider.effectPlayer.save()
            finish()
        }

        binding.closeBtn.setOnClickListener {
            AppProvider.effectPlayer.set(DataProvider.sounds[PVM.soundPos.value!!].effect())
            finish()
        }


    }

    override fun onBackPressed() {
//        super.onBackPressed()
        binding.closeBtn.callOnClick()
    }


    private class AllEffectAdapter(var itemClick: ItemClick) :
        RecyclerView.Adapter<AllEffectAdapter.ViewHolder>() {
        interface ItemClick {
            operator fun invoke(effect: Effect)
        }

        class ViewHolder(view: View, var itemClick: ItemClick) : RecyclerView.ViewHolder(view) {
            fun bindView(effect: Effect) {
                with(effect) {
                    Utils.setEffectIcon(
                        itemView.context,
                        itemView.findViewById(R.id.iconView),
                        icon
                    )
                    itemView.findViewById<TextView>(R.id.nameView).text = name
                    itemView.setOnClickListener { itemClick(this) }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.effect_all_adapter_layout, parent, false)
            return ViewHolder(view, itemClick)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindView(DataProvider.effects[position])
        }

        override fun getItemCount(): Int = DataProvider.effects.size
    }

    private class SelectedEffectAdapter :
        RecyclerView.Adapter<SelectedEffectAdapter.ViewHolder>() {


        class ViewHolder(
            var binding: EffectSelectedAdapterLayoutBinding,
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bindView(position: Int) {
                val effectSet = AppProvider.effectPlayer.data[position].effectSet
                with(DataProvider.effectsMap[effectSet.effectUid]!!) {
                    Utils.setEffectIcon(
                        itemView.context,
                        binding.iconView,
                        icon
                    )
                    binding.nameView.text = name

                    binding.seekBar.setOnSeekBarChangeListener(null)
                    binding.seekBar.progress = effectSet.level
                    binding.seekBar.setOnSeekBarChangeListener(object :
                        SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                        }

                        override fun onStartTrackingTouch(p0: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(p0: SeekBar?) {
                            AppProvider.effectPlayer.changeVolume(position, p0!!.progress)
                        }
                    })

                    binding.deleteBtn.setOnClickListener {
                        if (AppProvider.effectPlayer.data.size == 1) {
                            Toast.makeText(AppProvider.mContext, "Error!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            AppProvider.effectPlayer.removeAt(position)
                            PVM.changedEffect()
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = EffectSelectedAdapterLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindView(position)
        }

        override fun getItemCount(): Int = AppProvider.effectPlayer.data.size
    }
}