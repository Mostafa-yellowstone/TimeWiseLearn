package myst.mostafayellowstone.timewiselearn

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat

import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import myst.NavGraph
import myst.NavGraphs
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.domin.model.Task
import myst.mostafayellowstone.timewiselearn.ui.theme.TimeWiseLearnTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeWiseLearnTheme {
                DestinationsNavHost(navGraph= NavGraphs.root)
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


