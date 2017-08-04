import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import java.io.File

/**
 * Created by jojo on 8/3/17.
 */

open class AppModel (context: Context, appInfo: ApplicationInfo) {
    val appLabel : String = appInfo.packageName

    val apkFile : File = File(appInfo.sourceDir)

    val icon : Drawable = appInfo.loadIcon(context.packageManager)

}