package com.demokiller.robustpatchimpl

import com.demokiller.robustpatch.PatchedClassInfo
import com.demokiller.robustpatch.PatchesInfo
import java.util.*

class PatchesInfoImpl : PatchesInfo {
    override fun getPatchedClassesInfo(): List<PatchedClassInfo?>? {
        val patchedClassesInfos: MutableList<PatchedClassInfo> = ArrayList()
        val patchedClass = PatchedClassInfo(
                "com.demokiller.host.MoneyBean",
                MoneyBeanStatePatch::class.java.canonicalName)
        patchedClassesInfos.add(patchedClass)
        return patchedClassesInfos
    }
}