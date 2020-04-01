package com.ai.covid19.tracking.android.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.ai.covid19.tracking.android.R

class Check2Fragment : Fragment() {

    private lateinit var viewModel: Check2ViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel =
                ViewModelProviders.of(this).get(Check2ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_check_2, container, false)
        return root
    }
}
