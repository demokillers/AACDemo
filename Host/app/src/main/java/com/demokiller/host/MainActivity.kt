package com.demokiller.host

import android.os.Bundle
import android.os.UserHandle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStore
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.demokiller.host.adapter.ContactAdapter
import com.demokiller.host.database.Contact
import com.demokiller.host.database.DatabaseUtil
import com.demokiller.host.model.ContactViewModel
import com.demokiller.host.resource.PluginManager
import com.demokiller.library.UIinterface
import dalvik.system.DexClassLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : BaseActivity() {
    private var classLoader: DexClassLoader? = null
    private var lib: UIinterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DatabaseUtil.getInstance(this)
        val viewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        val adapter = ContactAdapter()
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        launch {
            val a =async (Dispatchers.IO) {
                DatabaseUtil.getInstance().contactDao().insertContact(Contact(1, "1"))
                DatabaseUtil.getInstance().contactDao().insertContact(Contact(2, "2"))
                DatabaseUtil.getInstance().contactDao().insertContact(Contact(3, "3"))
            }
            a.join()
            viewModel.mContact.observe(this@MainActivity, Observer {
                adapter.submitList(it)
            })
        }
    }

    fun loadDex() {
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