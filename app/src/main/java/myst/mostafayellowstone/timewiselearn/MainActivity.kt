package myst.mostafayellowstone.timewiselearn

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat

import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import myst.NavGraph
import myst.NavGraphs
import myst.destinations.SessionScreenRouteDestination
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.domin.model.Task
import myst.mostafayellowstone.timewiselearn.ui.theme.TimeWiseLearnTheme
import myst.mostafayellowstone.timewiselearn.viewLayer.session.StudySessionTimerService

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isBound by mutableStateOf(false)
    private lateinit var timeService: StudySessionTimerService
    private val connection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StudySessionTimerService.StudySessionTimerBinder
            timeService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also {
            intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            if (isBound){
                TimeWiseLearnTheme {
                    DestinationsNavHost(navGraph= NavGraphs.root,
                        dependenciesContainerBuilder = {
                            dependency(SessionScreenRouteDestination){
                                timeService
                            }
                        })
                }
            }
        }
        requestPermission()
    }
    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}


val subjects = listOf(
    Subject(name = "English"  , goalHours = 10f , Subject.subjectCardColor[0].map { it.toArgb() }, subject_Id = 0),
    Subject(name = "Physics"  , goalHours = 8f , Subject.subjectCardColor[1].map { it.toArgb() } , subject_Id = 0),
    Subject(name = "Maths"  , goalHours = 11f , Subject.subjectCardColor[2].map { it.toArgb() } , subject_Id = 0),
    Subject(name = "Arabic"  , goalHours = 13f , Subject.subjectCardColor[3].map { it.toArgb() } , subject_Id = 0),
    Subject(name = "Geology"  , goalHours = 4f , Subject.subjectCardColor[4].map { it.toArgb() } , subject_Id = 0),
)

val tasks = listOf(
    Task(
        title = "Prepre notes",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = false,
        task_Id = 0,
        subjectTask_id = 1),


    Task(
        title = "Do Homework",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isComplete = true,
        task_Id = 0,
        subjectTask_id = 1),

    Task(
        title = "Study DesignPatterns",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        task_Id = 0,
        subjectTask_id = 1,
        isComplete = false),

    Task(
        title = "Data Structure",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        task_Id = 0,
        subjectTask_id = 1,
        isComplete = true),

    Task(
        title = "Jet Compose",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        task_Id = 0,
        subjectTask_id = 1,
        isComplete = true),

    Task(
        title = "Dependency Injection",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        task_Id = 0,
        subjectTask_id = 1,
        isComplete = true)
)

val sessions = listOf(
    Session(
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),       Session(
        relatedToSubject = "Arabic",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),       Session(
        relatedToSubject = "Geology",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),       Session(
        relatedToSubject = "Data Structure",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),       Session(
        relatedToSubject = "Design Patterns",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),       Session(
        relatedToSubject = "Room",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),       Session(
        relatedToSubject = "Dependency Injection",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),       Session(
        relatedToSubject = "Germany",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),




    )


