package myst.mostafayellowstone.timewiselearn.viewLayer.dashboard

import androidx.compose.ui.graphics.Color
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Subject

data class DashBoardState (
    val totalSubjectCount: Int = 0,
    val totalStudeiedHours: Float = 0f,
    val totalGoalStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.subjectCardColor.random(),
    val session: Session? = null
)