package com.demokiller.host.okhttp4

import kotlinx.coroutines.delay
import okhttp3.*
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object OkHttp4Util {
    private val mOkHttpClient: OkHttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient.Builder().build()
    }

    suspend fun post(url: String): String {
        return suspendCoroutine {
            mOkHttpClient.newCall(Request.Builder().post(FormBody.Builder().build()).url(url).build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    it.resume(response.toString())
                }
            })
        }
    }

    suspend fun get(url: String): String {
        return suspendCoroutine {
            mOkHttpClient.newCall(Request.Builder().get().url(url).build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    it.resume(response.toString())
                }
            })
        }
    }
}