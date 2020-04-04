package com.demokiller.host.resource

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.util.Log
import java.util.*

object PluginManager {
    private val mPluginAsset: HashMap<String, AssetManager>
    private val mPluginResources: HashMap<String, Resources>
    private val mPluginTheme: HashMap<String, Theme>
    /**
     * 插件包名，例如 com.demokiller.plugin
     */
    private val mPluginPackageName: HashMap<String?, String>
    /**
     * 插件实现jar包方法的类名，例如 .utils.xxx
     */
    private val mPluginClassName: HashMap<String?, String>
    private var mCurrentApk: String? = null
    private var mContext: Context? = null
    private val test = ArrayList<String>()
    fun setContext(context: Context?) {
        mContext = context
    }

    /**
     * 使用AssetManager根据资源包位置加载资源
     *
     * @param apkPath
     */
    fun loadResources(apkPath: String) {
        try {
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, apkPath)
            mPluginAsset[apkPath] = assetManager
            mCurrentApk = apkPath
        } catch (e: Exception) {
            Log.d("demokiller", "============loadResource error")
            e.printStackTrace()
            return
        }
        val superRes = mContext!!.resources
        superRes.displayMetrics
        superRes.configuration
        mPluginResources[apkPath] = Resources(mPluginAsset[apkPath],
                superRes.displayMetrics, superRes.configuration)
        mPluginTheme[apkPath] = mPluginResources[apkPath]!!.newTheme()
        mPluginTheme[apkPath]!!.setTo(mContext!!.theme)
        getPackageInfo(apkPath)
    }

    fun getPackageInfo(apkFilepath: String?) {
        val pm = mContext?.packageManager
        var pkgInfo: PackageInfo? = null
        try {
            pkgInfo = pm?.getPackageArchiveInfo(apkFilepath,
                    PackageManager.GET_ACTIVITIES or
                            PackageManager.GET_SERVICES or
                            PackageManager.GET_META_DATA)
        } catch (e: Exception) { // should be something wrong with parse
            e.printStackTrace()
        }
        mPluginClassName[mCurrentApk] = pkgInfo?.applicationInfo?.metaData?.getString("pluginname")
                ?: ""
        mPluginPackageName[mCurrentApk] = pkgInfo?.packageName ?: ""
    }

    /**
     * 根据资源名称，资源类型，获取资源ID
     *
     * @param name
     * @param defType
     * @return
     */
    fun getResourceID(name: String?, defType: String?): Int {
        return pluginResources!!.getIdentifier(name, defType, pluginPackageName)
    }

    /**
     * 获取当前正在使用插件的Resources
     *
     * @return
     */
    val pluginResources: Resources?
        get() = mPluginResources[mCurrentApk]

    val pluginTheme: Theme?
        get() = mPluginTheme[mCurrentApk]

    val pluginAsset: AssetManager?
        get() = mPluginAsset[mCurrentApk]

    val pluginPackageName: String?
        get() = mPluginPackageName[mCurrentApk]

    val pluginClassName: String?
        get() = mPluginClassName[mCurrentApk]

    init {
        mPluginTheme = HashMap()
        mPluginResources = HashMap()
        mPluginAsset = HashMap()
        mPluginPackageName = HashMap()
        mPluginClassName = HashMap()
    }
}