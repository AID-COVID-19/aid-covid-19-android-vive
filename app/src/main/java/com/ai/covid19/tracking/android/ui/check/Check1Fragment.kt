package com.ai.covid19.tracking.android.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ai.covid19.tracking.android.R
import com.ai.covid19.tracking.android.databinding.FragmentCheck1Binding
import kotlinx.android.synthetic.main.fragment_check_1.button_next_check_1

class Check1Fragment : Fragment(), View.OnClickListener {

    private val viewModelCheck1: CheckViewModel by viewModels()
    private lateinit var binding: FragmentCheck1Binding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheck1Binding.inflate(inflater,container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nav = findNavController()
        val listener: (View) -> Unit = {
            nav.navigate(R.id.action_check1Fragment_to_check2Fragment)
        }
        button_next_check_1.setOnClickListener(listener)
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}
