package com.ai.covid19.tracking.android.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState

class AuthViewModel : ViewModel() {

    var countryCode:String? = null
    var countryName:String? = null
    var phoneNumber:String? = null
    var phoneTemporalPassword:String? = null
    val isTempSignIn = MutableLiveData<Boolean>()

    fun tempSigIn(value: Boolean) {
        isTempSignIn.value = value
    }

    var isNewPassDone = MutableLiveData<Boolean>()

    fun newPassDone(value: Boolean) {
        runOnUiThread(Runnable {
            isNewPassDone.value = value
        })
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is auth phone Fragment"
    }
    val text: LiveData<String> = _text

    fun signIn(username: String, password: String) {
        AWSMobileClient.getInstance().signIn(
            username,
            password,
            null,
            object : Callback<SignInResult> {
                override fun onResult(signInResult: SignInResult) {
                    runOnUiThread(Runnable {
                        Log.d(
                            this.javaClass.canonicalName,
                            "Sign-in callback state: " + signInResult.signInState
                        )
                        when (signInResult.signInState) {
                            SignInState.DONE ->  Log.d(this.javaClass.canonicalName, "Sign In Done.")
                            SignInState.NEW_PASSWORD_REQUIRED -> tempSigIn(true)
                            else -> Log.d(this.javaClass.canonicalName, "Unsupported sign-in confirmation: " + signInResult.signInState)
                        }
                    })
                }

                override fun onError(e: java.lang.Exception) {
                    Log.e(this.javaClass.canonicalName, "Sign-in error", e)
                }
            })
    }

    fun confirmSignIn(newPass: String) {
        AWSMobileClient.getInstance()
            .confirmSignIn(newPass, object : Callback<SignInResult?> {
                override fun onResult(result: SignInResult?) {
                    Log.d(this.javaClass.canonicalName, "Sign-in callback state: " + result!!.signInState)
                    when (result.signInState) {
                        SignInState.DONE ->  newPassDone(true)
                        SignInState.SMS_MFA ->  Log.d(this.javaClass.canonicalName, "Please confirm sign-in with SMS.")
                        else ->  Log.d(this.javaClass.canonicalName, "Unsupported sign-in confirmation: " + result.signInState)
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e(this.javaClass.canonicalName, "Sign-in error", e)
                }
            })

    }
}