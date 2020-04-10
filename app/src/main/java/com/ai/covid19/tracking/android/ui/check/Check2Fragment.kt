package com.ai.covid19.tracking.android.ui.check

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ai.covid19.tracking.android.R
import com.ai.covid19.tracking.android.databinding.FragmentCheck2Binding
import com.amazonaws.amplify.generated.graphql.UpdateCheckMutation
import com.amazonaws.mobile.client.AWSMobileClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import type.UpdateCheckInput
import javax.annotation.Nonnull

class Check2Fragment : Fragment() {

    private val viewModelCheck: CheckViewModel by activityViewModels()
    private lateinit var binding: FragmentCheck2Binding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheck2Binding.inflate(inflater, container, false)
        temperatureRangeSetup()
        breathRangeSetup()
        bloodPleasureSetup()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listener: (View) -> Unit = {
            saveCheck2()
        }
        binding.buttonNextCheck2.setOnClickListener(listener)
    }

    private fun temperatureRangeSetup() {
        binding.temperature.check(viewModelCheck.temperatureRangeSelectedId)
        binding.temperature.setOnCheckedChangeListener { _, checkedId ->
            viewModelCheck.temperatureRangeSelectedId = checkedId
            viewModelCheck.temperatureRange = when (checkedId) {
                R.id.temperature_range_1 -> getString(R.string.temperature_range_1)
                R.id.temperature_range_2 -> getString(R.string.temperature_range_2)
                R.id.temperature_range_3 -> getString(R.string.temperature_range_3)
                R.id.temperature_range_4 -> getString(R.string.temperature_range_4)
                else -> getString(R.string.programming_error)
            }
        }
    }

    private fun breathRangeSetup() {
        binding.breath.check(viewModelCheck.breathRangeSelectedId)
        binding.breath.setOnCheckedChangeListener { _, checkedId ->
            viewModelCheck.breathRangeSelectedId = checkedId
            viewModelCheck.breathsPerMinuteRange = when (checkedId) {
                R.id.breath_range_1 -> getString(R.string.breath_range_1)
                R.id.breath_range_2 -> getString(R.string.breath_range_2)
                R.id.breath_range_3 -> getString(R.string.breath_range_3)
                R.id.breath_range_4 -> getString(R.string.breath_range_4)
                else -> getString(R.string.programming_error)
            }
        }
    }

    private fun bloodPleasureSetup(){
        binding.bloodPressureMax.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (!s.isBlank())
                    viewModelCheck.bloodPressureHighValue = s.toString().toInt()
            }
        })

        binding.bloodPressureMax.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (!s.isBlank())
                    viewModelCheck.bloodPressureLowValue = s.toString().toInt()
            }
        })
    }

    private fun saveCheck2() {
        val updateCheckInput = UpdateCheckInput.builder()
            .identityId(AWSMobileClient.getInstance().identityId)
            .checkTimestamp(viewModelCheck.timeStampLongId)
            .temperatureRange(viewModelCheck.temperatureRange)
            .breathsPerMinuteRange(viewModelCheck.breathsPerMinuteRange)
            .bloodPressureHighValue(viewModelCheck.bloodPressureHighValue)
            .bloodPressureLowValue(viewModelCheck.bloodPressureLowValue)
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
                runEvaluation()
                findNavController().navigate(R.id.action_check2Fragment_to_checkResultFragment)
                Log.i(this.javaClass.canonicalName , "Check 2 was added to database.");
            }
        }

    private fun runEvaluation() {
        val last12hTemperatures: Map<Long, Boolean> = mapOf(Pair(1586418640000L, viewModelCheck.fever),
            Pair(1586398840000L, true),
            Pair(1586380240000L, true)
        )
        val riskAlgorithm = RiskAlgorithm(context = requireContext(),
            breath_range = viewModelCheck.breathsPerMinuteRange,
            last12hPainChestMeasures = last12hTemperatures,
            headache = viewModelCheck.headache,
            temperature_range = viewModelCheck.temperatureRange,
            newConfusionOrInabilityToArouse = viewModelCheck.newConfusionOrInabilityToArouse,
            bluishLipsOrFace = viewModelCheck.bluishLipsOrFace
        )
        viewModelCheck.riskResult = riskAlgorithm.calculateRisk()
    }
}
