package com.demokiller.host.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.demokiller.host.R
import com.demokiller.host.utils.ResourceUtils


private val createdChannelIds: HashSet<String?> = HashSet()
fun ensureChannelCreated(manager: NotificationManagerCompat, channelId: String?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Log.d("NotificationChannelFactory", "ensureChannelCreated channelId=$channelId")
        if (createdChannelIds.contains(channelId)) {
            return
        }

        val channel = createChannelByChannelId(channelId)
        if (channel != null) {
            manager.createNotificationChannel(channel)
            createdChannelIds.add(channelId)
        } else {
            throw RuntimeException("Invalid channelId: $channelId. Have you add create-logic in createChannelByChannelId?")
        }
    }
}

@SuppressLint("WrongConstant")
@RequiresApi(Build.VERSION_CODES.O)
internal fun createChannelByChannelId(channelId: String?): NotificationChannel? {
    if (channelId == null) {
        return null
    }
    Log.d("NotificationChannelFactory", "createChannelByChannelId: $channelId")
    val color = Color.GREEN
    when (channelId) {
        ResourceUtils.getString(R.string.channel_message) -> {
            val chatChannel = NotificationChannel(
                channelId,
                ResourceUtils.getString(R.string.notify_channel_name_message),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            chatChannel.enableLights(true)
            chatChannel.lightColor = color
            chatChannel.enableVibration(true)
            chatChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            chatChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            chatChannel.setShowBadge(false)
            return chatChannel
        }

        else -> {
            return null
        }
    }
}