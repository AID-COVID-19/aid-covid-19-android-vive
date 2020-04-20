package com.ai.covid19.tracking.android.ui.doctor.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ai.covid19.tracking.android.databinding.FragmentDoctorDashboardBinding
import com.ai.covid19.tracking.android.util.ORGANIZATION_ID
import com.amazonaws.amplify.generated.graphql.QueryLastChecksByIdentityIdCheckTimestampIndexQuery
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import type.TableLastCheckFilterInput
import type.TableStringFilterInput
import javax.annotation.Nonnull

class DoctorDashboardFragment : Fragment() {

    private val viewModelDoctorDashboard: DoctorDashboardViewModel by activityViewModels()
    private lateinit var binding: FragmentDoctorDashboardBinding
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoctorDashboardBinding.inflate(inflater, container, false)
        queryListLastCheck()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun queryListLastCheck() {
        val filter = TableLastCheckFilterInput.builder().organizationId(
            TableStringFilterInput.builder().eq(ORGANIZATION_ID).build()
        ).build()
        viewModelDoctorDashboard.mAWSAppSyncClient?.query(QueryLastChecksByIdentityIdCheckTimestampIndexQuery.builder().organizationId(
            ORGANIZATION_ID).build())
            ?.responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
            ?.enqueue(callbackLastCheck)
    }

    private val callbackLastCheck: GraphQLCall.Callback<QueryLastChecksByIdentityIdCheckTimestampIndexQuery.Data?> =
        object : GraphQLCall.Callback<QueryLastChecksByIdentityIdCheckTimestampIndexQuery.Data?>() {
            override fun onFailure(@Nonnull e: ApolloException) {
                Log.e("ERROR", e.toString())
            }
            override fun onResponse(response: com.apollographql.apollo.api.Response<QueryLastChecksByIdentityIdCheckTimestampIndexQuery.Data?>) {
                viewModelDoctorDashboard.listLastCheck = response.data()!!.queryLastChecksByIdentityIdCheckTimestampIndex()!!
                ThreadUtils.runOnUiThread {
                    setupList()
                }
            }
        }

    private fun setupList () {
        if(viewModelDoctorDashboard.listLastCheck != null && viewModelDoctorDashboard.listLastCheck!!.items()!!.size != 0) {
            viewManager = LinearLayoutManager(requireContext())
            viewAdapter = CheckSummaryAdapter(viewModelDoctorDashboard.listLastCheck, requireContext())
            binding.recyclerDoctorDashboard.apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        } else {

        }
    }

}
