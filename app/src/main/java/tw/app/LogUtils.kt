package tw.app

import android.util.Log

fun log(message: String) {
    Log.d("ComposeApp", "[${Thread.currentThread()}] $message")
}