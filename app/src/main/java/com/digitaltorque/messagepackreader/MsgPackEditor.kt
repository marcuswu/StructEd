package com.digitaltorque.messagepackreader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.MessagePackReaderTheme
import logic.Container
import logic.Field

@Composable
fun MsgPackEditor(onDismissRequest: () -> Unit, onConfirmation: (Field) -> Unit, item: Field) {
    val array = try { item.array } catch( _: Exception) { null }
    val map = try { item.map } catch( _: Exception) { null }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        shape = RoundedCornerShape(15.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(value = item., onValueChange = )
        }
    }
}

@Preview
@Composable
fun PreviewMsgPackEditor() {
    MessagePackReaderTheme {
        val item = Field()
        item.string = "value"
        item.mapParent = true
        item.key = "key"
        MsgPackEditor(parent = Container { }, path="", item = item)
    }
}
