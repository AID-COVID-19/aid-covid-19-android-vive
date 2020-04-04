package com.ai.covid19.tracking.android.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ai.covid19.tracking.android.databinding.FragmentPhoneBinding
import com.hbb20.CountryCodePicker


class AuthPhoneFragment : Fragment(), CountryCodePicker.OnCountryChangeListener {

    private val viewModel: AuthPhoneViewModel by viewModels()
    private lateinit var binding: FragmentPhoneBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhoneBinding.inflate(inflater,container, false)
        binding.countryCodePicker.setOnCountryChangeListener(this)
        binding.countryCodePicker.setAutoDetectedCountry(true)
        return binding.root
    }

    override fun onCountrySelected() {
        viewModel.countryCode = binding.countryCodePicker.selectedCountryCode
        viewModel.countryName = binding.countryCodePicker.selectedCountryName
    }

}
