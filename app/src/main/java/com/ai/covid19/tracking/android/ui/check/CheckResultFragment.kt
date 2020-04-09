package com.ai.covid19.tracking.android.ui.check

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ai.covid19.tracking.android.R
import com.ai.covid19.tracking.android.databinding.FragmentCheckResultBinding
import com.amazonaws.amplify.generated.graphql.UpdateCheckMutation
import com.amazonaws.mobile.client.AWSMobileClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import type.UpdateCheckInput
import javax.annotation.Nonnull

class CheckResultFragment : Fragment() {

    private val viewModelCheck: CheckViewModel by activityViewModels()
    private lateinit var binding: FragmentCheckResultBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckResultBinding.inflate(inflater, container, false)
        setViewAccordingToResult()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listenerEnd: (View) -> Unit = {
        }
        val listenerDiscard: (View) -> Unit = {
        }
        binding.buttonEnd.setOnClickListener(listenerEnd)
        binding.txtDisc.setOnClickListener(listenerDiscard)
    }

    private fun setViewAccordingToResult() {
        when (viewModelCheck.riskResult) {
            RiskAlgorithm.RiskClassification.LOW -> settingLowResult()
            RiskAlgorithm.RiskClassification.MODERATE -> settingModerateResult()
            RiskAlgorithm.RiskClassification.HIGH -> settingHighResult()
        }
    }

    private fun settingLowResult() {
        val lowColor = ContextCompat.getColor(requireContext(), R.color.colorBackgroundGreenAlert)
        binding.fragmentCheckResult.setBackgroundColor(lowColor)
        binding.textViewRiskTitle.text = getText(R.string.check_result_low_title)
        binding.txtNotice.text = getText(R.string.check_result_low_notice)
    }

    private fun settingModerateResult() {
        val moderateColor = ContextCompat.getColor(requireContext(), R.color.colorBackgroundOrangeAlert)
        binding.fragmentCheckResult.setBackgroundColor(moderateColor)
        binding.textViewRiskTitle.text = getText(R.string.check_result_moderated_title)
        binding.txtNotice.text = getText(R.string.check_result_moderated_notice)
    }

    private fun settingHighResult() {
        val highColor = ContextCompat.getColor(requireContext(), R.color.colorBackgroundRedAlert)
        binding.fragmentCheckResult.setBackgroundColor(highColor)
        binding.textViewRiskTitle.text = getText(R.string.check_result_high_title)
        binding.txtNotice.text = getText(R.string.check_result_high_notice)
    }

}
