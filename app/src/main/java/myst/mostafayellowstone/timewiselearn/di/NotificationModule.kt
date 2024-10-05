package myst.mostafayellowstone.timewiselearn.di

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import myst.mostafayellowstone.timewiselearn.R
import myst.mostafayellowstone.timewiselearn.util.Constant.NOTIFICATION_CHANNEL_ID
import myst.mostafayellowstone.timewiselearn.viewLayer.session.ServiceHelper


@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {


    @Provides
    @ServiceScoped
    fun provideNotificationBuilder( @ApplicationContext context : Context): NotificationCompat.Builder{
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Session Lab")
            .setContentText("00:00:00")
            .setSmallIcon(R.drawable.session)
            .setOngoing(true)
            .setContentIntent(ServiceHelper.clickPendingIntent(context))
    }

    @Provides
    @ServiceScoped
    fun provideNotificationManager(  @ApplicationContext context : Context): NotificationManager{
      return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}