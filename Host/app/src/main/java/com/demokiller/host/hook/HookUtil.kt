package com.demokiller.host.hook

import android.content.Context
import android.os.IBinder
import android.os.Parcel
import android.util.Log
import android.widget.Toast
import java.lang.Exception
import java.lang.reflect.Proxy

class HookUtil {
    companion object {
        fun hookService(context: Context, serviceName: String, interfaceName: String): Any {
            val serviceManager = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManager.getDeclaredMethod("getService", String::class.java)
            getServiceMethod.isAccessible = true
            val serviceBinder = getServiceMethod.invoke(null, serviceName)
            val interfaceStubClass = Class.forName("$interfaceName\$Stub")
            val asInterfaceMethod = interfaceStubClass.getDeclaredMethod("asInterface", IBinder::class.java)
            val serviceBinderImplProxy = Proxy.newProxyInstance(context.classLoader,
                    arrayOf(IBinder::class.java)) { _, method, args ->
                Log.i("demoKillerTag", "method=$method")
                Log.i("demoKillerTag", "args.size=" + args.size)
                if(method.name == "transact"){
                    Log.i("demoKillerTag", "transact data.size=" + (args[1] as Parcel).dataSize())
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
    }
}