package com.ai.covid19.tracking.android.ui.check

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
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
            saveResult(viewModelCheck.riskResult)
            activity?.finish()
        }
        val listenerDiscard: (View) -> Unit = {
            saveResult(RiskAlgorithm.RiskClassification.DISCARDED)
            val directions = CheckResultFragmentDirections.actionCheckResultFragmentToCheck1Fragment(true)
            navigate(directions)
        }
        binding.buttonEnd.setOnClickListener(listenerEnd)
        binding.txtDisc.setOnClickListener(listenerDiscard)
    }

    private fun navigate(destination: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(destination.actionId)
            ?.let { navigate(destination) }
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

    private fun saveResult(result: RiskAlgorithm.RiskClassification) {
        val updateCheckInput = UpdateCheckInput.builder()
            .identityId(AWSMobileClient.getInstance().identityId)
            .riskResult(result.toString())
            .build()
        viewModelCheck.mAWSAppSyncClient?.mutate(UpdateCheckMutation.builder().input(updateCheckInput).build())
            ?.enqueue(mutationCallback)
    }

    private val mutationCallback: GraphQLCall.Callback<UpdateCheckMutation.Data?> =
        object : GraphQLCall.Callback<UpdateCheckMutation.Data?>() {
            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("Error", e.toString())
            }
            override fun onResponse(response: com.apollographql.apollo.api.Response<UpdateCheckMutation.Data?>) {
                Log.i(this.javaClass.canonicalName , "Risk result was added to database.");
            }
        }
}
