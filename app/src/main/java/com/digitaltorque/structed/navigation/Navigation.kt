package com.digitaltorque.structed.navigation

import androidx.navigation.NavHostController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class Navigation (private val router: NavHostController): router.Router_ {
    override fun back() {
        router.popBackStack()
    }

    override fun navigate(route: String?) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param("path", route ?: "unavailable")
        }
        route?.let { router.navigate(route) }
    }
}