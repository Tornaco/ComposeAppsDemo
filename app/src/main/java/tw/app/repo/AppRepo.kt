package tw.app.repo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tw.app.viewmodel.App

interface AppRepo {
    suspend fun getApps(context: Context): List<App>

    class Impl : AppRepo {
        @SuppressLint("QueryPermissionsNeeded")
        override suspend fun getApps(context: Context): List<App> {
            return withContext(Dispatchers.IO) {
                Log.d(
                    "AppRepo",
                    "getApps@${Thread.currentThread().hashCode()} ${Thread.currentThread()} "
                )
                val pm = context.packageManager
                Thread.sleep(3000)
                pm.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
                    .map { applicationInfo ->
                        toApp(pm, applicationInfo)
                    }.toList()
            }
        }

        private fun toApp(pm: PackageManager, applicationInfo: ApplicationInfo): App {
            return App(
                applicationInfo.loadLabel(pm).toString(),
                applicationInfo.loadIcon(pm),
                applicationInfo.minSdkVersion.toString()
            )
        }
    }
}