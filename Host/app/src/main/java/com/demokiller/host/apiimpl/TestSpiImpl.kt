package com.demokiller.host.apiimpl

import android.util.Log
import com.demokiller.host.api.TestSpi
import com.demokiller.spiannotation.ServiceImpl

@ServiceImpl
class TestSpiImpl: TestSpi {
    override fun test() {
        Log.d("wzh","eeeeeeeeeeee")
    }

}