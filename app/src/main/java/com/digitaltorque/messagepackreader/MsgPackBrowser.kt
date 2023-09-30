package com.digitaltorque.messagepackreader

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.digitaltorque.messagepackreader.data.MsgPackType
import com.digitaltorque.messagepackreader.utils.FileUtil
import com.digitaltorque.messagepackreader.utils.keys
import com.digitaltorque.messagepackreader.utils.toList
import logic.Field
import viewmodels.MsgPackViewerState
import viewmodels.ViewerViewModel


private fun errorMessage(context: Context, error: String) {
    Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
}

@Composable
fun MsgPackBrowser(file: Uri, path: Array<String>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewerViewModel = remember {
        val fileData = FileUtil.readFile(context, file)
        ViewerViewModel(fileData)
    }
    val mapState: MsgPackViewerState? = viewerViewModel.cloneState()
    var current: Field? = Field()
    current?.map = mapState?.data
    current?.mapParent = true
    println("Current path: ${path.joinToString(separator = "/")}")
    path.forEach {
        if (current == null) {
            return@forEach
        }
        if (current?.type()?.toInt() == MsgPackType.Map.ordinal) {
            current = current?.map?.getField(it)
        }
        if (current?.type()?.toInt() == MsgPackType.Array.ordinal) {
            current = current?.array?.get(it.toLong())
        }
    }
    mapState?.error?.let {
        println("Got an error from map state: ${it.message}")
        errorMessage(LocalContext.current, it.message ?: "")
        val newState = mapState.clone()
        newState.error = null
        viewerViewModel.updateState(newState)
    }
    LazyColumn {
        val currentPath = path.joinToString(separator = "&path=")
        println("currentPath: $currentPath")
        current?.let { field ->
            if (field.type().toInt() == MsgPackType.Map.ordinal) {
                val sortedKeys = field.map.keys().sorted()
                println("Map with ${field.map.keySize()} entries")
                items(items = sortedKeys) {
                    Surface {
                        MsgPackRow(parent = null, path = currentPath, item = field.map.getField(it))
                    }
                }
            }
            if (field.type().toInt() == MsgPackType.Array.ordinal) {
                println("Array with ${field.array.size()} entries")
                items(items = field.array.toList()) {
                    Surface {
                        MsgPackRow(parent = null, path = currentPath, item = it)
                    }
                }
            }
        }
    }
}
