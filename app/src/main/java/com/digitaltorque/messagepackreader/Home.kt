package com.digitaltorque.messagepackreader

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.MessagePackReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(onFileSelected: (Uri?) -> Unit, modifier: Modifier = Modifier) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        onFileSelected(it)
    }
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier
            .fillMaxSize(0.5f)
            .aspectRatio(1.0f)
            .padding(8.dp),
            elevation = CardDefaults.cardElevation(1.dp),
            colors = CardDefaults.elevatedCardColors(), onClick = {
                launcher.launch(arrayOf("*/*"))
            }
        ) {
            Column(
                modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Open File",
                    modifier = Modifier.padding(8.dp),
                )
                Text(
                    text = AnnotatedString("Click here to open a file")
                )
            }
        }
    }
}
@Preview
@Composable
fun PreviewHome() {
    MessagePackReaderTheme {
        Home(onFileSelected = { })
    }
}
