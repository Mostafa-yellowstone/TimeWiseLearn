package myst.mostafayellowstone.timewiselearn.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import myst.mostafayellowstone.timewiselearn.data.local.AppDatabase
import myst.mostafayellowstone.timewiselearn.data.local.SessionDao
import myst.mostafayellowstone.timewiselearn.data.local.SubjectDao
import myst.mostafayellowstone.timewiselearn.data.local.TaskDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): AppDatabase{
        return Room
            .databaseBuilder(
                application,
                AppDatabase::class.java,
                "timewiselearn.db"
            )
            .build()
    }
    @Provides
    @Singleton
    fun provideSubjectDao(database: AppDatabase): SubjectDao{
        return database.subjectDao()
    }
    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao{
        return database.taskDao()
    }
    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao{
        return database.sessionDao()
    }
}