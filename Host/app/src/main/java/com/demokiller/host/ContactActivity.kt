package com.demokiller.host

import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        Handler().postDelayed({
            text_tv.startScroll()
        }, 2000)

    }

}
