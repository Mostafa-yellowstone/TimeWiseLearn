package myst.mostafayellowstone.timewiselearn.viewLayer.dashboard

import androidx.compose.ui.graphics.Color
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Task

sealed class DashBoardEvent {
    data object SaveSubject: DashBoardEvent()
    data object DeleteSession: DashBoardEvent()
    data class onDeleteSessionButtonClick(val session: Session): DashBoardEvent()
    data class onTaskIsCompleteChange(val task: Task): DashBoardEvent()
    data class onSubjectCardColorChange(val color: List<Color>): DashBoardEvent()
    data class onSubjectNameChange(val name: String): DashBoardEvent()
    data class onGoalStudyHoursChange(val hours: String): DashBoardEvent()

}