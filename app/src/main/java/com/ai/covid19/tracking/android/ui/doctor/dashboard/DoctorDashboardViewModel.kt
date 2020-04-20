package com.ai.covid19.tracking.android.ui.doctor.dashboard

import androidx.lifecycle.ViewModel
import com.amazonaws.amplify.generated.graphql.QueryLastChecksByIdentityIdCheckTimestampIndexQuery
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient


class DoctorDashboardViewModel : ViewModel() {

    internal var mAWSAppSyncClient: AWSAppSyncClient? = null
    var listLastCheck: QueryLastChecksByIdentityIdCheckTimestampIndexQuery.QueryLastChecksByIdentityIdCheckTimestampIndex? = null
}