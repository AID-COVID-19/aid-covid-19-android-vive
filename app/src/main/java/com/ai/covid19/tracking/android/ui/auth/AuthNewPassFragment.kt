package com.ai.covid19.tracking.android.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ai.covid19.tracking.android.MainActivity
import com.ai.covid19.tracking.android.databinding.FragmentNewPassBinding

class AuthNewPassFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentNewPassBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPassBinding.inflate(inflater, container, false)
        binding.buttonAuthNext2.setOnClickListener { onNextButtonClicked() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isNewPassDone.observe(viewLifecycleOwner, Observer {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        })
        viewModel.lastErrorStringRes.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    viewModel.setLastErrorStringRes(null)
                }
            })
    }

    private fun onNextButtonClicked() {
        viewModel.onNewPasswordProvided(binding.editTextNewPassword.text.toString())
    }
}
