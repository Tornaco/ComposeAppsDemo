package tw.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tw.app.repo.AppRepo

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AppViewModel"

    val appList = mutableStateListOf<App>()
    val isLoading = mutableStateOf(false)

    fun getApps() {
        Log.d(TAG, "getApps: isLoading" + isLoading.hashCode())
        isLoading.value = true
        Log.d(TAG, "getApps: isLoading after" + isLoading.hashCode())

        viewModelScope.launch() {
            Log.d(
                "AppRepo",
                "launch@${Thread.currentThread().hashCode()} ${Thread.currentThread()} "
            )
            val appRepo = AppRepo.Impl()
            val apps = appRepo.getApps(getApplication())
            appList.clear()
            appList.addAll(apps)
            isLoading.value = false
        }
    }

}