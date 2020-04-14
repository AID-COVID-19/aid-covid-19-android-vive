package com.ai.covid19.tracking.android.ui.doctor.dashboard

import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ai.covid19.tracking.android.databinding.FragmentDoctorDashboardBinding

class DoctorDashboardFragment : Fragment() {

    private val viewModelDoctorDashboard: DoctorDashboardViewModel by activityViewModels()
    private lateinit var binding: FragmentDoctorDashboardBinding
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val myDataset: Array<String> = arrayOf("hola", "t")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = CheckSummaryAdapter(myDataset)
        binding.recyclerDoctorDashboard.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnListener: (View) -> Unit = {

        }
    }
}
