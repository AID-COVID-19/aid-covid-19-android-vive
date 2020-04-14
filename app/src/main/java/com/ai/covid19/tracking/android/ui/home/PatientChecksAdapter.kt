package com.ai.covid19.tracking.android.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.ai.covid19.tracking.android.R
import kotlinx.android.synthetic.main.item_patient_check.view.*

class PatientChecksAdapter(
    private val homeViewModel: HomeViewModel,
    viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<PatientChecksAdapter.PatientCheckViewHolder>() {

    init {
        homeViewModel.patientChecksLiveData.observe(viewLifecycleOwner, Observer {
            notifyDataSetChanged()
        })
    }

    class PatientCheckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientCheckNameTextView: TextView = itemView.patientCheckItemTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientCheckViewHolder {
        return PatientCheckViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_patient_check, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PatientCheckViewHolder, position: Int) {
        holder.patientCheckNameTextView.text =
            homeViewModel.patientChecksLiveData.value?.get(position)?.checkTimestamp().toString()
    }

    override fun getItemCount(): Int {
        return homeViewModel.patientChecksLiveData.value?.size ?: 0
    }
}
