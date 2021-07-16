package tw.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import tw.app.ui.theme.ComposeAppsTheme
import tw.app.ui.theme.dividerColor
import tw.app.viewmodel.App
import tw.app.viewmodel.AppViewModel
import tw.app.viewmodel.UiState

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
            }

            ComposeAppsTheme {
                ProvideWindowInsets {
                    InsetsBasics()
                }
            }
        }
    }
}

@Composable
fun InsetsBasics() {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
        Scaffold(topBar = {
            // We use TopAppBar from accompanist-insets-ui which allows us to provide
            // content padding matching the system bars insets.
            TopAppBar(
                title = { Text("应用管理") },
                backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.9f),
                contentPadding = rememberInsetsPaddingValues(
                    LocalWindowInsets.current.statusBars,
                    applyBottom = false,
                ),
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp
            )
        }) {
            Box(modifier = Modifier.padding(it)) {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val appViewModel = viewModel<AppViewModel>()
    appViewModel.loadApps()

    NavHost(
        navController = navController,
        startDestination = "AppList",
        modifier = modifier
    ) {
        composable("AppList") {
            log("composable AppList")
            val state = appViewModel.uiState.collectAsState()
            AppListWithSearchBar(state) {
                // In the source screen...
                navController.currentBackStackEntry?.arguments =
                    Bundle().apply {
                        putSerializable("appInfo", it)
                    }

                navController.navigate("AppDetail")
            }
        }
        composable(
            "AppDetail",
            arguments = listOf(navArgument("appInfo") {
                type = NavType.SerializableType(App::class.java)
            })
        ) {
            log("composable AppDetail")

            val app = navController.previousBackStackEntry
                ?.arguments?.getSerializable("appInfo") as App
            AppDetail(app = app)
        }
    }
}

@Composable
fun SearchBar() {
    val textFieldValueState = remember {
        mutableStateOf(
            IndexedTextFieldValue(
                TextFieldValue(
                    text = "",
                    selection = TextRange(0)
                ), 0
            )
        )
    }
    log("SearchBar compose $textFieldValueState")
    OutlinedTextField(
        value = textFieldValueState.value.textFieldValue,
        onValueChange = { tfv ->
            val formatted = if (tfv.text.length >= 3) tfv.text.substring(0, 3) else tfv.text
            log("onValueChange $tfv")
            textFieldValueState.value = IndexedTextFieldValue(
                tfv.copy(text = formatted),
                textFieldValueState.value.index + 1
            )
            trace()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),

        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun AppDetail(app: App) {
    Column {
        Image(
            bitmap = BitmapUtil.getBitmap(LocalContext.current, app.icon)!!.asImageBitmap(),
            contentDescription = "app icon",
            Modifier
                .size(70.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp)
        )

    }

}

@Composable
fun AppListWithSearchBar(uiState: State<UiState>, onItemClick: (App) -> Unit) {
    Column {
        SearchBar()
        AppList(uiState, onItemClick)
    }
}

@Composable
fun AppList(uiState: State<UiState>, onItemClick: (App) -> Unit) {
    log("vm ${viewModel<AppViewModel>().hashCode()}")
    log("vm ${viewModel<AppViewModel>().hashCode()}")


    log("HS ${uiState.value}")
    when (val state = uiState.value) {
        UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is UiState.Success -> {
            val apps = state.apps
            log("apps $apps")
            LazyColumn {
                items(apps) {
                    AppItem(app = it, onClick = {
                        onItemClick(it)
                    })
                    Spacer(
                        modifier = Modifier
                            .background(MaterialTheme.dividerColor)
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                }
            }
        }
        else -> {
            val error = state as UiState.Error
            log("error $error")
        }
    }
}

@Composable
fun AppItem(app: App, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(
                data = app.icon.toBitmap(),
                imageLoader = LocalImageLoader.current,
                builder = {
                    placeholder(R.drawable.ic_launcher_foreground)
                    transformations(CircleCropTransformation())
                }
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
                text = app.versionName ?: "UnKnow", textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 30.dp)
            )
        }
    }
}