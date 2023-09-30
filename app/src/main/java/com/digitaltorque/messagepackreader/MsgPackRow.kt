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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.App
import com.digitaltorque.messagepackreader.data.MsgPackType
import com.example.compose.MessagePackReaderTheme
import logic.Container
import logic.Field

@Composable
fun MsgPackRow(parent: Container?, path: String, item: Field) {
    val array = try { item.array } catch( _: Exception) { null }
    val map = try { item.map } catch( _: Exception) { null }
    val name = if (item.mapParent) {
        item.key
    } else {
        item.index.toString()
    }
    val routePath = if(path.isNotEmpty()) { arrayOf(path, name).joinToString(separator = "&path=") } else { name }
    Row(modifier = Modifier
        .padding(all = 8.dp)
        .padding(start = 16.dp)
        .fillMaxWidth()
        .clickable {
            if (array == null && map == null) {
                App.router().navigate("editor?path=$routePath")
                return@clickable
            }
            App.router().navigate("viewer?path=$routePath")
        }) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            val value = when (item.type().toInt()) {
                MsgPackType.Nil.ordinal -> null
                MsgPackType.Int.ordinal -> item.int.toString()
                MsgPackType.Int8.ordinal -> item.int8.toString()
                MsgPackType.Int16.ordinal -> item.int16.toString()
                MsgPackType.Int32.ordinal -> item.int32.toString()
                MsgPackType.Int64.ordinal -> item.int64.toString()
                MsgPackType.Bool.ordinal -> item.bool.toString()
                MsgPackType.Float32.ordinal -> item.float32.toString()
                MsgPackType.Float64.ordinal -> item.float64.toString()
                MsgPackType.String.ordinal -> item.string
                MsgPackType.Array.ordinal -> "List"
                MsgPackType.Map.ordinal -> "Name-Value"
                MsgPackType.Unknown.ordinal -> "Unknown"
                else -> "Unknown"
            }
            Text(text = "$value", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodyMedium)
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
}

@Preview
@Composable
fun PreviewMsgPackRow() {
    MessagePackReaderTheme {
        val item = Field()
        item.string = "value"
        item.mapParent = true
        item.key = "key"
        MsgPackRow(parent = Container { }, path="", item = item)
    }
}