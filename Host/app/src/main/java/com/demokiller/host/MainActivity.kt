package com.demokiller.host

import android.os.Bundle
import android.util.Log
import com.demokiller.host.resource.PluginManager
import com.demokiller.library.UIinterface
import dalvik.system.DexClassLoader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {
    private var classLoader: DexClassLoader? = null
    private var lib: UIinterface? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var filePath = "/data/data/com.demokiller.host/cache/app-release-unsigned.apk"
        filePath = "/mnt/usb/sda1/app-release-unsigned.apk"
        val file = File(filePath)
        if (!file.exists()) filePath = "/mnt/usb/sdb1/app-release-unsigned.apk"
        val releasePath = cacheDir.absolutePath
        Log.d("wzh", filePath)
        Log.d("wzh", releasePath)
        Log.d("wzh", getClassLoader().toString())
        classLoader = DexClassLoader(filePath, releasePath, null, getClassLoader())
        PluginManager.setContext(this)
        PluginManager.loadResources(filePath)
        root_layout.setBackgroundResource(PluginManager.getResourceID("background", "drawable"))
        //setBackground();
    }

    /**
     * 动态调用插件包方法
     */
    private fun setBackground() {
        try {
            val clazz = classLoader?.loadClass(PluginManager.pluginPackageName +
                    PluginManager.pluginClassName)
            lib = clazz?.newInstance() as UIinterface
            root_layout.background = lib?.getBackground(this)
        } catch (e: Exception) {
            Log.i("wzh", "error:" + Log.getStackTraceString(e))
        }
    }
}