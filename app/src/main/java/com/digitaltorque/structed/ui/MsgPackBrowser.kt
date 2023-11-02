package com.digitaltorque.structed.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import app.App
import com.digitaltorque.structed.config.AppConfig
import com.digitaltorque.structed.utils.FileUtil
import com.digitaltorque.structed.utils.PurchaseManager
import com.digitaltorque.structed.utils.PurchaseManagerImpl
import com.digitaltorque.structed.utils.errorMessage
import com.digitaltorque.structed.utils.keys
import com.digitaltorque.structed.viewmodel.BrowserViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import logic.Field


@Composable
fun MsgPackBrowser(path: Array<String>, purchaseManager: PurchaseManager, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val waitDuration = if (purchaseManager.hasPurchased(PurchaseManagerImpl.SUPPORT_DEVELOPER_PRODUCT_ID)) {
        0
    } else {
        App.config().getInt(AppConfig.ASK_SUPPORT_WAIT)
    }
    val viewModel = viewModel<BrowserViewModel>()
    val mapState = viewModel.state.collectAsState()

    val backHandlerEnabled = remember { mutableStateOf(true) }
    val askDismiss = remember { mutableStateOf(false) }
    val saving = remember { mutableStateOf(false) }
    val saveFile = remember { mutableStateOf(viewModel.file) }

    mapState.value?.error?.let {
        println("Got an error from map state: ${it.message}")
        LocalContext.current.errorMessage(it.message ?: "")
        viewModel.clearError()
    }
    val keys = viewModel.getField(path.joinToString(separator = "/"))?.keys() ?: listOf()
    println("browser keys: $keys")
    LazyColumn (modifier = modifier) {
        items(items = keys) {
            Surface {
                val field = viewModel.getField(path.plus(it).joinToString("/")) ?: Field()
                MsgPackRow(parentPath = path, field)
            }
        }
    }
    if (askDismiss.value) {
        CloseFileDialog(
            filename = viewModel.file?.lastPathSegment?.split("/")?.lastOrNull() ?: viewModel.file.toString(),
            format = FileUtil.formatToString[viewModel.getFormat()] ?: FileUtil.MimeMsgPack,
            setShowDialog = { askDismiss.value = it},
            cancelAction = {
                askDismiss.value = false
                backHandlerEnabled.value = true
                Firebase.analytics.logEvent("cancelCloseFile") {
                    param("file", viewModel.file?.toString() ?: "Unknown")
                }
            },
            closeAction = {
                askDismiss.value = false
                backHandlerEnabled.value = false
                Firebase.analytics.logEvent("closeNoSave") {
                    param("file", viewModel.file?.toString() ?: "Unknown")
                }
                App.router().back()
            },
            saveAsAction = { filename: Uri?, format: Int ->
                viewModel.setFormat(format)
                saving.value = true
                askDismiss.value = false
                saveFile.value = filename
            },
            saveAction = {
                viewModel.setFormat(it)
                saving.value = true
                askDismiss.value = false
            }
        )
    }
    if (saving.value) {
        WaitScreen(waitDuration, purchaseManager) {
            saving.value = false
            backHandlerEnabled.value = false
            saveFile.value?.let {fileUri ->
                Firebase.analytics.logEvent("saveFile") {
                    param("path", fileUri.toString())
                    param("format", FileUtil.formatToString[viewModel.getFormat()] ?: "Unknown")
                }
                val error = FileUtil.writeFile(context, viewModel.fileData(), fileUri)
                error?.let {
                    viewModel.setError(Exception(it))
                }
            }
            App.router().back()
        }
    }
    BackHandler(enabled = backHandlerEnabled.value) {
        if (path.isNotEmpty()) {
            backHandlerEnabled.value = false
            App.router().back()
            return@BackHandler
        }
        askDismiss.value = path.isEmpty()
        backHandlerEnabled.value = false
    }
}
