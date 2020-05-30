package com.demokiller.host.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.demokiller.host.database.Contact
import com.demokiller.host.database.DatabaseUtil

class TestWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        DatabaseUtil.getInstance().contactDao().insertContact(Contact(4, "4"))
        return Result.success()
    }
}