package com.digitaltorque.messagepackreader

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.App
import com.digitaltorque.messagepackreader.navigation.Navigation
import com.example.compose.MessagePackReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val msgPackFile = remember { mutableStateOf<Uri?>(null) }
            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                msgPackFile.value = it
                App.router().navigate("viewer")
            }
            val navController = rememberNavController()
            App.setRouter(Navigation(navController))
            MessagePackReaderTheme {
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { Splash() }
                    composable("home") {
                        ClickableText(
                            onClick = { launcher.launch(arrayOf("*/*"))},
                            text = AnnotatedString("Click here to open a file")
                        )
                    }
                    composable("viewer?path={path}", arguments = listOf(navArgument("path") {
                        defaultValue = arrayOf<String>()
                        type = NavType.StringArrayType
                    })) {
                        msgPackFile.value?.let { uri ->
                            Surface (modifier = Modifier.fillMaxSize()){
                                MsgPackBrowser(file = uri, path = it.arguments?.getStringArray("path") ?: arrayOf())
                            }
                        }
                    }
                }
            }
        }
    }
}