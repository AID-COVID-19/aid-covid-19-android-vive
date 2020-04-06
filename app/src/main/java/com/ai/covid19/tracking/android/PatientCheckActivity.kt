package com.ai.covid19.tracking.android

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.ai.covid19.tracking.android.ui.check.CheckViewModel
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient


class PatientCheckActivity : AppCompatActivity() {

    private val viewModelCheck: CheckViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_check)
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_alarm) as NavHostFragment? ?: return
        val navController = host.navController
        amplifySetup()
    }
    fun startPatientCheck(view: View){
        Navigation.createNavigateOnClickListener(R.id.check1Fragment)
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
        viewModelCheck.mAWSAppSyncClient = AWSAppSyncClient.builder()
            .context(applicationContext)
            .awsConfiguration(AWSConfiguration(applicationContext))
            .credentialsProvider(AWSMobileClient.getInstance()).build()

    }
}
