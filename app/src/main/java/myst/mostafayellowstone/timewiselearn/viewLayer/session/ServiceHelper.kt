package myst.mostafayellowstone.timewiselearn.viewLayer.session

import android.content.Context
import android.content.Intent

object ServiceHelper {
    fun triggerForegroundService(context: Context , action: String){
        Intent(context, StudySessionTimerService::class.java).apply{
            this.action = action
            context.startService(this)
        }
    }
}