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
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException

class AuthViewModel : ViewModel() {

    var countryCode:String? = null
    var countryName:String? = null
    var phoneNumber:String? = null
    var phoneTemporalPassword:String? = null

    // This is to prevent duplicate-operation errors, such as calling
    // AWSMobileClient.getInstance().signIn() twice, which causes a crash:
    private var isBusy = false

    val isTempSignIn = MutableLiveData<Boolean>()

    fun tempSigIn(value: Boolean) {
        isTempSignIn.value = value
    }

    val isSignIn = MutableLiveData<Boolean>()

    fun sigIn(value: Boolean) {
        isSignIn.value = value
    }

    var isNewPassDone = MutableLiveData<Boolean>()

    fun newPassDone(value: Boolean) = runOnUiThread {
        isNewPassDone.value = value
    }

    val authorizationFailedNeedsNotification = MutableLiveData<Boolean>()

    fun setAuthorizationFailedNeedsNotification(needsNotification: Boolean) = runOnUiThread {
        authorizationFailedNeedsNotification.value = needsNotification
    }

    init {
        isTempSignIn.value = false
        isSignIn.value = false
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is auth phone Fragment"
    }
    val text: LiveData<String> = _text

    fun signIn(username: String, password: String) {
        if (isBusy)
            return

        isBusy = true

        AWSMobileClient.getInstance().signIn(
            username,
            password,
            null,
            object : Callback<SignInResult> {
                override fun onResult(signInResult: SignInResult) {
                    isBusy = false
                    runOnUiThread(Runnable {
                        Log.d(
                            this.javaClass.canonicalName,
                            "Sign-in callback state: " + signInResult.signInState
                        )
                        when (signInResult.signInState) {
                            SignInState.DONE -> sigIn(true)
                            SignInState.NEW_PASSWORD_REQUIRED -> tempSigIn(true)
                            else -> Log.d(this.javaClass.canonicalName, "Unsupported sign-in confirmation: " + signInResult.signInState)
                        }
                    })
                }

                override fun onError(e: Exception) {
                    isBusy = false
                    Log.e(this.javaClass.canonicalName, "Sign-in error", e)

                    if (e is NotAuthorizedException) {
                        setAuthorizationFailedNeedsNotification(true)
                    }
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