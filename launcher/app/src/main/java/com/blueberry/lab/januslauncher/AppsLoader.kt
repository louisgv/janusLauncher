package com.blueberry.lab.januslauncher

import android.content.AsyncTaskLoader
import android.content.Context
import android.content.pm.PackageManager

/**
 * Created by jojo on 8/3/17.
 */
class AppsLoader (context: Context) :
    AsyncTaskLoader<Array<AppModel>>(context){

    val packageManager : PackageManager = context.packageManager

    val mInstalledApps : Array<AppModel> by lazy <Array<AppModel>>{
        val apps = packageManager.getInstalledApplications(0) ?: emptyList()

        if (apps.isEmpty()) return@lazy emptyArray()

        return@lazy apps
                .filter { app -> packageManager.getLaunchIntentForPackage(app.packageName) != null }
                .sortedWith(compareBy{ app -> app.packageName })
                .map { app -> AppModel(context, app) }
                .toTypedArray()
    }

    override fun loadInBackground(): Array<AppModel> {
        return mInstalledApps
    }

    override fun onStartLoading() {
        if (takeContentChanged() || mInstalledApps.isEmpty() ) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad()
        }

        if (mInstalledApps.isNotEmpty()) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mInstalledApps)
        }
    }

    override fun deliverResult(apps: Array<AppModel>?) {
        if (isReset) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps)
            }
        }

        val oldApps = apps

        if (isStarted) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps)
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps)
        }
    }

    private fun onReleaseResources(apps: Array<AppModel>) {
        // do nothing
    }
}