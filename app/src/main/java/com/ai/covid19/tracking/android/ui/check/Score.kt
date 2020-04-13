package com.ai.covid19.tracking.android.ui.check

data class Score (
    val howYouFeel_better: Double = -1.0,
    val howYouFeel_no_better: Double = 0.0,
    val howYouFeel_worse: Double = 1.0,

    val generalDiscomfort: Double = 0.1,
    val itchyOrSoreThroat: Double = 0.1,
    val diarrhea: Double = 0.1,
    val badTasteInTheMouth: Double = 0.1,
    val lossOfTasteInFood: Double = 0.1,
    val lossOfSmell: Double = 0.1,
    val musclePains: Double = 0.1,
    val chestOrBackPain: Double = 0.1,
    val headache: Double = 0.1,
    val wetCoughWithPhlegm: Double = 0.1,
    val dryCough: Double = 0.1,
    val chill: Double = 0.2,
    val fever: Double = 0.2,
    val fatigueWhenWalkingOrClimbingStairs: Double = 0.5,
    val feelingShortOfBreathWithDailyActivities: Double = 0.5,
    val respiratoryDistress: Double = 1.0,
    val newConfusionOrInabilityToArouse: Double = 1.0,
    val bluishLipsOrFace: Double = 1.0,

    val temperature_range_1: Double = 1.0,
    val temperature_range_2: Double = -1.0,
    val temperature_range_3: Double = 1.0,
    val temperature_range_4: Double = 2.0,

    val breath_range_1: Double = 1.0,
    val breath_range_2: Double = -1.0,
    val breath_range_3: Double = 1.0,
    val breath_range_4: Double = 2.0,

    val hypotension: Double = 0.25,
    val normal: Double = -1.0,
    val elevated: Double = 1.0,
    val stage_1_hypertension: Double = 2.0,
    val stage_2_hypertension: Double = 2.0,
    val hypertensive_crisis: Double = 3.0,

    val areYouTakingYourMedications_yes: Double = -1.0,
    val areYouTakingYourMedications_no: Double = 1.0,
    val areYouTakingYourMedications_I_finished: Double = 0.0
)