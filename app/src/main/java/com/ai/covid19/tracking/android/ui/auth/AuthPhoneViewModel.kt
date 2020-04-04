package com.ai.covid19.tracking.android.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthPhoneViewModel : ViewModel() {

    var countryCode:String? = null
    var countryName:String? = null

    private val _text = MutableLiveData<String>().apply {
        value = "This is auth phone Fragment"
    }
    val text: LiveData<String> = _text
}