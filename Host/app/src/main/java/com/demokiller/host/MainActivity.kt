package com.demokiller.host

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.View.DragShadowBuilder
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.demokiller.host.adapter.ContactAdapter
import com.demokiller.host.api.TestSpi
import com.demokiller.host.database.Contact
import com.demokiller.host.database.DatabaseUtil
import com.demokiller.host.model.ContactViewModel
import com.demokiller.host.okhttp4.OkHttp4Util
import com.demokiller.host.resource.PluginManager
import com.demokiller.library.UIinterface
import com.demokiller.spiandroid.SpiProvider
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
            val task = async(Dispatchers.IO) {
                DatabaseUtil.getInstance().contactDao().insertContact(Contact(1, "1"))
                DatabaseUtil.getInstance().contactDao().insertContact(Contact(2, "2"))
                DatabaseUtil.getInstance().contactDao().insertContact(Contact(3, "3"))
            }
            task.join()
            viewModel.mContact.observe(this@MainActivity, Observer {
                adapter.submitList(it)
            })
            drag_text_view.text = OkHttp4Util.post("https://www.baidu.com")
        }
        initDrag()
        SpiProvider.syncGetImpl(TestSpi::class.java).test()
    }

    private fun initDrag() {
        drag_text_view.setOnLongClickListener {
            val item = ClipData.Item(drag_text_view.text)
            val dragData = ClipData(
                    drag_text_view.text,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item)
            val shadowBuilder = DragShadowBuilder(it)
            it.startDrag(dragData, shadowBuilder, null, 0)
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