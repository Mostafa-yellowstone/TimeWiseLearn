package myst.mostafayellowstone.timewiselearn.viewLayer.session

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.repository.SessionRepository
import myst.mostafayellowstone.timewiselearn.domin.repository.SubjectRepository
import myst.mostafayellowstone.timewiselearn.util.SnackBarEvent
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val subjectRepository: SubjectRepository
): ViewModel() {
    private val _state = MutableStateFlow(SessionState())
    val state = combine(
        _state,
        subjectRepository.getAllSubject(),
        sessionRepository.getAllSession(),
    ) { state, subjects, sessions ->
        state.copy(
            subjects = subjects,
            sessions = sessions,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionState()
    )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    fun onEvent(event: SessionEvent) {
        when (event) {
            SessionEvent.CheckSubjectId -> notifyToUpdateSubject()
            SessionEvent.DeleteSession -> deleteSession()
            is SessionEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy( session = event.session)
                }
            }
            is SessionEvent.OnRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subject_Id
                    )
                }
            }

            is SessionEvent.SaveSession -> insetSession(event.duration)
            is SessionEvent.UpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.relatedToSubject,
                        subjectId = event.subjectId
                    )
                }
            }

        }
    }

    private fun insetSession(duration: Long) {
        viewModelScope.launch {
            try {
                if (duration < 60) {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.showSnackBar(
                            "Session can not be less than a minute "
                        )
                    )
                    return@launch
                }
                sessionRepository.insetSession(
                    session = Session(
                        sessionSubjectId = state.value.subjectId ?: -1,
                        relatedToSubject = state.value.relatedToSubject ?: "",
                        date = Instant.now().toEpochMilli(),
                        duration = duration
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.showSnackBar(
                        "Session Saved Successfully"
                    )
                )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.showSnackBar(
                        "Invalid. ${e.message}", SnackbarDuration.Long
                    )
                )
            }
        }

    }

    private fun notifyToUpdateSubject() {
        viewModelScope.launch {
            if (state.value.subjectId == null || state.value.relatedToSubject == null) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.showSnackBar(
                        msg = "Please select subject related to the session."
                    )
                )
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                    _snackBarEventFlow.emit(
                        SnackBarEvent.showSnackBar(
                            "Session Deleted Successfully" , SnackbarDuration.Long
                        ))
                }
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.showSnackBar(
                        msg = "Invalid. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

}

