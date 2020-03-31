package com.ai.covid19.tracking.android.ui.check

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Check1ViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Check 1 Fragment"
    }
    val text: LiveData<String> = _text
}