package com.demokiller.robustpatchimpl

import android.text.TextUtils
import com.demokiller.robustpatch.ChangeQuickRedirect

class MoneyBeanStatePatch : ChangeQuickRedirect {
    override fun accessDispatch(methodSignature: String?, paramArrayOfObject: Array<Any?>?): Any? { //这里通过判断方法签名进行方法的替换
        //methodSignature格式为：classname:methodname:isstatic
        val signature = methodSignature?.split(":")?.toTypedArray()
        //以下句式需要进行修复方法的具体修复代码
        if (TextUtils.equals(signature?.get(1), "getMoneyValue")) {
            return 10000
        }
        return if (TextUtils.equals(signature?.get(1), "desc")) {
            "Patch Desc"
        } else null
    }

    override fun isSupport(methodSignature: String?, paramArrayOfObject: Array<Any?>?): Boolean { //这里需要先校验方法的正确性
        //methodSignature格式为：classname:methodname:isstatic
        val signature = methodSignature?.split(":")?.toTypedArray()
        //以下就是需要进行修复的方法名称
        if (TextUtils.equals(signature?.get(1), "getMoneyValue")) {
            return true
        }
        return TextUtils.equals(signature?.get(1), "desc")
    }
}