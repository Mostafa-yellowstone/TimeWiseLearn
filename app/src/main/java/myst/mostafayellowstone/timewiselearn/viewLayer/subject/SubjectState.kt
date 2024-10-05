package myst.mostafayellowstone.timewiselearn.viewLayer.subject

import androidx.compose.ui.graphics.Color
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.domin.model.Task

data class SubjectState(
    val currentSubjectId: Int? = null,
    val subjectName: String = "",
    val goalStudyHours: String= "",
    val studiedHours: Float = 0f,
    val subjectCardColors: List<Color> = Subject.subjectCardColor.random(),
    val recentSessions: List<Session> = emptyList(),
    val upComingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Session? = null,
    val progress: Float = 0f,
)
