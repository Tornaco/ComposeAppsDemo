package tw.app

import android.util.Log

fun log(message: String) {
    Log.d("ComposeApp", "[${Thread.currentThread()}] $message")
}

fun trace() {
    log(Log.getStackTraceString(Throwable("Here.")))
}