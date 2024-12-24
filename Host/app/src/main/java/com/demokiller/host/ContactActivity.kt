package com.demokiller.host

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import androidx.core.widget.TextViewCompat
import androidx.work.WorkManager
import com.demokiller.host.databinding.ActivityContactBinding
import com.demokiller.host.workmanager.WorkManagerUtils

class ContactActivity : BaseActivity() {

    lateinit var binding: ActivityContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler().postDelayed({
            binding.marqueetextview.startScroll()
        }, 2000)

        WorkManager.getInstance(this).beginWith(WorkManagerUtils.test()).enqueue()
        binding.textview.let {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                it, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                it,
                1, 15, 1, TypedValue.COMPLEX_UNIT_SP
            )
        }
    }

}
