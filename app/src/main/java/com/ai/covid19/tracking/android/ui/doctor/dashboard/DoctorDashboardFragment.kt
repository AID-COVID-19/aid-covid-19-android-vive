package com.ai.covid19.tracking.android.ui.doctor.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ai.covid19.tracking.android.databinding.FragmentCheck1Binding

class DoctorDashboardFragment : Fragment() {

    private val viewModelDoctorDashboard: DoctorDashboardViewModel by activityViewModels()
    private lateinit var binding: FragmentCheck1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnListener: (View) -> Unit = {

        }
    }
}
