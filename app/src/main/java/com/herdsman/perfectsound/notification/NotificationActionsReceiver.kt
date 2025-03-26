package com.herdsman.perfectsound.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.herdsman.perfectsound.datamodel.PlayState
import com.herdsman.perfectsound.provider.PVM
import com.herdsman.perfectsound.ui.SetTimerActivity

class NotificationActionsReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "NotificationActionsReceiver"

        const val ACTION_STARTED_FROM_NOTIFICATION =
            "com.com.herdsman.perfectsound.ACTION_STARTED_FROM_NOTIFICATION"

        const val ACTION_PLAY = "com.com.herdsman.perfectsound.ACTION_PLAY"
        const val ACTION_PAUSE = "com.com.herdsman.perfectsound.ACTION_PAUSE"
        const val ACTION_STOP = "com.com.herdsman.perfectsound.ACTION_STOP"
        const val ACTION_TIMER_CANCEL = "com.com.herdsman.perfectsound.ACTION_TIMER_CANCEL"
        const val ACTION_NOTIFICATION_DELETED =
            "com.com.herdsman.perfectsound.ACTION_NOTIFICATION_DELETED"

        fun getPauseBroadcastPendingIntent(context: Context): PendingIntent {
            return getBroadcastPendingIntent(context, ACTION_PAUSE, 2)
        }

        fun getPlayBroadcastPendingIntent(context: Context): PendingIntent {
            return getBroadcastPendingIntent(context, ACTION_PLAY, 1)
        }

        fun getStopBroadcastPendingIntent(context: Context): PendingIntent {
            return getBroadcastPendingIntent(context, ACTION_STOP, 3)
        }

        fun getTimerCancelBroadcastPendingIntent(context: Context): PendingIntent {
            return getBroadcastPendingIntent(context, ACTION_TIMER_CANCEL, 5)
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        fun getOpenTimerDialogPendingIntent(context: Context?): PendingIntent {
            val intent = Intent(context, SetTimerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            return PendingIntent.getActivity(context, 4, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }

        fun getBroadcastPendingIntent(context: Context): PendingIntent {
            return getBroadcastPendingIntent(context, ACTION_NOTIFICATION_DELETED, 7)
        }

        private fun getBroadcastPendingIntent(
            context: Context,
            action: String,
            requestCode: Int
        ): PendingIntent {
            val intent = Intent(action)
            intent.setClass(context, NotificationActionsReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action != null) {
            Log.d(TAG, "onReceive: ${intent.action}")
            if (intent.action == ACTION_PLAY) {
                PVM.soundPlayingState.postValue(PlayState.PLAYING)
            } else if (intent.action == ACTION_PAUSE) {
                PVM.soundPlayingState.postValue(PlayState.STOPPED)
            } else if (intent.action == ACTION_STOP) {
                PVM.appRunning.postValue(0)
            } else if (intent.action == ACTION_TIMER_CANCEL) {
                PVM.remainedTime.postValue(-1)
            } else if (intent.action == ACTION_NOTIFICATION_DELETED) {
                PVM.appRunning.postValue(0)
            }
        }
    }
}