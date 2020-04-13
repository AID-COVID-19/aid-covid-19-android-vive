package com.ai.covid19.tracking.android.ui.check

import kotlin.math.exp

class SigmoideAlgorithm(){

    fun calculateFromStringValues(values: List<String>) : Double {
        var score = 0.0
        for(  value in values ){
            score += value.toDouble()
        }
        return calculate(score)
    }

    fun calculate(values: List<Double>) : Double {
        var score = 0.0
        for(  value in values ){
            score += value
        }
        return calculate(score)
    }

    fun calculate(score: Double) : Double {
        return 1/(1+ exp(-score))
    }
}