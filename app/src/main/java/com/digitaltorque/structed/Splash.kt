package com.digitaltorque.structed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import app.App

@Preview
@Composable
fun Splash() {
    val splashViewModel = remember { viewmodels.SplashViewModel() }
    splashViewModel.loadRemoteConfig()
    App.router().navigate("home")
}
