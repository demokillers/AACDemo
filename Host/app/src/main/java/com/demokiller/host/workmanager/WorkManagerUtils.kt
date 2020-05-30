package com.demokiller.host.workmanager

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import java.util.concurrent.TimeUnit


object WorkManagerUtils {
    fun test(): OneTimeWorkRequest {
        return OneTimeWorkRequest.Builder(TestWorker::class.java)
                .setInputData(Data.Builder().putString("fdsf", "fsdfsdf").build())
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()

    }
}