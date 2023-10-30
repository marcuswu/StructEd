package com.digitaltorque.structed

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.digitaltorque.structed.utils.FileUtil

@Composable
fun CloseFileDialog(filename: String, format: String,
                    setShowDialog: (Boolean) -> Unit,
                    cancelAction: () -> Unit,
                    closeAction: () -> Unit,
                    saveAction: (format: Int) -> Unit,
                    saveAsAction: (filename: Uri?, format: Int) -> Unit) {
    val saveFormat = remember { mutableIntStateOf(FileUtil.stringToFormat[format] ?: 0) }
    val showDropdown = remember { mutableStateOf(false) }
    val launcherMsgPack = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(FileUtil.MimeMsgPack)
    ) {
        saveAsAction(it, saveFormat.intValue)
    }
    val launcherYaml = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(FileUtil.MimeYaml)
    ) {
        saveAsAction(it, saveFormat.intValue)
    }
    val launcherJson = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(FileUtil.MimeJson)
    ) {
        saveAsAction(it, saveFormat.intValue)
    }
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Closing File", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Row {
                            Text(text = "• ", style = MaterialTheme.typography.titleMedium)
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontStyle = MaterialTheme.typography.bodyMedium.fontStyle)) {
                                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                            append("Cancel")
                                        }
                                        append(" will keep the file open.")
                                    }
                                }
                            )
                        }
                        Row {
                            Text(text = "• ", style = MaterialTheme.typography.titleMedium)
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontStyle = MaterialTheme.typography.bodyMedium.fontStyle)) {
                                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                            append("Close")
                                        }
                                        append(" will close without saving.")
                                    }
                                }
                            )
                        }
                        Row {
                            Text(text = "• ", style = MaterialTheme.typography.titleMedium)
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontStyle = MaterialTheme.typography.bodyMedium.fontStyle)) {
                                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                            append("Save")
                                        }
                                        append(" will save to the original filename with the provided format.")
                                    }
                                }
                            )
                        }
                        Row {
                            Text(text = "• ", style = MaterialTheme.typography.titleMedium)
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontStyle = MaterialTheme.typography.bodyMedium.fontStyle)) {
                                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                            append("Save As")
                                        }
                                        append(" will save to a new filename with the provided format.")
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Format", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        Row(modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                            .clickable {
                                showDropdown.value = true
                            },
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(text = FileUtil.formatToString[saveFormat.intValue] ?: "MessagePack",
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
                            FileUtil.formatToString.forEach {
                                DropdownMenuItem(
                                    text = { Text(text = it.value) },
                                    onClick = {
                                        saveFormat.intValue = it.key
                                        showDropdown.value = false
                                    })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Column {
                        Row {
                            Button(onClick = cancelAction) {
                                Text(text = "Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = closeAction) {
                                Text(text = "Close")
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Row {
                            Button(onClick = { saveAction(saveFormat.intValue) }) {
                                Text(text = "Save")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                when (saveFormat.intValue) {
                                    FileUtil.MsgPackFormat -> launcherMsgPack.launch(filename)
                                    FileUtil.YamlFormat -> launcherYaml.launch(filename)
                                    FileUtil.JsonFormat -> launcherJson.launch(filename)
                                }
                            }) {
                                Text(text = "Save As")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CloseFileDialog() {
    CloseFileDialog(
        filename = "", format = "MessagePack",
        setShowDialog = {},
        cancelAction = { },
        closeAction = { },
        saveAction = {},
        saveAsAction = { _: Uri?, _: Int -> }
    )
}
