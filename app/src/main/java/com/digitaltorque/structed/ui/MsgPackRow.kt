package com.digitaltorque.structed.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.App
import com.digitaltorque.structed.R
import com.digitaltorque.structed.utils.setValue
import com.digitaltorque.structed.utils.valueOrType
import com.digitaltorque.structed.viewmodel.BrowserViewModel
import com.example.compose.MessagePackReaderTheme
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import logic.Field

@Composable
fun MsgPackRow(parentPath: Array<String>, item: Field) {
    val viewModel = viewModel<BrowserViewModel>()
//    val item = viewModel.getField(path.joinToString("/")) ?: Field()
    val array = try { item.array } catch( _: Exception) { null }
    val map = try { item.map } catch( _: Exception) { null }
    val name = item.key
    val routePath = parentPath.plus(name).joinToString(separator = "&path=")
    val openEditorDialog = remember { mutableStateOf(false) }
    Row(modifier = Modifier
        .padding(all = 8.dp)
        .padding(start = 16.dp)
        .fillMaxWidth()
        .clickable {
            if (array == null && map == null) {
                openEditorDialog.value = true
                return@clickable
            }
            App
                .router()
                .navigate("viewer?path=$routePath")
        }) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.valueOrType(), color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (array != null || map != null) {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                    contentDescription = "Select",
                    modifier = Modifier.padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
    when {
        openEditorDialog.value -> {
            MsgPackEditor(onDismissRequest = {
                openEditorDialog.value = false
                Firebase.analytics.logEvent("cancelEdit") {
                    param("path", parentPath.plus(item.key).joinToString("/"))
                }
            }, onConfirmation = {
                Firebase.analytics.logEvent("saveValue") {
                    param("path", parentPath.plus(item.key).joinToString("/"))
                    param("value", it)
                }
                item.setValue(it)
                viewModel.setField(parentPath.joinToString("/"), item)
                openEditorDialog.value = false
            }, item = item)
        }
    }
}

@Preview
@Composable
fun PreviewMsgPackRow() {
    MessagePackReaderTheme {
        val item = Field()
        item.string = "value"
        item.key = "key"
        MsgPackRow(parentPath= arrayOf(""), item = item)
    }
}