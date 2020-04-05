package com.ai.covid19.tracking.android.ui.check

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.mobile.client.AWSMobileClient

class CheckViewModel : ViewModel() {

    init {
        AWSMobileClient.getInstance().userAttributes[""]
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is Check Fragment"
    }
    val text: LiveData<String> = _text
}