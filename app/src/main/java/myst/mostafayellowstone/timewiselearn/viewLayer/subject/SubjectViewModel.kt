package myst.mostafayellowstone.timewiselearn.viewLayer.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.domin.repository.SessionRepository
import myst.mostafayellowstone.timewiselearn.domin.repository.SubjectRepository
import myst.mostafayellowstone.timewiselearn.domin.repository.TaskRepository
import myst.mostafayellowstone.timewiselearn.util.SnackBarEvent
import myst.mostafayellowstone.timewiselearn.util.toHours
import myst.navArgs
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(
) {

    private val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()

    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getUpComingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTaskForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionForSubject(navArgs.subjectId),
        sessionRepository.getTotalSesssionsDurationBySubject(navArgs.subjectId),
    ){
        state, upcomingTasks, completedTasks, recentSessions, totalSessionDuration ->
        state.copy(
            upComingTasks = upcomingTasks,
            completedTasks = completedTasks,
            recentSessions = recentSessions,
            studiedHours = totalSessionDuration.toHours()

        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SubjectState()
    )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    init {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvent){
        when(event){
            SubjectEvent.DeleteSession -> {}
            SubjectEvent.DeleteSubject -> deleteSubject()
            SubjectEvent.UpdateSubject -> updateSubject()
            is SubjectEvent.onDeleteSessionButtonClick -> {}
            is SubjectEvent.onGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }
            is SubjectEvent.onSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = event.color)
                }
            }
            is SubjectEvent.onSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            is SubjectEvent.onTaskCompleteChange -> {}
            SubjectEvent.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(0f,1f)
                    )
                }
            }
        }
    }
    private fun updateSubject() {
        viewModelScope.launch {
        try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        subject_Id = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.showSnackBar("Subject Updated Successfully")
                )
            }catch(e: Exception){
            _snackBarEventFlow.emit(
                SnackBarEvent.showSnackBar(msg = "Invalid ${e.message}",
                    SnackbarDuration.Long)
            )
        }
    }
 }

    private fun fetchSubject(){
        viewModelScope.launch {
            subjectRepository.getSubjectById(navArgs.subjectId)?.let {
                subject -> _state.update {
                    it.copy(
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { Color(it) },
                        currentSubjectId = subject.subject_Id

                    )
               }
            }
        }
    }
    private fun deleteSubject() {
        viewModelScope.launch {
            try {
                val currentSubjectId = state.value.currentSubjectId
                if (currentSubjectId != null) {
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
                    }
                    _snackBarEventFlow.emit(
                        SnackBarEvent.showSnackBar(msg = "Subject deleted successfully")
                    )
                    _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
                } else {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.showSnackBar(msg = "No Subject to delete")
                    )
                }
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.showSnackBar(
                        msg = "Couldn't delete subject. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )

            }
        }
    }
}