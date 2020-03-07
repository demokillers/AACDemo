package com.demokiller.host

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.demokiller.robustpatch.ChangeQuickRedirect
import com.demokiller.robustpatch.PatchesInfo
import dalvik.system.DexClassLoader
import java.io.File

class MainApplication : Application() {
    private var mLoader: DexClassLoader? = null
    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context) {
        val isSucc = loadDex(base)
        if (isSucc) {
            patch()
        }
        super.attachBaseContext(base)
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
}