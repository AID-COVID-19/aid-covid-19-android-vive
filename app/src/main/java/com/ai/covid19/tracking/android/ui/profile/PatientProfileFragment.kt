package com.ai.covid19.tracking.android.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ai.covid19.tracking.android.R

class PatientProfileFragment : Fragment() {

    private lateinit var patientProfileViewModel: PatientProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        patientProfileViewModel =
                ViewModelProviders.of(this).get(PatientProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_patient_profile, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
        patientProfileViewModel.text.observe(viewLifecycleOwner, Observer {
  //          textView.text = it
        })
        return root
    }
}
