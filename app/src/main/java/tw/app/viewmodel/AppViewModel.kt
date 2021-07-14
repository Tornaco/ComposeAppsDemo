package tw.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tw.app.log
import tw.app.repo.AppRepo

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val _appListState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _appListState

    fun loadApps() {
        log("getApps")
        _appListState.value = UiState.Loading
        viewModelScope.launch {
            log("launch in.")
            val appRepo = AppRepo.Impl()
            val apps = appRepo.getApps(getApplication())
            apps
                .onStart {
                    log("getApps: onStart")
                }
                .onEach {
                    log("getApps: onEach $it")
                }
                .onCompletion {
                    log("getApps: onCompletion $it")
                }
                .flowOn(Dispatchers.IO)
                .catch {
                    log("getApps: error")
                }.collect {
                    log("getApps: collect")
                    _appListState.value = UiState.Success(it)
                }
        }
    }


}

sealed class UiState {
    data class Success(val apps: List<App>) : UiState()
    data class Error(val errorMsg: String) : UiState()
    object Loading : UiState()
}