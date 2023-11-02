package com.digitaltorque.structed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.App
import com.digitaltorque.structed.config.AppConfig
import com.digitaltorque.structed.navigation.Navigation
import com.digitaltorque.structed.ui.Home
import com.digitaltorque.structed.ui.MsgPackBrowser
import com.digitaltorque.structed.ui.Splash
import com.digitaltorque.structed.utils.PurchaseManagerImpl
import com.digitaltorque.structed.viewmodel.BrowserViewModel
import com.example.compose.MessagePackReaderTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        val purchaseManager = PurchaseManagerImpl(this)
        App.setConfig(AppConfig())
        purchaseManager.start(connected = {
            runBlocking {
                launch {
                    purchaseManager.queryPurchaseHistory()
                }
            }
        }, disconnected = null)
        setContent {
            val browserViewModel: BrowserViewModel by viewModels { BrowserViewModel.Factory }
            val navController = rememberNavController()
            val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
                Firebase.crashlytics.recordException(Exception("No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"))
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }
            App.setRouter(Navigation(navController))
            MessagePackReaderTheme {
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { Splash() }
                    composable("home") {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            Home(onFileSelected = { file ->
                                browserViewModel.file = file
                                App.router().navigate("viewer")
                            }, modifier = Modifier
                            )
                        }
                    }
                    composable("viewer?path={path}", arguments = listOf(navArgument("path") {
                        defaultValue = arrayOf<String>()
                        type = NavType.StringArrayType
                    })) {backStackEntry ->
                        CompositionLocalProvider (LocalViewModelStoreOwner provides viewModelStoreOwner) {
                            browserViewModel.file?.let {
                                val pathArray = backStackEntry.arguments?.getStringArray("path") ?: arrayOf()
                                Surface (modifier = Modifier.fillMaxSize()){
                                    MsgPackBrowser(path = pathArray,
                                        purchaseManager = purchaseManager)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}