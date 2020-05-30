package com.demokiller.host

import android.os.Bundle
import android.os.Handler
import androidx.work.WorkManager
import com.demokiller.host.workmanager.WorkManagerUtils
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        Handler().postDelayed({
            text_tv.startScroll()
        }, 2000)

        WorkManager.getInstance(this).beginWith(WorkManagerUtils.test()).enqueue()
    }

}
