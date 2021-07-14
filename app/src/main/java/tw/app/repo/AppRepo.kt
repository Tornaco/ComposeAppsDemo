package tw.app.repo

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.flow.Flow
import tw.app.datasource.AppDataSource
import tw.app.log
import tw.app.viewmodel.App

interface AppRepo {
    suspend fun getApps(context: Context): Flow<List<App>>

    class Impl : AppRepo {
        @SuppressLint("QueryPermissionsNeeded")
        override suspend fun getApps(context: Context): Flow<List<App>> {
            log("Impl#getApps")
            return AppDataSource(context).latestApps
        }
    }
}