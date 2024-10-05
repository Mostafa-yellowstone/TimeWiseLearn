package myst.mostafayellowstone.timewiselearn.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import myst.mostafayellowstone.timewiselearn.data.converter.ColorListConverter
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.domin.model.Task

@Database(
    entities = [Subject::class , Session::class , Task::class] , version = 1)
@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {



    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao

}