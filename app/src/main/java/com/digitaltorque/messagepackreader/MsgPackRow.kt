package com.digitaltorque.messagepackreader

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
import app.App
import com.digitaltorque.messagepackreader.utils.MsgPackType
import com.digitaltorque.messagepackreader.utils.setValue
import com.digitaltorque.messagepackreader.utils.value
import com.digitaltorque.messagepackreader.utils.valueOrType
import com.example.compose.MessagePackReaderTheme
import logic.Container
import logic.Field

@Composable
fun MsgPackRow(parent: Container?, path: String, item: Field, saveFn: () -> Unit) {
    val array = try { item.array } catch( _: Exception) { null }
    val map = try { item.map } catch( _: Exception) { null }
    val name = if (item.mapParent) {
        item.key
    } else {
        item.index.toString()
    }
    val routePath = if(path.isNotEmpty()) { arrayOf(path, name).joinToString(separator = "&path=") } else { name }
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
            App.router().navigate("viewer?path=$routePath")
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
            }, onConfirmation = {
                item.setValue(it)
                parent?.set(item)
                saveFn()
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
        item.mapParent = true
        item.key = "key"
        MsgPackRow(parent = { }, path="", item = item, saveFn = {})
    }
}