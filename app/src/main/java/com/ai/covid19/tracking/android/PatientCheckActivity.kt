package com.ai.covid19.tracking.android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment


class PatientCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_check)
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_alarm) as NavHostFragment? ?: return
        val navController = host.navController
    }
    fun startPatientCheck(view: View){
        Navigation.createNavigateOnClickListener(R.id.check1Fragment)
    }
}
