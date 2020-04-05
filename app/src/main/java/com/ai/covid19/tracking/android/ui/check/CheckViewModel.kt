package com.ai.covid19.tracking.android.ui.check

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.mobile.client.AWSMobileClient

class CheckViewModel : ViewModel() {

    var howYouFeel: String? = null
    var generalDiscomfort: Boolean? = null
    var itchyOrSoreThroat: Boolean? = null
    var diarrhea: Boolean? = null
    var badTasteInTheMouth: Boolean? = null
    var lossOfTasteInFood: Boolean? = null
    var lossOfSmell: Boolean? = null
    var musclePains: Boolean? = null
    var chestOrBackPain: Boolean? = null
    var headache: Boolean? = null
    var wetCoughWithPhlegm: Boolean? = null
    var dryCough: Boolean? = null
    var chill: Boolean? = null
    var fever: Boolean? = null
    var fatigueWhenWalkingOrClimbingStairs: Boolean? = null
    var feelingShortOfBreathWithDailyActivities: Boolean? = null
    var feelingOfStrainingToBreathe: Boolean? = null
    var respiratoryDistress: Boolean? = null
    var dizziness: Boolean? = null
    var otherSymptomsOrDiscomfort: String? = null
    var temperatureRange: String? = null
    var breathsPerMinuteRange: String? = null
    var bloodPressureLowValue: Int? = null
    var bloodPressureHighValue: Int? = null
    var haveYouBeenNervousOrAnxious: String? = null
    var couldntStopBeingWorried: String? = null
    var haveYouWorriedTooMuchAboutDifferentThings: String? = null
    var haveYouHadDifficultyRelaxing: String? = null
    var haveYouBeenSoRestlessThatItIsDifficultToStayStill: String? = null
    var haveYouBecomeEasilyAnnoyedOrIrritable: String? = null
    var haveYouFeltFearAsIfSomethingHorribleWasGoingToHappen: String? = null
    var areYouTakingYourMedications: String? = null

    init {
        AWSMobileClient.getInstance().userAttributes[""]
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is Check Fragment"
    }
    val text: LiveData<String> = _text
}