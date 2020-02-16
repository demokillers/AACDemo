package com.demokiller.robustpatch

interface ChangeQuickRedirect {
    fun isSupport(methodSignature: String?, paramArrayOfObject: Array<Any?>?): Boolean
    fun accessDispatch(methodSignature: String?, paramArrayOfObject: Array<Any?>?): Any?
}