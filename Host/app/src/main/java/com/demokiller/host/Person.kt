package com.demokiller.host

import com.demokiller.host.utils.PatchProxy
import com.demokiller.robustpatch.ChangeQuickRedirect

object Person {
    var changeQuickRedirect: ChangeQuickRedirect? = null
    fun toStr(str: String): String {
        if (changeQuickRedirect != null) {
            if (PatchProxy.isSupport(arrayOf<Any>(str), null, changeQuickRedirect, true)) {
                return PatchProxy.accessDispatch(arrayOf<Any>(str), null, changeQuickRedirect, true) as String
            }
        }
        return "aaa"
    }
}