/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 6/8/2023
 */

package com.herdsman.perfectsound.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.herdsman.perfectsound.databinding.ActivityTimeCustomBinding
import com.herdsman.perfectsound.provider.PVM

class TimeCustomActivity : AppCompatActivity() {
    lateinit var binding: ActivityTimeCustomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeCustomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23

        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 59

        binding.minutePicker.value = 20


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.minutePicker.textColor = Color.WHITE
            binding.hourPicker.textColor = Color.WHITE
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.checkBtn.setOnClickListener {
            val time = binding.hourPicker.value * 3600 + binding.minutePicker.value * 60
            PVM.remainedTime.postValue(time)
            PVM.recentSelectedTime = time
//            Toast.makeText(this, "$time", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}