package com.demokiller.host.utils

import android.text.TextUtils
import com.demokiller.robustpatch.ChangeQuickRedirect

object PatchProxy {
    fun isSupport(argsObj: Array<Any>?, thisObj: Any?, changeQuickRedirect: ChangeQuickRedirect?, isStatic: Boolean): Boolean {
        if (changeQuickRedirect == null) {
            return false
        }
        val classMethod = getClassMethod(isStatic)
        return if (TextUtils.isEmpty(classMethod)) {
            false
        } else changeQuickRedirect.isSupport(classMethod, getObjects(argsObj, thisObj, isStatic))
    }

    fun accessDispatch(argsObj: Array<Any>?, thisObj: Any?, changeQuickRedirect: ChangeQuickRedirect?, isStatic: Boolean): Any? {
        if (changeQuickRedirect == null) {
            return null
        }
        val classMethod = getClassMethod(isStatic)
        return if (TextUtils.isEmpty(classMethod)) {
            null
        } else changeQuickRedirect.accessDispatch(classMethod, getObjects(argsObj, thisObj, isStatic))
    }

    fun accessDispatchVoid(argsObj: Array<Any>?, thisObj: Any, changeQuickRedirect: ChangeQuickRedirect?, isStatic: Boolean) {
        if (changeQuickRedirect != null) {
            val classMethod = getClassMethod(isStatic)
            if (!TextUtils.isEmpty(classMethod)) {
                changeQuickRedirect.accessDispatch(classMethod, getObjects(argsObj, thisObj, isStatic))
            }
        }
    }

    private fun getObjects(argsObj: Array<Any>?, thisObj: Any?, isStatic: Boolean): Array<Any?>? {
        if (argsObj == null) {
            return null
        }
        val newArgsObj: Array<Any?>
        val length = argsObj.size
        newArgsObj = if (isStatic) {
            arrayOfNulls(length)
        } else {
            arrayOfNulls(length + 1)
        }
        var i = 0
        while (i < length) {
            newArgsObj[i] = argsObj[i]
            i++
        }
        if (isStatic) {
            return newArgsObj
        }
        newArgsObj[i] = thisObj
        return newArgsObj
    }

    private fun getClassMethod(isStatic: Boolean): String {
        var str = ""
        try {
            val stackTraceElement = Throwable().stackTrace[2]
            str = stackTraceElement.className + ":" + stackTraceElement.methodName + ":" + isStatic
        } catch (th: Throwable) {
        }
        return str
    }
}