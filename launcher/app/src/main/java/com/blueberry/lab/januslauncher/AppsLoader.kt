package com.blueberry.lab.januslauncher

import android.content.AsyncTaskLoader
import android.content.Context

/**
 * Created by jojo on 8/3/17.
 */
class AppsLoader(context: Context) :
        AsyncTaskLoader<List<AppModel>>(context) {

    private val packageManager = context.packageManager!!

    // To be used for filtering
    private val listOfAppModels by lazy(LazyThreadSafetyMode.NONE) {
        val apps = packageManager.getInstalledApplications(0) ?: emptyList()

        if (apps.isEmpty()) return@lazy emptyList<AppModel>()

        return@lazy apps
                .filter { app -> packageManager.getLaunchIntentForPackage(app.packageName) != null }
                .map { app -> AppModel(context, app) }
                .sortedWith(compareBy { appModel -> appModel.label.toString().toLowerCase() })
    }

    override fun loadInBackground(): List<AppModel> {
        return this.listOfAppModels
    }

    override fun onStartLoading() {
        if (takeContentChanged() || this.listOfAppModels.isEmpty()) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad()
        }

        if (this.listOfAppModels.isNotEmpty()) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(this.listOfAppModels)
        }
    }

    override fun deliverResult(apps: List<AppModel>?) {
        if (isReset) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps)
            }
        }

        if (isStarted) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps)
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (apps != null) {
            onReleaseResources(apps)
        }
    }

    private fun onReleaseResources(apps: List<AppModel>) {
        // do nothing
    }
}