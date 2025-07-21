package com.demokiller.host

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.demokiller.host.hook.HookUtil
import com.demokiller.host.notification.AppNotificationManager
import com.demokiller.host.utils.AppUtils
import com.demokiller.robustpatch.ChangeQuickRedirect
import com.demokiller.robustpatch.PatchesInfo
import com.demokiller.host.notification.ensureChannelCreated
import dalvik.system.DexClassLoader
import java.io.File

class MainApplication : Application() {
    private var mLoader: DexClassLoader? = null

    companion object{
        init {
            System.loadLibrary("native-lib")
        }
    }
    override fun onCreate() {
        super.onCreate()
        AppUtils.init(this)
        setupNotifyChannel()
    }

    override fun attachBaseContext(base: Context) {
        val isSucc = loadDex(base)
        if (isSucc) {
            patch()
        }
        super.attachBaseContext(base)
        HookUtil.hookPackageManager(base)
    }

    @SuppressLint("SdCardPath")
    private fun loadDex(ctx: Context): Boolean {
        val dexFile = File("/sdcard/patch.dex")
        if (!dexFile.exists()) {
            return false
        }
        try {
            val odexDir = File(ctx.filesDir.toString() + File.separator + "odex" + File.separator)
            if (!odexDir.exists()) {
                odexDir.mkdirs()
            }
            mLoader = DexClassLoader(dexFile.absolutePath, odexDir.absolutePath, null, ctx.classLoader)
            return true
        } catch (e: Throwable) {
        }
        return false
    }

    @SuppressLint("NewApi")
    private fun patch() {
        try {
            val patchInfoClazz = mLoader?.loadClass("com.demokiller.robustpatchimpl.PatchesInfoImpl")
            val patchInfo = patchInfoClazz?.newInstance() as PatchesInfo
            val infoList = patchInfo.getPatchedClassesInfo() ?: arrayListOf()
            for (info in infoList) {
                val redirectObj = mLoader?.loadClass(
                        info?.patchClassName)?.newInstance() as ChangeQuickRedirect
                val fixClass = mLoader?.loadClass(info?.fixClassName)
                val redirectF = fixClass?.getField("changeQuickRedirect")
                redirectF?.set(null, redirectObj)
            }
        } catch (e: Throwable) {
        }
    }

    /**
     * Oreo不用Priority了，用importance
     *
     * @see NotificationManager#IMPORTANCE_NONE 关闭通知
     * @see NotificationManager#IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
     * @see NotificationManager#IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
     * @see NotificationManager#IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
     * @see NotificationManager#IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
     */
    private fun setupNotifyChannel() {
        AppNotificationManager.setChannel(object : AppNotificationManager.IChannel {
            override fun createChannel(manager: NotificationManagerCompat?, channelId: String?) {
                manager?.let {
                    ensureChannelCreated(it, channelId)
                }
            }
        })
    }
}