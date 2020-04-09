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
import com.ai.covid19.tracking.android.databinding.FragmentCheck1Binding
import com.amazonaws.amplify.generated.graphql.CreateCheckMutation
import com.amazonaws.mobile.client.AWSMobileClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.soywiz.klock.DateTime
import kotlinx.android.synthetic.main.fragment_check_1.*
import type.CreateCheckInput
import javax.annotation.Nonnull


class Check1Fragment : Fragment() {

    private val viewModelCheck: CheckViewModel by activityViewModels()
    private lateinit var binding: FragmentCheck1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheck1Binding.inflate(inflater, container, false)
        howYouFeelSetup()
        botherSetup()
        otherSymptomsOrDiscomfortSetup()
        botherFillContent()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listener: (View) -> Unit = {
            saveCheck1()
        }
        button_next_check_1.setOnClickListener(listener)
    }

    private fun howYouFeelSetup() {
        binding.howYouFeel.check(viewModelCheck.howYouFeelSelectedId)
        binding.howYouFeel.setOnCheckedChangeListener { _, checkedId ->
            viewModelCheck.howYouFeelSelectedId = checkedId
            viewModelCheck.howYouFeel = when (checkedId) {
                R.id.howYouFeel_better -> getString(R.string.howYouFeel_better)
                R.id.howYouFeel_no_better -> getString(R.string.howYouFeel_no_better)
                R.id.howYouFeel_worse -> getString(R.string.howYouFeel_worse)
                else -> null
            }
        }
    }

    private fun botherSetup() {
        binding.generalDiscomfort.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.itchyOrSoreThroat.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.diarrhea.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.badTasteInTheMouth.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.lossOfTasteInFood.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.lossOfSmell.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.musclePains.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.chestOrBackPain.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.headache.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.wetCoughWithPhlegm.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.dryCough.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.chill.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.fever.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.fatigueWhenWalkingOrClimbingStairs.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.feelingShortOfBreathWithDailyActivities.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.respiratoryDistress.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
        binding.dizziness.setOnCheckedChangeListener { view, isChecked ->
            botherOptions( view.id, isChecked )
        }
    }

    private fun botherOptions(checkedId: Int, isChecked: Boolean) {
        when (checkedId) {
            R.id.generalDiscomfort -> viewModelCheck.generalDiscomfort = isChecked
            R.id.itchyOrSoreThroat -> viewModelCheck.itchyOrSoreThroat = isChecked
            R.id.diarrhea -> viewModelCheck.diarrhea = isChecked
            R.id.badTasteInTheMouth -> viewModelCheck.badTasteInTheMouth = isChecked
            R.id.lossOfTasteInFood ->viewModelCheck.lossOfTasteInFood = isChecked
            R.id.lossOfSmell -> viewModelCheck.lossOfSmell = isChecked
            R.id.musclePains -> viewModelCheck.musclePains = isChecked
            R.id.chestOrBackPain -> viewModelCheck.chestOrBackPain = isChecked
            R.id.headache -> viewModelCheck.headache = isChecked
            R.id.wetCoughWithPhlegm -> viewModelCheck.wetCoughWithPhlegm = isChecked
            R.id.dryCough -> viewModelCheck.dryCough = isChecked
            R.id.chill -> viewModelCheck.chill = isChecked
            R.id.fever -> viewModelCheck.fever = isChecked
            R.id.fatigueWhenWalkingOrClimbingStairs -> viewModelCheck.fatigueWhenWalkingOrClimbingStairs = isChecked
            R.id.feelingShortOfBreathWithDailyActivities -> viewModelCheck.feelingShortOfBreathWithDailyActivities = isChecked
            R.id.respiratoryDistress -> viewModelCheck.respiratoryDistress = isChecked
            R.id.dizziness -> viewModelCheck.dizziness = isChecked
        }
    }

    private fun botherFillContent() {
        binding.generalDiscomfort.isChecked = viewModelCheck.generalDiscomfort
        binding.itchyOrSoreThroat.isChecked = viewModelCheck.itchyOrSoreThroat
        binding.diarrhea.isChecked = viewModelCheck.diarrhea
        binding.badTasteInTheMouth.isChecked = viewModelCheck.badTasteInTheMouth
        binding.lossOfTasteInFood.isChecked = viewModelCheck.lossOfTasteInFood
        binding.lossOfSmell.isChecked = viewModelCheck.lossOfSmell
        binding.musclePains.isChecked = viewModelCheck.musclePains
        binding.chestOrBackPain.isChecked = viewModelCheck.chestOrBackPain
        binding.headache.isChecked = viewModelCheck.headache
        binding.wetCoughWithPhlegm.isChecked = viewModelCheck.wetCoughWithPhlegm
        binding.dryCough.isChecked = viewModelCheck.dryCough
        binding.chill.isChecked = viewModelCheck.chill
        binding.fever.isChecked = viewModelCheck.fever
        binding.fatigueWhenWalkingOrClimbingStairs.isChecked = viewModelCheck.fatigueWhenWalkingOrClimbingStairs
        binding.feelingShortOfBreathWithDailyActivities.isChecked = viewModelCheck.feelingShortOfBreathWithDailyActivities
        binding.respiratoryDistress.isChecked = viewModelCheck.respiratoryDistress
        binding.dizziness.isChecked = viewModelCheck.dizziness
        if(!viewModelCheck.otherSymptomsOrDiscomfort.isNullOrBlank())
            binding.otherSymptomsOrDiscomfort.setText(viewModelCheck.otherSymptomsOrDiscomfort)
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

    private fun otherSymptomsOrDiscomfortSetup(){
        binding.otherSymptomsOrDiscomfort.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (!s.isBlank())
                    viewModelCheck.otherSymptomsOrDiscomfort = s.toString()
            }
        })
    }
}
