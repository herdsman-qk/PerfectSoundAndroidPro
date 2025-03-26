/*
 * Copyright (c) 2023. YeHwi Kim (KeE)
 * This source code was created only for PerfectSoundsProject.
 * Author: YeHwi Kim(KeE)
 * Created Date: 16/8/2023
 */
package com.herdsman.perfectsound.notification

import android.app.*
import android.app.Notification.MediaStyle
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.herdsman.perfectsound.R
import com.herdsman.perfectsound.datamodel.PlayState
import com.herdsman.perfectsound.provider.PVM
import com.herdsman.perfectsound.ui.PlayActivity

class MainService : Service() {
    companion object {
        const val TAG = "MainService"
    }

    private var notificationManager: NotificationManager? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

        updateNotification()
    }

    override fun onCreate() {
        super.onCreate()

        PVM.appRunning.observeForever {
            if (it == 0) {
                Log.d(TAG, "onCreate: Finished")
                PVM.soundPlayingState.postValue(PlayState.STOPPED)
                PVM.musicPlayingState.postValue(PlayState.STOPPED)
                appRemoved = true
                stopForeground(true)
//                baseContext.stopService(Intent(baseContext, MainService::class.java))
            }
        }

        PVM.soundPlayingState.observeForever {
            updateNotification()
        }
        PVM.remainedTime.observeForever {
            updateNotification()
        }

        notificationManager =
            baseContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val filter = IntentFilter()
        filter.addAction(NotificationActionsReceiver.ACTION_PLAY)
        filter.addAction(NotificationActionsReceiver.ACTION_PAUSE)
        filter.addAction(NotificationActionsReceiver.ACTION_STOP)
        filter.addAction(NotificationActionsReceiver.ACTION_TIMER_CANCEL)
        filter.addAction(NotificationActionsReceiver.ACTION_NOTIFICATION_DELETED)

        baseContext.registerReceiver(NotificationActionsReceiver(), filter)

        updateNotification()
    }

    private var notificationCreated = false
    private var appRemoved = false
    private fun updateNotification() {
        if (appRemoved) {
            stopForeground(true)
        } else {
            if (notificationManager == null) return
            if (!notificationCreated) {
                startForeground(100, createNotificationBuilder().build())
                notificationCreated = true
            } else {
                notificationManager!!.notify(100, createNotificationBuilder().build())
            }
        }
    }

    private fun getStringByResourceId(@StringRes resourceId: Int): String {
        return baseContext.resources.getString(resourceId)
    }

    private fun configNotificationBuilder(builder: Notification.Builder) {
        if (PVM.soundPlayingState.value == PlayState.PLAYING) { //playing
            builder.setContentTitle(baseContext.getString(R.string.notification_sounds_playing))
            builder.addAction(
                R.drawable.ic_notification_pause,
                getStringByResourceId(R.string.notification_pause),
                NotificationActionsReceiver.getPauseBroadcastPendingIntent(
                    baseContext
                )
            )
            builder.setSmallIcon(R.drawable.ic_notification)
        } else {
            builder.setContentTitle(baseContext.getString(R.string.notification_sounds_paused))
            builder.addAction(
                R.drawable.ic_notification_play,
                getStringByResourceId(R.string.notification_play),
                NotificationActionsReceiver.getPlayBroadcastPendingIntent(
                    baseContext
                )
            )
            builder.setSmallIcon(R.drawable.ic_notification)
        }
        builder.addAction(
            R.drawable.ic_notification_stop,
            getStringByResourceId(R.string.notification_stop),
            NotificationActionsReceiver.getStopBroadcastPendingIntent(
                baseContext
            )
        )
        if (PVM.remainedTime.value != -1) { // TODO: 8/16/2023  timer ok
            builder.addAction(
                R.drawable.ic_notification_timer_cancel,
                getStringByResourceId(R.string.notification_cancel_timer),
                NotificationActionsReceiver.getTimerCancelBroadcastPendingIntent(
                    baseContext
                )
            )
            with(PVM.remainedTime.value!!) {
                builder.setContentText(
                    String.format(
                        "%02d:%02d:%02d",
                        this / 3600,
                        (this % 3600) / 60,
                        this % 60
                    )
                )
                if (this == 0) {
                    PVM.appRunning.postValue(0)
                }
            }
        } else {
            builder.addAction(
                R.drawable.ic_notification_timer,
                getStringByResourceId(R.string.notification_set_timer),
                NotificationActionsReceiver.getOpenTimerDialogPendingIntent(
                    baseContext
                )
            )
            builder.setContentText(getStringByResourceId(R.string.notification_timer_not_set))
        }
    }

    private fun createNotificationBuilder(): Notification.Builder {
        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("PerfectSoundId", "PerfectSoundName", NotificationManager.IMPORTANCE_LOW)
            notificationManager!!.createNotificationChannel(notificationChannel)
            Notification.Builder(baseContext, notificationChannel.id)
        } else {
            Notification.Builder(baseContext)
        }
        builder.setColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
        builder.style = MediaStyle()
        builder.setOngoing(true)
        builder.setAutoCancel(false)
        builder.setContentTitle(baseContext.getString(R.string.app_name))
        builder.setLargeIcon(
            BitmapFactory.decodeResource(
                baseContext.resources,
                R.drawable.app_icon
            )
        )
        configNotificationBuilder(builder)
        val intent = Intent(baseContext, PlayActivity::class.java)
        intent.putExtra("EXTRA_OPENED_FROM_NOTIFICATION", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        intent.action = ACTION_STARTED_FROM_NOTIFICATION
        builder.setContentIntent(
            PendingIntent.getActivity(
                baseContext,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        builder.setDeleteIntent(NotificationActionsReceiver.getBroadcastPendingIntent(baseContext))
        return builder
    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//    }
}