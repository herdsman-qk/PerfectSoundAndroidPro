/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 5/8/2023
 */

package com.herdsman.perfectsound.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.herdsman.perfectsound.R
import com.herdsman.perfectsound.databinding.ActivitySetTimerBinding
import com.herdsman.perfectsound.provider.PVM

class SetTimerActivity : AppCompatActivity() {

    val BTN_IDS = listOf(
        R.id.time_15,
        R.id.time_20,
        R.id.time_30,
        R.id.time_40,
        R.id.time_60,
        R.id.time_120,
        R.id.time_off
    )
    val VALUES = listOf(15 * 60, 20 * 60, 30 * 60, 40 * 60, 60 * 60, 120 * 60, -1)
    lateinit var binding: ActivitySetTimerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.customBtn.setOnClickListener {
            startActivity(Intent(this, TimeCustomActivity::class.java))
            finish()
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.checkBtn.setOnClickListener {
            if (currentValue != -2) {
                PVM.remainedTime.postValue(currentValue)
                PVM.recentSelectedTime = currentValue
//                Toast.makeText(this, "$currentValue", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Not Selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.time15.setOnClickListener { itemClicked(0) }
        binding.time20.setOnClickListener { itemClicked(1) }
        binding.time30.setOnClickListener { itemClicked(2) }
        binding.time40.setOnClickListener { itemClicked(3) }
        binding.time60.setOnClickListener { itemClicked(4) }
        binding.time120.setOnClickListener { itemClicked(5) }
        binding.timeOff.setOnClickListener { itemClicked(6) }
    }

    private var currentValue = -2

    private fun itemClicked(pos: Int) {
        BTN_IDS.forEach {
            findViewById<TextView>(it).setTextColor(Color.WHITE)
        }
        currentValue = VALUES[pos]
        findViewById<TextView>(BTN_IDS[pos]).setTextColor(Color.parseColor("#F2A602"))
    }
}