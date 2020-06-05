package com.demokiller.host

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import androidx.core.widget.TextViewCompat
import androidx.work.WorkManager
import com.demokiller.host.workmanager.WorkManagerUtils
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        Handler().postDelayed({
            marqueetextview.startScroll()
        }, 2000)

        WorkManager.getInstance(this).beginWith(WorkManagerUtils.test()).enqueue()
        textview?.let {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                    it, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(it,
                    1, 15, 1, TypedValue.COMPLEX_UNIT_SP)
        }
    }

}
