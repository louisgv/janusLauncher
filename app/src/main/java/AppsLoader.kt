import android.app.Application
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.pm.PackageManager

/**
 * Created by jojo on 8/3/17.
 */
class AppsLoader (context: Context) :
    AsyncTaskLoader<Array<AppModel>>(context){

    val packageManager : PackageManager = context.packageManager;

    override fun loadInBackground(): Array<AppModel> {

        val apps = packageManager.getInstalledApplications(0) ?: emptyList()

        if (apps.isEmpty()) return emptyArray()

        return apps
                .filter { app -> packageManager.getLaunchIntentForPackage(app.packageName) != null }
                .sortedWith(compareBy{ app -> app.packageName })
                .map { app -> AppModel(context, app) }
                .toTypedArray()
    }

}