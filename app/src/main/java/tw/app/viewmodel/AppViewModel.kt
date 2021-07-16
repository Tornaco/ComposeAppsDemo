package tw.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tw.app.log
import tw.app.repo.AppRepo

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val _appShowListState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    private val appList: MutableList<App>  = mutableListOf()
    val uiState: StateFlow<UiState> = _appShowListState

    fun loadApps() {
        log("getApps")
        _appShowListState.value = UiState.Loading
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
                .catch {
                    log("getApps: error")
                }.collect {
                    log("getApps: collect")
                    appList.clear()
                    appList.addAll(it)
                    _appShowListState.value = UiState.Success(it)
                }
        }
    }

    fun searchAppByKeyWord(keyword: String) {
        val value = _appShowListState.value
        if (value is UiState.Success) {
            appList.filter {
                it.title.contains(keyword)
            }.toList().also {
                _appShowListState.value = value.copy(it)
            }
        }
    }


}

sealed class UiState {
    data class Success(val apps: List<App>) : UiState()
    data class Error(val errorMsg: String) : UiState()
    object Loading : UiState()
}