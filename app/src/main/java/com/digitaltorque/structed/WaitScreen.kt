package com.digitaltorque.structed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitaltorque.structed.utils.DummyPurchaseManager
import com.digitaltorque.structed.utils.PurchaseManager
import com.digitaltorque.structed.utils.PurchaseManagerImpl
import com.digitaltorque.structed.utils.errorMessage
import com.digitaltorque.structed.utils.getActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun WaitScreen(duration: Long, purchaseManager: PurchaseManager, completion: () -> Unit) {
    val activity = LocalContext.current.getActivity()
    val error = remember { mutableStateOf("") }
    if (duration == 0L) {
        completion()
        return
    }
    var waitTime by remember { mutableLongStateOf(duration) }
    LaunchedEffect(key1 = waitTime) {
        if (waitTime > 0) {
            delay(1_000)
            waitTime -= 1
        } else {
            completion()
        }
    }
    if (error.value.isNotEmpty()) {
        LocalContext.current.errorMessage(error.value)
        error.value = ""
    }
    Surface(modifier = Modifier
        .fillMaxSize()
        .alpha(0.8f), color = Color.Black) {
        Surface {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Support the Developer",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.padding(32.dp, 16.dp),
                    textAlign = TextAlign.Justify,
                    text = "As the developer of this app, I have decided not to include advertisements here. " +
                            "If you get value from this app, please consider sending your appreciation in the form" +
                            " of purchasing a removal of this notification.",
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "This notice will close in $waitTime seconds")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    activity?.let { activity ->
                        runBlocking {
                            launch {
                                val products = purchaseManager.queryProducts()
                                val product =
                                    products.firstOrNull { it.productId == PurchaseManagerImpl.SUPPORT_DEVELOPER_PRODUCT_ID }
                                product?.also {
                                    purchaseManager.beginPurchase(activity, it)
                                } ?: run {
                                    error.value = "Failed to retrieve product information from the Play Store"
                                }
                            }
                        }
                    }
                }) {
                    Text(text = "Support")
                }
            }
        }
    }
}

@Preview
@Composable
fun WaitScreen(){
    WaitScreen(duration = 20, purchaseManager = DummyPurchaseManager()) {

    }
}
