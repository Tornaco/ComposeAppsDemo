package tw.app

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tw.app.ui.theme.ComposeAppsTheme
import tw.app.viewmodel.App
import tw.app.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appViewModel: AppViewModel by viewModels()
        setContent {
            ComposeAppsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen(appViewModel.appList, appViewModel.isLoading)
                }
            }
        }
        appViewModel.getApps()
    }
}

@Composable
fun HomeScreen(apps: List<App>, loading: MutableState<Boolean>) {
    Log.d("viewmodel", "HS ${loading.value}")
    if (loading.value) {
        CircularProgressIndicator()
    }
    LazyColumn {
        items(apps) {
            AppItem(app = it)
        }
    }
}

@Composable
fun AppItem(app: App) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = BitmapPainter(
                BitmapUtil.getBitmap(LocalContext.current, app.icon)!!.asImageBitmap()
            ),
            modifier = Modifier
                .padding(start = 16.dp)
                .size(48.dp),
            contentDescription = "app icon",
        )
        Column(
            Modifier
                .fillMaxHeight()
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = app.title,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 30.dp)
            )
            Text(
                text = app.versionCode, textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 30.dp)
            )
        }
    }
}

@Preview
@Composable
fun AppItemPreView() {
    AppItem(App("XXX", Drawable.createFromPath("")!!, "11"))
}