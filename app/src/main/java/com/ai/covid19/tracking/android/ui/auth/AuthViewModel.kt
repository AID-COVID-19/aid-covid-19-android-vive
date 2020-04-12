package com.ai.covid19.tracking.android.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ai.covid19.tracking.android.R
import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException
import com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException

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

    val lastErrorStringRes = MutableLiveData<Int?>()

    fun setLastErrorStringRes(newStringRes: Int?) = runOnUiThread {
        lastErrorStringRes.value = newStringRes
    }

    init {
        isTempSignIn.value = false
        isSignIn.value = false
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is auth phone Fragment"
    }
    val text: LiveData<String> = _text

    private fun signIn(username: String, password: String) {
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
                            else -> {
                                Log.d(
                                    this.javaClass.canonicalName,
                                    "Unsupported sign-in confirmation: " + signInResult.signInState
                                )
                                setLastErrorStringRes(R.string.auth_service_exception)
                            }
                        }
                    })
                }

                override fun onError(e: Exception) {
                    isBusy = false
                    Log.e(this.javaClass.canonicalName, "Sign-in error", e)

                    when (e) {
                        is NotAuthorizedException -> setLastErrorStringRes(R.string.auth_invalid_credentials_provided)
                        is UserNotFoundException -> setLastErrorStringRes(R.string.auth_user_not_found)
                        is AmazonClientException -> setLastErrorStringRes(R.string.auth_client_exception)
                        is AmazonServiceException -> setLastErrorStringRes(R.string.auth_service_exception)
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

    fun onPhoneAndPasswordProvided(typedPhoneNumber: String, typedTempPassword: String) {
        phoneNumber = typedPhoneNumber.trim()
        phoneTemporalPassword = typedTempPassword

        if (phoneNumber!!.isEmpty() || phoneTemporalPassword!!.isEmpty()) {
            setLastErrorStringRes(R.string.auth_incomplete_fields)
        } else {
            signIn("+$countryCode$phoneNumber", phoneTemporalPassword!!)
        }
    }
}