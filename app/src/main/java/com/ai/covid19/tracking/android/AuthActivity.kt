package com.ai.covid19.tracking.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails?> {
                override fun onError(e: Exception?) {
                    Log.e("INIT", "Initialization error.", e)
                }

                override fun onResult(result: UserStateDetails?) {
                    Log.i("INIT", "onResult: " + result!!.userState)
                }
            }
            )

    }
}
