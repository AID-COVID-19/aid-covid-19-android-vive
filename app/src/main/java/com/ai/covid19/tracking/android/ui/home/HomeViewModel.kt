package com.ai.covid19.tracking.android.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.amazonaws.amplify.generated.graphql.ListChecksQuery
import com.amazonaws.amplify.generated.graphql.ListPatientProfilesQuery
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import javax.annotation.Nonnull

class HomeViewModel : ViewModel() {
    private var mAWSAppSyncClient: AWSAppSyncClient? = null

    fun createSynClient(context: Context) {
        mAWSAppSyncClient = AWSAppSyncClient.builder()
            .context(context)
            .awsConfiguration(AWSConfiguration(context))
            .credentialsProvider(AWSMobileClient.getInstance()).build()
    }

    fun requestUserChecksHistory() {
        mAWSAppSyncClient?.apply {
            query(ListChecksQuery.builder().build())
                ?.responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                ?.enqueue(userChecksCallback)
        }
    }

    private val userChecksCallback: GraphQLCall.Callback<ListChecksQuery.Data?> =
        object : GraphQLCall.Callback<ListChecksQuery.Data?>() {
            override fun onResponse(@Nonnull response: Response<ListChecksQuery.Data?>) {
                Log.i("Results", response.data()?.listChecks()?.items().toString())
            }

            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("ERROR", e.toString())
            }
        }
}