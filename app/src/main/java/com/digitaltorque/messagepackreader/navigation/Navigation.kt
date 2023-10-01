package com.digitaltorque.messagepackreader.navigation

import androidx.navigation.NavHostController

class Navigation (private val router: NavHostController): router.Router_ {
    override fun back() {
        router.popBackStack()
    }

    override fun navigate(route: String?) {
        route?.let { router.navigate(route) }
    }
}