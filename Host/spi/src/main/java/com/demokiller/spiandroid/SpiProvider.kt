package com.demokiller.spiandroid

import com.demokiller.spiannotation.IServiceImplFinder
import com.demokiller.spiannotation.ServiceConst
import java.util.concurrent.ConcurrentHashMap

object SpiProvider {
    private val mModels = ConcurrentHashMap<Class<out Any>, Any>()

    fun <T> syncGetImpl(clz: Class<T>): T {
        mModels[clz]?.also { return it.castToImpl() }
        synchronized(SpiProvider) {
            mModels[clz]?.also { return it.castToImpl() }
            val finder = createImplFinder(clz)
            if(finder.getApis().contains(clz)){
                createImpl(finder)
            }
        }
        return mModels[clz] as T
    }

    private fun <T> createImplFinder(clz: Class<T>): IServiceImplFinder {
        val apiCanonicalName = clz.canonicalName
        val packageName = apiCanonicalName?.substring(0, apiCanonicalName.lastIndexOf("."))
        val apiName = apiCanonicalName?.substring(apiCanonicalName.lastIndexOf(".") + 1)
        val implCanonicalName = packageName + "." + apiName + ServiceConst.SERVICE_FINDER_SUFFIX
        return (Class.forName(implCanonicalName).newInstance() as IServiceImplFinder)
    }

    private fun createImpl(finder: IServiceImplFinder) {
        val apiImpl = finder.getInstance()
        finder.getApis().forEach {
            mModels[it as Class<out Any>] = apiImpl
        }
    }

    private fun <T> Any.castToImpl(): T {
        return this as T
    }
}