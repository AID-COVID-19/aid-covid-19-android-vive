package com.ai.covid19.tracking.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.ai.covid19.tracking.android.R
import com.ai.covid19.tracking.android.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater,container, false)

        val patientChecksAdapter = PatientChecksAdapter(homeViewModel, viewLifecycleOwner)
        binding.patientChecksHistoryRecyclerView.adapter = patientChecksAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            homeViewModel.createSynClient(it)
            homeViewModel.requestUserChecksHistory()
        }
    }
}
