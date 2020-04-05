package com.ai.covid19.tracking.android.ui.check

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ai.covid19.tracking.android.R
import com.ai.covid19.tracking.android.databinding.FragmentCheck1Binding
import com.amazonaws.amplify.generated.graphql.CreateCheckMutation
import com.amazonaws.mobile.client.AWSMobileClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.soywiz.klock.DateTime
import kotlinx.android.synthetic.main.fragment_check_1.button_next_check_1
import type.CreateCheckInput
import javax.annotation.Nonnull

class Check1Fragment : Fragment(), View.OnClickListener {

    private val viewModelCheck: CheckViewModel by activityViewModels()
    private lateinit var binding: FragmentCheck1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheck1Binding.inflate(inflater, container, false)
        howYouFeelSetup()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listener: (View) -> Unit = {
            saveCheck1()
        }
        button_next_check_1.setOnClickListener(listener)
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    private fun howYouFeelSetup() {
        binding.howYouFeel.check(viewModelCheck.howYouFeelSelectedId)
        binding.howYouFeel.setOnCheckedChangeListener { _, checkedId ->
            viewModelCheck.howYouFeel = when (checkedId) {
                R.id.howYouFeel_better -> getString(R.string.howYouFeel_better)
                R.id.howYouFeel_no_better -> getString(R.string.howYouFeel_no_better)
                R.id.howYouFeel_worse -> getString(R.string.howYouFeel_worse)
                else -> null
            }
        }
    }

    private fun saveCheck1() {
        viewModelCheck.timeStampLongId = DateTime.now().unixMillisLong
        val createCheckInput = CreateCheckInput.builder()
            .identityId(AWSMobileClient.getInstance().identityId)
            .checkTimestamp(viewModelCheck.timeStampLongId)
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
            .feelingOfStrainingToBreathe(viewModelCheck.feelingOfStrainingToBreathe)
            .respiratoryDistress(viewModelCheck.respiratoryDistress)
            .dizziness(viewModelCheck.dizziness)
            .otherSymptomsOrDiscomfort(viewModelCheck.otherSymptomsOrDiscomfort).build()

        viewModelCheck.mAWSAppSyncClient?.mutate(CreateCheckMutation.builder().input(createCheckInput).build())
            ?.enqueue(mutationCallback)
    }

    private val mutationCallback: GraphQLCall.Callback<CreateCheckMutation.Data?> =
        object : GraphQLCall.Callback<CreateCheckMutation.Data?>() {
            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("Error", e.toString())
            }
            override fun onResponse(response: com.apollographql.apollo.api.Response<CreateCheckMutation.Data?>) {
                findNavController().navigate(R.id.action_check1Fragment_to_check2Fragment)
                Log.i(this.javaClass.canonicalName , "Check 1 was added to database.");
            }
        }
}
