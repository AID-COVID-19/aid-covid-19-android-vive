package com.ai.covid19.tracking.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.*

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
        // 'this' refers the the current active activity
        AWSMobileClient.getInstance().showSignIn(
            this,
            SignInUIOptions.builder()
                .nextActivity(MainActivity::class.java)
                .backgroundColor(R.color.colorPrimary)
                .build(),
            object : Callback<UserStateDetails> {
                override fun onResult(result: UserStateDetails) {
                    Log.d("AuthActivity", "onResult: " + result.userState)
                    when (result.userState) {
                        UserState.SIGNED_IN -> Log.i("INIT", "logged in!")
                        UserState.SIGNED_OUT -> Log.i(
                            "AuthActivity",
                            "onResult: User did not choose to sign-in"
                        )
                        else -> AWSMobileClient.getInstance().signOut()
                    }
                }

                override fun onError(e: java.lang.Exception) {
                    Log.e("AuthActivity", "onError: ", e)
                }
            }
        )
    }
}
