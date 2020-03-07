package com.demokiller.host
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demokiller.host.resource.PluginManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
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

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}