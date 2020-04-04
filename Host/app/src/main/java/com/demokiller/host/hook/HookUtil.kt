package com.demokiller.host.hook

import android.app.ActivityManager
import android.content.Context
import android.os.IBinder
import android.os.Parcel
import android.util.Log
import android.widget.Toast
import java.lang.reflect.Proxy

class HookUtil {
    companion object {
        private fun hookService(context: Context, serviceName: String, interfaceName: String): Any {
            val serviceManager = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManager.getDeclaredMethod("getService", String::class.java)
            getServiceMethod.isAccessible = true
            val serviceBinder = getServiceMethod.invoke(null, serviceName)
            val interfaceStubClass = Class.forName("$interfaceName\$Stub")
            val asInterfaceMethod = interfaceStubClass.getDeclaredMethod("asInterface", IBinder::class.java)
            val serviceBinderImplProxy = Proxy.newProxyInstance(context.classLoader,
                    arrayOf(IBinder::class.java)) { _, method, args ->
                Log.e("demoKillerTag", "method=$method")
                Log.e("demoKillerTag", Thread.currentThread().stackTrace.contentDeepToString())
                if (method.name == "transact") {
                    Log.e("demoKillerTag", "transact data.size=" + (args[1] as Parcel).dataSize())
                }
                method.invoke(serviceBinder, *(args ?: emptyArray()))
            }
            val sCache = serviceManager.getDeclaredField("sCache")
            sCache.isAccessible = true
            val cacheMap = sCache.get(null) as MutableMap<String, IBinder>
            cacheMap[serviceName] = serviceBinderImplProxy as IBinder
            return asInterfaceMethod.invoke(null, serviceBinderImplProxy)
        }

        fun hookPackageManager(context: Context) {
            val packageManager = context.packageManager
            val field = packageManager::class.java.getDeclaredField("mPM")
            field.isAccessible = true
            field.set(packageManager,
                    hookService(context, "package", "android.content.pm.IPackageManager"))
        }

        fun hookActivityManager(context: Context) {
            val serviceManager = Class.forName("android.app.ActivityManager")
            val iActivityManagerSingleton = serviceManager.getDeclaredField("IActivityManagerSingleton")
            iActivityManagerSingleton.isAccessible = true
            val singleton = Class.forName("android.util.Singleton")
            val mInstance = singleton.getDeclaredField("mInstance")
            mInstance.isAccessible = true
            val activityManager = hookService(context, "activity", "android.app.IActivityManager")
            mInstance.set(iActivityManagerSingleton.get(null), activityManager)
        }
    }
}