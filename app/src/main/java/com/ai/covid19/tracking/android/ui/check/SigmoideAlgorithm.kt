package com.ai.covid19.tracking.android.ui.check

import kotlin.math.exp

class SigmoideAlgorithm(){

    fun calculateFromStringValues(values: List<String>) : Float{
        var score = 0.0f
        for(  value in values ){
            score += value.toFloat()
        }
        return calculate(score)
    }

    fun calculate(values: List<Float>) : Float{
        var score = 0.0f
        for(  value in values ){
            score += value
        }
        return calculate(score)
    }

    fun calculate(score: Float) : Float {
        return 1/(1+ exp(-score))
    }
}