package com.ai.covid19.tracking.android.ui.check

import android.os.Bundle
import android.util.ArrayMap
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
import com.ai.covid19.tracking.android.util.ORGANIZATION_ID
import com.amazonaws.amplify.generated.graphql.*
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
import type.*
import java.math.BigDecimal
import java.math.RoundingMode
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runEvaluation()
        val listenerEnd: (View) -> Unit = {
            binding.buttonEnd.isEnabled = false
            binding.buttonEnd.showLoading()
            saveResult(viewModelCheck.riskResult)
            queryLastCheck()
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
        settingScore()
        Thread.sleep(1_000)
        settinAllVisible()
    }

    private fun settingScore() {
        val roundScore = BigDecimal(viewModelCheck.riskScore.toDouble()).setScale(6, RoundingMode.HALF_EVEN)
        binding.textViewRiskScore.text = roundScore.toString()
    }

    private fun settinAllVisible() {
        binding.textViewRiskScore.visibility = View.VISIBLE
        binding.textViewRiskTitle.visibility = View.VISIBLE
        binding.txtNotice.visibility = View.VISIBLE
        binding.txtDisc.visibility = View.VISIBLE
        binding.buttonEnd.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
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
            .checkTimestamp(viewModelCheck.timeStampLongId)
            .riskResult(result.toString())
            .riskScore(viewModelCheck.riskScore)
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

    private fun runEvaluation(last12hMeasures: Map<Long, Boolean?>) {
        val riskAlgorithm = RiskAlgorithm(context = requireContext(),
            breath_range = viewModelCheck.breathsPerMinuteRange,
            last12hPainChestMeasures = last12hMeasures,
            headache = viewModelCheck.headache,
            temperature_range = viewModelCheck.temperatureRange,
            newConfusionOrInabilityToArouse = viewModelCheck.newConfusionOrInabilityToArouse,
            bluishLipsOrFace = viewModelCheck.bluishLipsOrFace
        )
        viewModelCheck.riskResult = riskAlgorithm.calculateRisk()
        viewModelCheck.riskScore = calculateScore()
        setViewAccordingToResult()
    }

    private fun runEvaluation() {
        val dateTime12HrAgo = DateTime.now() - 12.hours
        val filter = TableCheckFilterInput.builder().checkTimestamp(
            TableIntFilterInput.builder().gt(dateTime12HrAgo.unixMillis.toInt()).build()
        ).build()
        viewModelCheck.mAWSAppSyncClient?.query(ListChecksQuery.builder().filter(filter).build())
            ?.responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
            ?.enqueue(callbackList)
    }

    private val callbackList: GraphQLCall.Callback<ListChecksQuery.Data?> =
        object : GraphQLCall.Callback<ListChecksQuery.Data?>() {

            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("ERROR", e.toString())
            }

            override fun onResponse(response: com.apollographql.apollo.api.Response<ListChecksQuery.Data?>) {
                if( response.data() != null) {
                    Log.i("Results", response.data()?.listChecks()?.items().toString())
                    runOnUiThread {
                        val last12hMeasures: MutableMap<Long, Boolean?> = ArrayMap()
                        response.data()?.listChecks()?.items()?.forEach {
                            last12hMeasures[it.checkTimestamp()] = it.chestOrBackPain()
                        }
                        runEvaluation(last12hMeasures)
                    }
                }
            }
        }

    private fun calculateScore(): Double {
        val sigmoideAlgorithm = SigmoideAlgorithm()
        val score = Score()

        val scoreList: MutableList<Double> = ArrayList()

        when(viewModelCheck.howYouFeel) {
            getString(R.string.howYouFeel_better) -> scoreList.add(score.howYouFeel_better)
            getString(R.string.howYouFeel_no_better) -> scoreList.add(score.howYouFeel_no_better)
            getString(R.string.howYouFeel_worse) -> scoreList.add(score.howYouFeel_worse)
        }

        if ( viewModelCheck.generalDiscomfort ) scoreList.add(score.generalDiscomfort)
        if ( viewModelCheck.itchyOrSoreThroat ) scoreList.add(score.itchyOrSoreThroat)
        if ( viewModelCheck.diarrhea ) scoreList.add(score.diarrhea)
        if ( viewModelCheck.badTasteInTheMouth ) scoreList.add(score.badTasteInTheMouth)
        if ( viewModelCheck.lossOfTasteInFood ) scoreList.add(score.lossOfTasteInFood)
        if ( viewModelCheck.lossOfSmell ) scoreList.add(score.lossOfSmell)
        if ( viewModelCheck.musclePains ) scoreList.add(score.musclePains)
        if ( viewModelCheck.chestOrBackPain ) scoreList.add(score.chestOrBackPain)
        if ( viewModelCheck.headache ) scoreList.add(score.headache)
        if ( viewModelCheck.wetCoughWithPhlegm ) scoreList.add(score.wetCoughWithPhlegm)
        if ( viewModelCheck.dryCough ) scoreList.add(score.dryCough)
        if ( viewModelCheck.chill ) scoreList.add(score.chill)
        if ( viewModelCheck.fever ) scoreList.add(score.fever)
        if ( viewModelCheck.fatigueWhenWalkingOrClimbingStairs ) scoreList.add(score.fatigueWhenWalkingOrClimbingStairs)
        if ( viewModelCheck.feelingShortOfBreathWithDailyActivities ) scoreList.add(score.feelingShortOfBreathWithDailyActivities)
        if ( viewModelCheck.respiratoryDistress ) scoreList.add(score.respiratoryDistress)
        if ( viewModelCheck.newConfusionOrInabilityToArouse ) scoreList.add(score.newConfusionOrInabilityToArouse)
        if ( viewModelCheck.bluishLipsOrFace ) scoreList.add(score.bluishLipsOrFace)

        when(viewModelCheck.temperatureRange) {
            getString(R.string.temperature_range_1) -> scoreList.add(score.temperature_range_1)
            getString(R.string.temperature_range_2) -> scoreList.add(score.temperature_range_2)
            getString(R.string.temperature_range_3) -> scoreList.add(score.temperature_range_3)
            getString(R.string.temperature_range_4) -> scoreList.add(score.temperature_range_4)
        }

        when(viewModelCheck.breathsPerMinuteRange) {
            getString(R.string.breath_range_1) -> scoreList.add(score.breath_range_1)
            getString(R.string.breath_range_2) -> scoreList.add(score.breath_range_2)
            getString(R.string.breath_range_3) -> scoreList.add(score.breath_range_3)
            getString(R.string.breath_range_4) -> scoreList.add(score.breath_range_4)
        }

        return sigmoideAlgorithm.calculate(scoreList)
    }

    private fun queryLastCheck() {
        val filter = TableLastCheckFilterInput.builder().identityId(
            TableStringFilterInput.builder().eq(AWSMobileClient.getInstance().identityId).build()
        ).build()
        viewModelCheck.mAWSAppSyncClient?.query(ListLastChecksQuery.builder().filter(filter).build())
            ?.responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
            ?.enqueue(callbackLastCheck)
    }

    private val callbackLastCheck: GraphQLCall.Callback<ListLastChecksQuery.Data?> =
        object : GraphQLCall.Callback<ListLastChecksQuery.Data?>() {
            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("ERROR", e.toString())
            }
            override fun onResponse(response: com.apollographql.apollo.api.Response<ListLastChecksQuery.Data?>) {
                if(response.data()!!.listLastChecks()!!.items()!!.size != 0) {
                    updateLastCheck()
                } else {
                    createLastCheck()
                }
            }
        }

    private fun updateLastCheck() {
        val updateLastCheckInput = UpdateLastCheckInput.builder()
            .organizationId(ORGANIZATION_ID)
            .identityId(AWSMobileClient.getInstance().identityId)
            .checkTimestamp(viewModelCheck.timeStampLongId)
            .riskResult(viewModelCheck.riskResult.toString())
            .riskScore(viewModelCheck.riskScore)
            .temperatureRange(viewModelCheck.temperatureRange)
            .breathsPerMinuteRange(viewModelCheck.breathsPerMinuteRange)
            .howYouFeel(viewModelCheck.howYouFeel)
            .generalDiscomfort(viewModelCheck.generalDiscomfort)
            .itchyOrSoreThroat(viewModelCheck.itchyOrSoreThroat)
            .diarrhea(viewModelCheck.diarrhea)
            .badTasteInTheMouth(viewModelCheck.badTasteInTheMouth)
            .lossOfTasteInFood(viewModelCheck.lossOfTasteInFood)
            .lossOfSmell(viewModelCheck.lossOfSmell)
            .musclePains(viewModelCheck.musclePains)
            .chestOrBackPain(viewModelCheck.chestOrBackPain)
            .headache(viewModelCheck.headache)
            .wetCoughWithPhlegm(viewModelCheck.wetCoughWithPhlegm)
            .dryCough(viewModelCheck.dryCough)
            .chill(viewModelCheck.chill)
            .fever(viewModelCheck.fever)
            .fatigueWhenWalkingOrClimbingStairs(viewModelCheck.fatigueWhenWalkingOrClimbingStairs)
            .feelingShortOfBreathWithDailyActivities(viewModelCheck.feelingShortOfBreathWithDailyActivities)
            .respiratoryDistress(viewModelCheck.respiratoryDistress)
            .confusion(viewModelCheck.newConfusionOrInabilityToArouse)
            .bluishLipsOrFace(viewModelCheck.bluishLipsOrFace)
            .otherSymptomsOrDiscomfort(viewModelCheck.otherSymptomsOrDiscomfort).build()

        viewModelCheck.mAWSAppSyncClient?.mutate(UpdateLastCheckMutation.builder().input(updateLastCheckInput).build())
            ?.enqueue(mutationCallbackUpdateLastCheck)
    }

    private val mutationCallbackUpdateLastCheck: GraphQLCall.Callback<UpdateLastCheckMutation.Data?> =
        object : GraphQLCall.Callback<UpdateLastCheckMutation.Data?>() {
            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("Error", e.toString())
            }
            override fun onResponse(response: com.apollographql.apollo.api.Response<UpdateLastCheckMutation.Data?>) {
                Log.i(this.javaClass.canonicalName , "Last check was added to database.");
            }
        }

    private fun createLastCheck() {
        val updateLastCheckInput = CreateLastCheckInput.builder()
            .organizationId(ORGANIZATION_ID)
            .identityId(AWSMobileClient.getInstance().identityId)
            .checkTimestamp(viewModelCheck.timeStampLongId)
            .riskResult(viewModelCheck.riskResult.toString())
            .riskScore(viewModelCheck.riskScore)
            .temperatureRange(viewModelCheck.temperatureRange)
            .breathsPerMinuteRange(viewModelCheck.breathsPerMinuteRange)
            .howYouFeel(viewModelCheck.howYouFeel)
            .generalDiscomfort(viewModelCheck.generalDiscomfort)
            .itchyOrSoreThroat(viewModelCheck.itchyOrSoreThroat)
            .diarrhea(viewModelCheck.diarrhea)
            .badTasteInTheMouth(viewModelCheck.badTasteInTheMouth)
            .lossOfTasteInFood(viewModelCheck.lossOfTasteInFood)
            .lossOfSmell(viewModelCheck.lossOfSmell)
            .musclePains(viewModelCheck.musclePains)
            .chestOrBackPain(viewModelCheck.chestOrBackPain)
            .headache(viewModelCheck.headache)
            .wetCoughWithPhlegm(viewModelCheck.wetCoughWithPhlegm)
            .dryCough(viewModelCheck.dryCough)
            .chill(viewModelCheck.chill)
            .fever(viewModelCheck.fever)
            .fatigueWhenWalkingOrClimbingStairs(viewModelCheck.fatigueWhenWalkingOrClimbingStairs)
            .feelingShortOfBreathWithDailyActivities(viewModelCheck.feelingShortOfBreathWithDailyActivities)
            .respiratoryDistress(viewModelCheck.respiratoryDistress)
            .confusion(viewModelCheck.newConfusionOrInabilityToArouse)
            .bluishLipsOrFace(viewModelCheck.bluishLipsOrFace)
            .otherSymptomsOrDiscomfort(viewModelCheck.otherSymptomsOrDiscomfort).build()

        viewModelCheck.mAWSAppSyncClient?.mutate(CreateLastCheckMutation.builder().input(updateLastCheckInput).build())
            ?.enqueue(mutationCallbackCreateLastCheck)
    }

    private val mutationCallbackCreateLastCheck: GraphQLCall.Callback<CreateLastCheckMutation.Data?> =
        object : GraphQLCall.Callback<CreateLastCheckMutation.Data?>() {
            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("Error", e.toString())
            }
            override fun onResponse(response: com.apollographql.apollo.api.Response<CreateLastCheckMutation.Data?>) {
                Log.i(this.javaClass.canonicalName , "Last check was added to database.");
            }
        }
}
