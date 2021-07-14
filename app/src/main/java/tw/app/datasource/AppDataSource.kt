package tw.app.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import tw.app.log
import tw.app.viewmodel.App

class AppDataSource(private val context: Context) {

    val latestApps: Flow<List<App>> = flow {
        log("flow#emit")
        delay(3000)
        emit(getAppsFromAndroid())
    }.flowOn(Dispatchers.IO)

    @SuppressLint("QueryPermissionsNeeded")
    private fun getAppsFromAndroid(): List<App> {
        val pm = context.packageManager
        @Suppress("BlockingMethodInNonBlockingContext")
        return pm.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            .map { applicationInfo ->
                toApp(pm, applicationInfo)
            }.toList()
    }

    private fun toApp(pm: PackageManager, applicationInfo: ApplicationInfo): App {
        return App(
            applicationInfo.loadLabel(pm).toString(),
            applicationInfo.loadIcon(pm),
            applicationInfo.minSdkVersion.toString()
        )
    }
}