package com.demokiller.host.notification

import android.app.PendingIntent
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.demokiller.host.R
import com.demokiller.host.utils.AppUtils
import com.demokiller.host.utils.string

object NotificationUtils {

    const val MESSAGE_ID = 100000001

    val MAIN_ACTION = AppUtils.getContext().packageName + ".action.mainactivity"

    fun createNotification() {
        val intent = Intent(MAIN_ACTION)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            AppUtils.getContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        // 添加自定义通知view
        val views = RemoteViews(AppUtils.getContext().packageName, R.layout.layout_notification)
        val builder = AppNotificationManager.getInstance()
            .getNotificationBuilder(R.string.channel_message.string())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setShowWhen(true)
            .setOngoing(true)
            .setAutoCancel(false)
            .setStyle(NotificationCompat.CallStyle.forIncomingCall(Person.Builder().setName("1213").build(),pendingIntent,pendingIntent))
            .build()
        AppNotificationManager.getInstance().notify(MESSAGE_ID, builder)
    }
}