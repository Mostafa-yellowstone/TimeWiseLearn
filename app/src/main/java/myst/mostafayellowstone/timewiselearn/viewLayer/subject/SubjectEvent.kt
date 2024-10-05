package myst.mostafayellowstone.timewiselearn.viewLayer.subject

import androidx.compose.ui.graphics.Color
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Task

sealed class SubjectEvent {
    data object UpdateSubject: SubjectEvent()

    data object DeleteSubject: SubjectEvent()

    data object DeleteSession: SubjectEvent()

    data object UpdateProgress: SubjectEvent()

    data class onTaskCompleteChange(val task: Task): SubjectEvent()

    data class onSubjectCardColorChange(val color: List<Color>): SubjectEvent()

    data class onSubjectNameChange(val name: String): SubjectEvent()

    data class onGoalStudyHoursChange(val hours: String): SubjectEvent()

    data class onDeleteSessionButtonClick(val session: Session):SubjectEvent()
}