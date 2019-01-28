package com.demokiller.host;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;

import com.demokiller.host.resource.PluginManager;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public AssetManager getAssets() {
        return PluginManager.getInstance().getPluginAsset() == null ?
                super.getAssets() : PluginManager.getInstance().getPluginAsset();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getPluginResources() == null ?
                super.getResources() : PluginManager.getInstance().getPluginResources();
    }

    @Override
    public Theme getTheme() {
        return PluginManager.getInstance().getPluginTheme() == null ?
                super.getTheme() : PluginManager.getInstance().getPluginTheme();
    }

}
