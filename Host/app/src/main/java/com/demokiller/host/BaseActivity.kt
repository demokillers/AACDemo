package com.demokiller.host

import android.app.Activity
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.os.Bundle
import com.demokiller.host.resource.PluginManager

open class BaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getAssets(): AssetManager {
        PluginManager.pluginAsset?.let {
            return it
        }
        return super.getAssets()
    }

    override fun getResources(): Resources {
        PluginManager.pluginResources?.let {
            return it
        }
        return super.getResources()
    }

    override fun getTheme(): Theme {
        PluginManager.pluginTheme?.let {
            return it
        }
        return super.getTheme()
    }
}