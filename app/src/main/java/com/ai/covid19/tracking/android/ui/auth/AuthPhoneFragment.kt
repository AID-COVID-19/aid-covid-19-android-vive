package com.ai.covid19.tracking.android.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ai.covid19.tracking.android.R
import com.ai.covid19.tracking.android.databinding.FragmentPhoneBinding
import com.hbb20.CountryCodePicker

class AuthPhoneFragment : Fragment(), CountryCodePicker.OnCountryChangeListener {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhoneBinding.inflate(inflater,container, false)
        binding.buttonAuthNext1.setOnClickListener{onNextButtonClicked()}
        binding.countryCodePicker.setOnCountryChangeListener(this)
        binding.countryCodePicker.setAutoDetectedCountry(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isTempSignIn.observe(viewLifecycleOwner, Observer {
            val nav = findNavController()
            nav.navigate(R.id.action_navigation_login_to_authNewPassFragment)
        })
    }

    override fun onCountrySelected() {
        viewModel.countryCode = binding.countryCodePicker.selectedCountryCode
        viewModel.countryName = binding.countryCodePicker.selectedCountryName
    }

    private fun onNextButtonClicked() {
        viewModel.phoneNumber = binding.editTextPhone.text.toString()
        viewModel.phoneTemporalPassword = binding.editTextPassword.text.toString()
        viewModel.signIn("+" + viewModel.countryCode + viewModel.phoneNumber, viewModel.phoneTemporalPassword!!)
    }
}
