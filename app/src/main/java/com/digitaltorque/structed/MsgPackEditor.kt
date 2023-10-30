package com.digitaltorque.structed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitaltorque.structed.utils.typeHintText
import com.digitaltorque.structed.utils.value
import com.example.compose.MessagePackReaderTheme
import logic.Field

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MsgPackEditor(onDismissRequest: () -> Unit, onConfirmation: (String) -> Unit, item: Field) {
    var value by remember { mutableStateOf(item.value()) }
    val name = item.key
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
            Text("Edit Field", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(8.dp))
            Text(item.typeHintText(), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
            OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text(name) })
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Close")
                }
                TextButton(
                    onClick = { onConfirmation(value)},
                    modifier = Modifier.padding(8.dp)) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMsgPackEditor() {
    MessagePackReaderTheme {
        val item = Field()
        item.string = "value"
        item.key = "key"
        MsgPackEditor(onConfirmation = {}, onDismissRequest = {}, item = item)
    }
}
