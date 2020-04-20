package com.ai.covid19.tracking.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ai.covid19.tracking.android.ui.doctor.dashboard.DoctorDashboardViewModel
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import kotlinx.android.synthetic.main.activity_main.*

class DoctorMainActivity : AppCompatActivity() {

    private val doctorDashboardViewModel: DoctorDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_main)

        amplifySetup()

        // Finding the Navigation Controller
        val navController = findNavController(R.id.nav_doctor_host_fragment)

        // Setting Navigation Controller with the BottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
    }

    fun openAlarmActivity(view: View) {
        val intent = Intent(this, PatientCheckActivity::class.java)
        startActivity(intent)
    }

    private fun amplifySetup(){
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails?> {
                override fun onError(e: Exception?) {
                    Log.e("INIT", "Initialization error.", e)
                }
                override fun onResult(result: UserStateDetails?) {
                    Log.i("INIT", "onResult: " + result?.userState)
                }
            })
        doctorDashboardViewModel.mAWSAppSyncClient = AWSAppSyncClient.builder()
            .context(applicationContext)
            .awsConfiguration(AWSConfiguration(applicationContext))
            .credentialsProvider(AWSMobileClient.getInstance()).build()
    }

}
