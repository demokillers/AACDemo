package com.demokiller.host

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.content.pm.PackageManager
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
import com.demokiller.host.databinding.ActivityMainBinding
import com.demokiller.host.model.ContactViewModel
import com.demokiller.host.native.JNIUtils
import com.demokiller.host.okhttp4.OkHttp4Util
import com.demokiller.host.resource.PluginManager
import com.demokiller.host.utils.ConstantUtils
import com.demokiller.library.UIinterface
import com.demokiller.spiandroid.SpiProvider
import dalvik.system.DexClassLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : BaseActivity() {
    private var classLoader: DexClassLoader? = null
    private var lib: UIinterface? = null
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DatabaseUtil.getInstance(this)
        val viewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        val adapter = ContactAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
            binding.dragTextView.text = OkHttp4Util.post("https://www.baidu.com")
        }
        initDrag()
        SpiProvider.syncGetImpl(TestSpi::class.java).test()
    }

    override fun onResume() {
        super.onResume()
        applicationContext.packageManager.getPackageInfo("com.demokiller.host", PackageManager.GET_ACTIVITIES)
        Log.d(ConstantUtils.TAG, JNIUtils().testJNI())
    }

    private fun initDrag() {
        binding.dragTextView.setOnLongClickListener {
            val item = ClipData.Item(binding.dragTextView.text)
            val dragData = ClipData(
                binding.dragTextView.text,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item)
            val shadowBuilder = DragShadowBuilder(it)
            it.startDrag(dragData, shadowBuilder, null, 0)
        }
        binding.dragTextView.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }
    }

    fun loadDex() {
        var filePath = "/data/data/com.demokiller.host/cache/app-release-unsigned.apk"
        filePath = "/mnt/usb/sda1/app-release-unsigned.apk"
        val file = File(filePath)
        if (!file.exists()) filePath = "/mnt/usb/sdb1/app-release-unsigned.apk"
        val releasePath = cacheDir.absolutePath
        Log.d(ConstantUtils.TAG, filePath)
        Log.d(ConstantUtils.TAG, releasePath)
        Log.d(ConstantUtils.TAG, getClassLoader().toString())
        classLoader = DexClassLoader(filePath, releasePath, null, getClassLoader())
        PluginManager.setContext(this)
        PluginManager.loadResources(filePath)
        binding.root.setBackgroundResource(PluginManager.getResourceID("background", "drawable"))
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
            binding.root.background = lib?.getBackground(this)
        } catch (e: Exception) {
            Log.i(ConstantUtils.TAG, "error:" + Log.getStackTraceString(e))
        }
    }
}