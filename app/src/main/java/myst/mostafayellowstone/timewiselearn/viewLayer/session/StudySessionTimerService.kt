package myst.mostafayellowstone.timewiselearn.viewLayer.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import myst.mostafayellowstone.timewiselearn.util.Constant.ACTION_SERVICE_DISMISS
import myst.mostafayellowstone.timewiselearn.util.Constant.ACTION_SERVICE_START
import myst.mostafayellowstone.timewiselearn.util.Constant.ACTION_SERVICE_STOP
import myst.mostafayellowstone.timewiselearn.util.Constant.NOTIFICATION_CHANNEL_ID
import myst.mostafayellowstone.timewiselearn.util.Constant.NOTIFICATION_CHANNEL_NAME
import myst.mostafayellowstone.timewiselearn.util.Constant.NOTIFICATION_ID
import myst.mostafayellowstone.timewiselearn.util.pad
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class StudySessionTimerService: Service() {
    private lateinit var timer: Timer
    var duration: Duration = Duration.ZERO
        private set
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    var seconds = mutableStateOf("00")
        private set
    var minuts =  mutableStateOf("00")
        private set
    var hours =  mutableStateOf("00")
        private set
    var currentTimeState = mutableStateOf(TimerState.IDLE)
        private set

    override fun onBind(intent: Intent?): IBinder?  = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let {
            when(it){
                ACTION_SERVICE_START -> {
                    startForgroundService()
                    setStartTimer{ hours, minutes, seconds ->
                        updateNotification(hours, minutes, seconds)
                    }
                }
                ACTION_SERVICE_STOP -> {
                    stopTimer()
                }
                ACTION_SERVICE_DISMISS -> {
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForgroundService(){
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build(),FOREGROUND_SERVICE_TYPE_SHORT_SERVICE)
    }

    private fun stopForegroundService(){
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel  =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(hours: String, minutes: String, seconds: String){
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder
                .setContentText("$hours:$minutes:$seconds")
                .build()
        )
    }

    private fun setStartTimer(
        onTick:(h: String, m: String, s: String) -> Unit
    ){
        currentTimeState.value = TimerState.STARTED
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L){
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value,minuts.value,seconds.value)
        }
    }

    private fun stopTimer(){
        if (this::timer.isInitialized){
            timer.cancel()
        }
        currentTimeState.value = TimerState.STOPPED
    }

    fun cancelTimer(){
        duration = Duration.ZERO
        updateTimeUnits()
        currentTimeState.value = TimerState.IDLE

    }

    fun updateTimeUnits(){
        duration.toComponents { hours, minuts, seconds ->
        this@StudySessionTimerService.hours.value = hours.toInt().pad()
        this@StudySessionTimerService.minuts.value = minuts.pad()
        this@StudySessionTimerService.seconds.value = seconds.pad()
    }
}
enum class TimerState{
    IDLE,
    STARTED,
    STOPPED,
    }

}