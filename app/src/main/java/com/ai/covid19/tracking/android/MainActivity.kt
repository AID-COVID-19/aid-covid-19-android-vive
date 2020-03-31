package com.ai.covid19.tracking.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Finding the Navigation Controller
        val navController = findNavController(R.id.nav_host_fragment)

        // Setting Navigation Controller with the BottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
    }

    fun openAlarmActivity(view: View) {
        val intent = Intent(this, PatientCheckActivity::class.java)
        startActivity(intent)
    }
}
