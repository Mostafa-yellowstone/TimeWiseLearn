package myst.mostafayellowstone.timewiselearn.viewLayer.session

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import myst.mostafayellowstone.timewiselearn.MainActivity
import myst.mostafayellowstone.timewiselearn.util.Constant.CLICK_REQUEST_CODE

object ServiceHelper {

    fun clickPendingIntent(context: Context): PendingIntent{
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "time_wise_learn://dashboard/session".toUri(),
            context,
            MainActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE
            )!!
        }
    }


    fun triggerForegroundService(context: Context , action: String){
        Intent(context, StudySessionTimerService::class.java).apply{
            this.action = action
            context.startService(this)
        }
    }
}