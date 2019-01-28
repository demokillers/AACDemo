package com.demokiller.host.resource;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;

public class PluginManager {

    private HashMap<String, AssetManager> mPluginAsset;
    private HashMap<String, Resources> mPluginResources;
    private HashMap<String, Theme> mPluginTheme;
    /**
     * 插件包名，例如 com.demokiller.plugin
     */
    private HashMap<String, String> mPluginPackageName;
    /**
     * 插件实现jar包方法的类名，例如 .utils.xxx
     */
    private HashMap<String, String> mPluginClassName;
    private String mCurrentApk;
    private Context mContext;
    private static PluginManager mPluginManager;

    private PluginManager() {
        mPluginTheme = new HashMap<>();
        mPluginResources = new HashMap<>();
        mPluginAsset = new HashMap<>();
        mPluginPackageName = new HashMap<>();
        mPluginClassName = new HashMap<>();
    }


    public static PluginManager getInstance() {
        if (mPluginManager != null) {
            return mPluginManager;
        } else {
            synchronized (PluginManager.class) {
                mPluginManager = new PluginManager();
                return mPluginManager;
            }
        }
    }

    public void setContext(Context context) {
        mContext = context;
    }

    /**
     * 使用AssetManager根据资源包位置加载资源
     *
     * @param apkPath
     */
    public void loadResources(String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, apkPath);
            mPluginAsset.put(apkPath, assetManager);
            mCurrentApk = apkPath;
        } catch (Exception e) {
            Log.d("wzh", "============loadResource error");
            e.printStackTrace();
            return;
        }
        Resources superRes = mContext.getResources();
        superRes.getDisplayMetrics();
        superRes.getConfiguration();
        mPluginResources.put(apkPath,
                new Resources(mPluginAsset.get(apkPath),
                        superRes.getDisplayMetrics(), superRes.getConfiguration()));
        mPluginTheme.put(apkPath, mPluginResources.get(apkPath).newTheme());
        mPluginTheme.get(apkPath).setTo(mContext.getTheme());
        getPackageInfo(apkPath);

    }

    public void getPackageInfo(String apkFilepath) {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath,
                    PackageManager.GET_ACTIVITIES |
                            PackageManager.GET_SERVICES |
                            PackageManager.GET_META_DATA)
            ;
        } catch (Exception e) {
            // should be something wrong with parse
            e.printStackTrace();
        }
        mPluginClassName.put(mCurrentApk, pkgInfo.applicationInfo.metaData.getString("pluginname"));
        mPluginPackageName.put(mCurrentApk, pkgInfo.packageName);
    }


    /**
     * 根据资源名称，资源类型，获取资源ID
     *
     * @param name
     * @param defType
     * @return
     */
    public int getResourceID(String name, String defType) {
        int id = getPluginResources().getIdentifier(name, defType, getPluginPackageName());
        return id;
    }

    /**
     * 获取当前正在使用插件的Resources
     *
     * @return
     */
    public Resources getPluginResources() {
        return mPluginResources.get(mCurrentApk);
    }

    public Theme getPluginTheme() {
        return mPluginTheme.get(mCurrentApk);
    }

    public AssetManager getPluginAsset() {
        return mPluginAsset.get(mCurrentApk);
    }

    public String getPluginPackageName() {
        return mPluginPackageName.get(mCurrentApk);
    }

    public String getPluginClassName() {
        return mPluginClassName.get(mCurrentApk);
    }

}
