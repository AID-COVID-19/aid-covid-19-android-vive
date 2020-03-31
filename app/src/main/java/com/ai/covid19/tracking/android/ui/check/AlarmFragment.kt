package com.ai.covid19.tracking.android.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.ai.covid19.tracking.android.R
import kotlinx.android.synthetic.main.activity_patient_check.*
import kotlinx.android.synthetic.main.fragment_alarm.button_start

class AlarmFragment : Fragment() {

    private lateinit var viewModel: AlarmViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel =
                ViewModelProviders.of(this).get(AlarmViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nav = findNavController()
        val listener: (View) -> Unit = {
            nav.navigate(R.id.action_alarmFragment_to_check1Fragment)
        }
        button_start.setOnClickListener(listener)
    }
}
