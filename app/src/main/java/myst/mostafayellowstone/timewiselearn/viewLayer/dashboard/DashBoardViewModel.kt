package myst.mostafayellowstone.timewiselearn.viewLayer.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.domin.model.Task
import myst.mostafayellowstone.timewiselearn.domin.repository.SessionRepository
import myst.mostafayellowstone.timewiselearn.domin.repository.SubjectRepository
import myst.mostafayellowstone.timewiselearn.domin.repository.TaskRepository
import myst.mostafayellowstone.timewiselearn.util.SnackBarEvent
import myst.mostafayellowstone.timewiselearn.util.toHours
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository,
): ViewModel() {
    private val _state = MutableStateFlow(DashBoardState())
    val state = combine(
        _state ,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHours(),
        subjectRepository.getAllSubject(),
        sessionRepository.getTotalSessionDuration()
    ){
        state, subjectCount, goalHours, subjects, totalsessionDuration ->
        state.copy(
            totalSubjectCount = subjectCount,
            totalGoalStudyHours = goalHours,
            subjects = subjects,
            totalStudeiedHours = totalsessionDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = DashBoardState()
    )

    val tasks: StateFlow<List<Task>> = taskRepository.getAllUpComingTasks()
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = emptyList()
    )
    val recentSession: StateFlow<List<Session>> = sessionRepository.getRecentFiveSession()
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = emptyList()
    )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    fun onEvent(event: DashBoardEvent){
        when(event){
            DashBoardEvent.DeleteSession -> TODO()
            DashBoardEvent.SaveSubject -> saveSubject()
            is DashBoardEvent.onDeleteSessionButtonClick ->   _state.update {
                it.copy(session = event.session)
            }
            is DashBoardEvent.onGoalStudyHoursChange ->
                _state.update {
                it.copy(goalStudyHours = event.hours)
            }
            is DashBoardEvent.onSubjectCardColorChange ->   _state.update {
                it.copy(subjectCardColors = event.color)
            }
            is DashBoardEvent.onSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            is DashBoardEvent.onTaskIsCompleteChange -> TODO()
        }
    }

    private fun saveSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull()?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectCardColors = Subject.subjectCardColor.random()
                    )
                }
                _snackBarEventFlow.emit(SnackBarEvent.showSnackBar(
                    "Subject Added Successfully"
                ))

            }catch (e:Exception){
                _snackBarEventFlow.emit(SnackBarEvent.showSnackBar(
                    "Invalid. ${e.message}" , SnackbarDuration.Long
                ))
            }

        }
    }
}