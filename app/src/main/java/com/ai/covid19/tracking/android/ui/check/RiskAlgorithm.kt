package com.ai.covid19.tracking.android.ui.check

import android.content.Context
import com.ai.covid19.tracking.android.R
import com.soywiz.klock.DateTime
import com.soywiz.klock.until

class RiskAlgorithm(
    private val context: Context,
    private val temperature_range: String?,
    private val headache: Boolean = false,
    private val breath_range: String?,
    private val last12hPainChestMeasures: Map<Long, Boolean?>,
    private val bluishLipsOrFace: Boolean = false,
    private val newConfusionOrInabilityToArouse: Boolean = false
) {

    enum class RiskClassification {
        /**
         * HIGH vital urgency
         *   MODERATE priority consultation
         *   LOW non-priority consultation
         *   **/
        HIGH, MODERATE, LOW, DISCARDED
    }

    lateinit var riskClassification: RiskClassification

    /**
     *  Given the variables in the constructor, the patient's risk is calculated in this method.
     *  temperature_range_4 = greater than 39ºC
     *  temperature_range_3 = between 38ºC and 39ºC
     *  breath_range_4 = between 20 and 28
     *  breath_range_3 = greater than 28
     *  **/
    fun calculateRisk() : RiskClassification  {
        riskClassification = if (breath_range == context.getString(R.string.breath_range_4)
            || persistentChestPain(last12hPainChestMeasures)!! || bluishLipsOrFace || newConfusionOrInabilityToArouse
        ) {
            return RiskClassification.HIGH
        } else if (breath_range == context.getString(R.string.breath_range_3)
            || headache
            || (temperature_range == context.getString(R.string.temperature_range_4)
                    || temperature_range == context.getString(R.string.temperature_range_3)
                    )
        ) {
            RiskClassification.MODERATE
        } else {
            RiskClassification.LOW
        }
        return riskClassification
    }

    /**
     *  Give a list of booleans with it's timestamp, of last 12 hours measures, where true means chest pain and false not pain.
     *  persistentChestPain = true if every window time had it
     **/
    var persistentChestPain: Boolean? = false
    var date1: DateTime? = null
    var date2: DateTime? = null

    private fun persistentChestPain(last12hMeasures: Map<Long, Boolean?>) : Boolean? {
        var windows4h_1: Boolean? = false
        var windows4h_2: Boolean? = false
        var windows4h_3: Boolean? = false
        val sortedLast12hMeasures = last12hMeasures.toSortedMap()

        if((DateTime.fromUnix(sortedLast12hMeasures.firstKey()) until DateTime.fromUnix(sortedLast12hMeasures.lastKey()))
            .span.hours > 99)
            throw Exception("The given list have a hour timestamp windows grater than 12 hours. Should be equal or less.")

        sortedLast12hMeasures.forEach{
            if ( date1 == null) {
                date1 = DateTime.fromUnix(it.key)
                windows4h_1 = it.value
            }
            else {
                date2 = DateTime.fromUnix(it.key)
                when ((date1!! until date2!!).span.hours) {
                    in 0..1 -> {
                        windows4h_1 = it.value
                    }
                    in 4..6 -> {
                        windows4h_2 = it.value
                    }
                    in 7..12 -> {
                        windows4h_3 = it.value
                    }
                }
            }
        }
        persistentChestPain = windows4h_1!! && windows4h_2!! && windows4h_3!!
        return persistentChestPain
    }
}