package com.ai.covid19.tracking.android.ui.check

import android.content.Context
import com.ai.covid19.tracking.android.R
import com.soywiz.klock.DateTime
import com.soywiz.klock.until

class RiskAlgorithm(
    private val context: Context,
    private val temperature_range: String,
    private val headache: Boolean = false,
    private val breath_range: String,
    private val chestOrBackPain: Boolean,
    private val last12hTemperatures: Map<Long, Boolean>
) {

    enum class RiskClassification {
        /**
         * HIGH vital urgency
         *   MODERATE priority consultation
         *   LOW non-priority consultation
         *   **/
        HIGH, MODERATE, LOW
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
        if (breath_range == context.getString(R.string.breath_range_4)
            && chestOrBackPain
            && (temperature_range == context.getString(R.string.temperature_range_4)
                    || temperature_range == context.getString(R.string.temperature_range_3)
                    )
        ) {
            return if (persistentFever(last12hTemperatures)){
                RiskClassification.HIGH
            } else {
                RiskClassification.MODERATE
            }
        } else if (breath_range == context.getString(R.string.breath_range_3)
            && headache
            && (temperature_range == context.getString(R.string.temperature_range_4)
                    || temperature_range == context.getString(R.string.temperature_range_3)
                    )
        ) {
            riskClassification = RiskClassification.MODERATE
        } else {
            riskClassification = RiskClassification.LOW
        }
        return riskClassification
    }

    /**
     *  Give a list of booleans with it's timestamp, of last 12 hours measures, where true means fever and false not fever.
     *  persistentFever = true if every 4 hours had fever,
     *  in the 4 hours windows, fever = true when the early measure in the timeline is true
     **/
    var persistentFever = false

    private fun persistentFever(last12hTemperatures: Map<Long, Boolean>) : Boolean{
        var hadFeverWindows4h_1 = false
        var hadFeverWindows4h_2 = false
        var hadFeverWindows4h_3 = false
        val sortedLast12hTemperatures = last12hTemperatures.toSortedMap()

        if((DateTime.fromUnix(sortedLast12hTemperatures.firstKey()) until DateTime.fromUnix(sortedLast12hTemperatures.lastKey()))
            .span.hours > 12)
            throw Exception("The given list have a hour timestamp windows grater than 12 hours. Should be equal or less.")

        var date1: DateTime? = null
        var date2: DateTime?
        var hours: Int
        sortedLast12hTemperatures.forEach{
            if ( date1 == null) {
                date1 = DateTime.fromUnix(it.key)
                hadFeverWindows4h_1 = it.value
            }
            else {
                date2 = DateTime.fromUnix(it.key)
                hadFeverWindows4h_2 = it.value
                hours = (date1!! until date2!!).span.hours
                when {
                    hours <= 4 -> {
                        hadFeverWindows4h_1 = it.value
                    }
                    hours in 5..8 -> {
                        hadFeverWindows4h_2 = it.value
                    }
                    else -> {
                        hadFeverWindows4h_3 = it.value
                    }
                }
            }
        }
        persistentFever = hadFeverWindows4h_1 && hadFeverWindows4h_2 && hadFeverWindows4h_3
        return persistentFever
    }
}