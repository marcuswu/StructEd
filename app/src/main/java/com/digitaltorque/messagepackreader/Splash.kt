package com.digitaltorque.messagepackreader

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.App

@Composable
fun Splash() {
    val splashViewModel = remember { viewmodels.SplashViewModel() }
    //splashViewModel.loadRemoteConfig()
    App.router().navigate("home")
}
