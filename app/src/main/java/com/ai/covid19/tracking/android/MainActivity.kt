package com.ai.covid19.tracking.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.amazonaws.amplify.generated.graphql.ListPatientProfilesQuery
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.activity_main.*
import javax.annotation.Nonnull


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amplifySetup()

        // Finding the Navigation Controller
        val navController = findNavController(R.id.nav_host_fragment)

        // Setting Navigation Controller with the BottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
    }

    fun openAlarmActivity(view: View) {
        query()
        val intent = Intent(this, PatientCheckActivity::class.java)
        startActivity(intent)
    }

    private var mAWSAppSyncClient: AWSAppSyncClient? = null

    private fun amplifySetup(){
        mAWSAppSyncClient = AWSAppSyncClient.builder()
            .context(applicationContext)
            .awsConfiguration(AWSConfiguration(applicationContext)) // If you are using complex objects (S3) then uncomment
            //.s3ObjectManager(new S3ObjectManagerImplementation(new AmazonS3Client(AWSMobileClient.getInstance())))
            .build()
    }

    fun query() {
        mAWSAppSyncClient!!.query(ListPatientProfilesQuery.builder().build())
            .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
            .enqueue(todosCallback)
    }

    private val todosCallback: GraphQLCall.Callback<ListPatientProfilesQuery.Data?> =
        object : GraphQLCall.Callback<ListPatientProfilesQuery.Data?>() {
            override fun onResponse(@Nonnull response: Response<ListPatientProfilesQuery.Data?>) {
                Log.i("Results", response.data()?.listPatientProfiles()?.items().toString())
            }

            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("ERROR", e.toString())
            }
        }

}
