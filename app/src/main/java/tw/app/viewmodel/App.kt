package tw.app.viewmodel

import android.graphics.drawable.Drawable
import java.io.Serializable

data class App(val title: String, val icon: Drawable, val versionName: String?) : Serializable