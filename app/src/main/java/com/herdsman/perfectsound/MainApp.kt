/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 3/8/2023
 */

package com.herdsman.perfectsound

import android.app.Application
import com.herdsman.perfectsound.provider.AppProvider
import com.herdsman.perfectsound.provider.DataProvider
import com.herdsman.perfectsound.utils.DisplayUtils

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        KeEIDB.setup(this)
        DisplayUtils.resetDensity(this)
        DataProvider.loadData()
        AppProvider.init(applicationContext)

    }
}