package com.ai.covid19.tracking.android.ui.doctor.dashboard

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ai.covid19.tracking.android.R
import com.ai.covid19.tracking.android.ui.check.RiskAlgorithm
import com.amazonaws.amplify.generated.graphql.QueryLastChecksByIdentityIdCheckTimestampIndexQuery
import com.soywiz.klock.DateTime
import kotlinx.android.synthetic.main.card_summary_check.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class CheckSummaryAdapter(private val myDataset: QueryLastChecksByIdentityIdCheckTimestampIndexQuery.QueryLastChecksByIdentityIdCheckTimestampIndex?,
                          private val context: Context) :
    RecyclerView.Adapter<CheckSummaryAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_summary_check , parent, false) as CardView
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (myDataset != null && myDataset.items()!!.size > 0) {
            holder.cardView.backgroundTintList = (setBackground(myDataset.items()!![position].RiskResult()))
            holder.cardView.txtRisk.text = risk(myDataset.items()!![position].RiskScore()!!, myDataset.items()!![position].RiskResult())
            holder.cardView.txtFeel.text = howYouFeel(myDataset.items()!![position].howYouFeel())
            holder.cardView.textTime.text = DateTime.fromUnix(myDataset.items()!![position].checkTimestamp()).local.toString("EEE, dd MMM yyyy HH:mm")
            holder.cardView.txtVitalSigns.text = vitalSignals(myDataset.items()!![position].temperatureRange()!!, myDataset.items()!![position].breathsPerMinuteRange()!!)
            if(symptomsOrDiscomfort(position) == null){
                holder.cardView.txtSymptom.visibility = View.GONE
            } else {
                holder.cardView.txtSymptom.text = symptomsOrDiscomfort(position)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset?.items()!!.size

    private fun setBackground (riskResult: String?) : ColorStateList? {
        return when (riskResult) {
            RiskAlgorithm.RiskClassification.HIGH.toString() -> ContextCompat.getColorStateList(context, R.color.colorBackgroundRedAlert)
            RiskAlgorithm.RiskClassification.MODERATE.toString()  -> ContextCompat.getColorStateList(context, R.color.colorBackgroundOrangeAlert)
            RiskAlgorithm.RiskClassification.LOW.toString()  -> ContextCompat.getColorStateList(context, R.color.colorBackgroundGreenAlert)
            else -> ContextCompat.getColorStateList(context, R.color.colorBackgroundLight)
        }
    }

    private fun howYouFeel (value: String?) : String? {
        var stringResult: String? = context.getString(R.string.card_feel_pro)
        stringResult += when (value) {
            context.getString(R.string.howYouFeel_better) -> context.getString(R.string.check_1_q_1_a_1)
            context.getString(R.string.howYouFeel_no_better) -> context.getString(R.string.check_1_q_1_a_2)
            context.getString(R.string.howYouFeel_worse) -> context.getString(R.string.check_1_q_1_a_3)
            else -> null
        }
        return stringResult
    }

    private fun risk (riskScore: Double, riskResult: String?) : String? {
        val roundScore = BigDecimal(riskScore).setScale(6, RoundingMode.HALF_EVEN)
        var stringResult = "$roundScore / "
        stringResult +=  when (riskResult) {
            RiskAlgorithm.RiskClassification.HIGH.toString() -> context.getString(R.string.check_result_high_title)
            RiskAlgorithm.RiskClassification.MODERATE.toString()  -> context.getString(R.string.check_result_moderated_title)
            RiskAlgorithm.RiskClassification.LOW.toString()  -> context.getString(R.string.check_result_low_title)
            else -> null
        }
        return stringResult
    }

    private fun vitalSignals (temperature: String, breath: String ) : String? {
        var  stringResult = "T: "
        stringResult += when (temperature) {
            context.getString(R.string.temperature_range_1) -> context.getString(R.string.card_vital_signs_T_1)
            context.getString(R.string.temperature_range_2) -> context.getString(R.string.card_vital_signs_T_2)
            context.getString(R.string.temperature_range_3) -> context.getString(R.string.card_vital_signs_T_3)
            context.getString(R.string.temperature_range_4) -> context.getString(R.string.card_vital_signs_T_4)
            else -> " "
        }
        stringResult += " | R: "
        stringResult += when (breath) {
            context.getString(R.string.breath_range_1) -> context.getString(R.string.card_vital_signs_R_1)
            context.getString(R.string.breath_range_2) -> context.getString(R.string.card_vital_signs_R_2)
            context.getString(R.string.breath_range_3) -> context.getString(R.string.card_vital_signs_R_3)
            context.getString(R.string.breath_range_4) -> context.getString(R.string.card_vital_signs_R_4)
            else -> " "
        }
        return stringResult
    }

    private fun symptomsOrDiscomfort (position: Int): String? {
        var stringResult = ""
        if(myDataset!!.items()!![position].generalDiscomfort()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_1) + " | "
        if(myDataset.items()!![position].itchyOrSoreThroat()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_2) + " | "
        if(myDataset.items()!![position].diarrhea()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_3) + " | "
        if(myDataset.items()!![position].badTasteInTheMouth()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_4) + " | "
        if(myDataset.items()!![position].lossOfTasteInFood()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_5) + " | "
        if(myDataset.items()!![position].lossOfSmell()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_6) + " | "
        if(myDataset.items()!![position].musclePains()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_7) + " | "
        if(myDataset.items()!![position].chestOrBackPain()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_8) + " | "
        if(myDataset.items()!![position].headache()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_9) + " | "
        if(myDataset.items()!![position].wetCoughWithPhlegm()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_10) + " | "
        if(myDataset.items()!![position].dryCough()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_11) + " | "
        if(myDataset.items()!![position].chill()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_12) + " | "
        if(myDataset.items()!![position].fever()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_13) + " | "
        if(myDataset.items()!![position].fatigueWhenWalkingOrClimbingStairs()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_14) + " | "
        if(myDataset.items()!![position].feelingShortOfBreathWithDailyActivities()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_15) + " | "
        if(myDataset.items()!![position].respiratoryDistress()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_17) + " | "
        if(myDataset.items()!![position].confusion()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_18) + " | "
        if(myDataset.items()!![position].bluishLipsOrFace()!!)
            stringResult += context.getString(R.string.check_1_q_2_a_19) + " | "

        if (stringResult.isBlank())
            return null
        return stringResult.dropLast(2)
    }
}