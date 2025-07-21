package com.demokiller.host.notification;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.demokiller.host.utils.AppUtils;


/**
 * Android版本的升级带来了Notification很多的兼容问题
 * 这里为了方便Notification以后的变化兼容做了一些封装
 * 把Notification的获取和展示、取消等逻辑都收归到这里
 * 其他地方尽量不要自己去创建Notification, 统一走这里
 */

public final class AppNotificationManager {

    private static final String TAG = "AppNotificationManager";
    private static IChannel CHANNEL;

    /**
     * 系统服务都是在SystemServiceRegistry中静态注册, 全局只有一份, 然后通过context.getSystemService(Context.NOTIFICATION_SERVICE)获取
     * 我们这里可以缓存一下, 免得每次都getSystemService()
     */
    private NotificationManagerCompat mNotificationManagerCompat; //Notification 相关
    private Context mContext;
    private IOnNotifyActionListener mOnNotifyActionListener;

    private static class SingletonHolder {
        private static AppNotificationManager sInstance = new AppNotificationManager();
    }

    public static AppNotificationManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public static void setChannel(IChannel channel) {
        CHANNEL = channel;
    }

    private AppNotificationManager() {
        init();
    }

    private void init() {
        mContext = AppUtils.getContext();
        mNotificationManagerCompat = NotificationManagerCompat.from(mContext);
    }

    /**
     * 获取NotificationCompat.Builder
     *
     * @param channelId
     * @return
     */
    public NotificationCompat.Builder getNotificationBuilder(String channelId) {
        if (CHANNEL != null) {
            CHANNEL.createChannel(mNotificationManagerCompat, channelId);
        }
        return new NotificationCompat.Builder(mContext, channelId);
    }

    /**
     * show出一个通知
     *
     * @param id
     * @param notification
     */
    public void notify(int id, Notification notification) {
        notify(null, id, notification);
    }

    /**
     * show出一个通知
     *
     * @param tag
     * @param id
     * @param notification
     */
    public void notify(String tag, int id, Notification notification) {
        try {
            if (ActivityCompat.checkSelfPermission(AppUtils.getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mNotificationManagerCompat.notify(tag, id, notification);
        } catch (Exception e) {
            Log.e(TAG, "notify notification with listener caught an exception.");
        }
    }

    /**
     * 仅用于亮屏时重复弹多一次通知，不会回调{@link #mOnNotifyActionListener}
     */
    public void notifyWithoutListener(String tag, int id, Notification notification) {
        try {
            if (ActivityCompat.checkSelfPermission(AppUtils.getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mNotificationManagerCompat.notify(tag, id, notification);
        } catch (Exception e) {
            Log.e(TAG, "notify notification without listener caught an exception.");
        }
    }

    /**
     * 取消一个通知
     *
     * @param id
     */
    public void cancel(int id) {
        cancel(null, id);
    }

    /**
     * 取消一个通知
     *
     * @param id
     * @param tag
     */
    public void cancel(String tag, int id) {
        try {
            mNotificationManagerCompat.cancel(tag, id);
            if (mOnNotifyActionListener != null) {
                mOnNotifyActionListener.onCancelOne(id, tag);
            }
        } catch (Exception e) {
            Log.e(TAG, "cancel notification caught an exception.");
        }
    }

    /**
     * 取消所有通知
     */
    public void cancelAll() {
        try {
            mNotificationManagerCompat.cancelAll();
            if (mOnNotifyActionListener != null) {
                mOnNotifyActionListener.onCancelAll();
            }
        } catch (Exception e) {
            Log.e(TAG, "cancel all notification caught an exception.");
        }
    }

    public void setOnNotifyActionListener(IOnNotifyActionListener onNotifyActionListener) {
        mOnNotifyActionListener = onNotifyActionListener;
    }

    public interface IOnNotifyActionListener {

        void onCancelOne(int notifyId, String notifyTag);

        void onCancelAll();
    }

    public interface IChannel {
        void createChannel(NotificationManagerCompat manager, String channelId);
    }

}
