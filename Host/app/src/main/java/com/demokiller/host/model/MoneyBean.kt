package com.demokiller.host.model
import android.annotation.SuppressLint
import com.demokiller.host.utils.PatchProxy
import com.demokiller.robustpatch.ChangeQuickRedirect
import java.util.*

class MoneyBean {
    @SuppressLint("UseValueOf")
    fun getInfo(str: String, f: Float, i: Int, list: List<String?>): List<String> {
        if (changeQuickRedirect != null) {
            if (PatchProxy.isSupport(arrayOf(str, f, i, list), this, changeQuickRedirect, false)) {
                return PatchProxy.accessDispatch(arrayOf(str, f, i, list), this, changeQuickRedirect, false) as List<String>
            }
        }
        return ArrayList()
    }

    val moneyValue: Int
        get() {
            if (changeQuickRedirect != null) {
                if (PatchProxy.isSupport(arrayOf(0), this, changeQuickRedirect, false)) {
                    return (PatchProxy.accessDispatch(arrayOf(0), this, changeQuickRedirect, false) as Int).toInt()
                }
            }
            return 10
        }

    companion object {
        var changeQuickRedirect: ChangeQuickRedirect? = null
        fun desc(): String {
            if (changeQuickRedirect != null) {
                if (PatchProxy.isSupport(arrayOf(0), null, changeQuickRedirect, true)) {
                    return PatchProxy.accessDispatch(arrayOf(0), null, changeQuickRedirect, true) as String
                }
            }
            return "MoneyBean"
        }
    }
}