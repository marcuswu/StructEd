package com.digitaltorque.structed.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitaltorque.structed.utils.MsgPackType
import com.digitaltorque.structed.utils.isScalarType
import com.digitaltorque.structed.utils.typeHintText
import com.example.compose.MessagePackReaderTheme

data class EditorOptions (
    var key: String? = null,
    var value: String = "",
    var type: MsgPackType = MsgPackType.Int,
    var parentType: MsgPackType = MsgPackType.Map,
) {
    companion object {
        fun builder(buildFunc: EditorOptions.() -> Unit): EditorOptions {
            val options = EditorOptions()
            options.buildFunc()
            return options
        }
    }
}

@Composable
fun MsgPackEditor(onDismissRequest: () -> Unit, onConfirmation: (String, String, MsgPackType) -> Unit, options: EditorOptions) {
    val newField = options.key == null
    var value by remember { mutableStateOf(options.value) }
    val newFieldName = if (options.parentType == MsgPackType.Array) "0" else "NewFieldName"
    var key by remember { mutableStateOf(options.key?: newFieldName) }
    val showDropdown = remember { mutableStateOf(false) }
    val fieldFormat = remember { mutableStateOf(options.type) }
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
            Text(if (newField) "New Field" else "Edit Field", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(8.dp))
            if (newField && options.parentType == MsgPackType.Map) {
                Row(modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                    .clickable {
                        showDropdown.value = true
                    },
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = fieldFormat.value.name,
                        style = MaterialTheme.typography.bodyMedium.plus(
                            TextStyle(textAlign = TextAlign.Center)
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp, 16.dp, 0.dp, 16.dp)
                            .width(100.dp))
                    Icon(
                        Icons.Filled.KeyboardArrowDown, "select format",
                        Modifier.padding(8.dp)
                    )
                }
                DropdownMenu(expanded = showDropdown.value,
                    onDismissRequest = { showDropdown.value = false }) {
                    MsgPackType.values().forEach {
                        if (it == MsgPackType.Unknown) {
                            return@forEach
                        }
                        DropdownMenuItem(
                            text = { Text(text = it.name) },
                            onClick = {
                                fieldFormat.value = it
                                showDropdown.value = false
                            })
                    }
                }
                Text("Field Name",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
                OutlinedTextField(value = key, onValueChange = { key = it }, label = { Text(key ?: "") })
            }
            if (fieldFormat.value.isScalarType()) {
                Text("Field Value", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(key ?: "") })
                Text(
                    fieldFormat.value.typeHintText(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(48.dp, 16.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = { onConfirmation(key ?: "", value, fieldFormat.value) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMsgPackEditor() {
    val options = EditorOptions.builder {
        key = null
        value = "value"
        type = MsgPackType.Time
        parentType = MsgPackType.Map
    }
    MessagePackReaderTheme {
        MsgPackEditor(
            onConfirmation = { _: String, _: String, _: MsgPackType -> },
            onDismissRequest = {},
            options = options
        )
    }
}
