package myst.mostafayellowstone.timewiselearn.viewLayer.task

import androidx.compose.material3.SnackbarDuration
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
import myst.mostafayellowstone.timewiselearn.domin.model.Task
import myst.mostafayellowstone.timewiselearn.domin.repository.SubjectRepository
import myst.mostafayellowstone.timewiselearn.domin.repository.TaskRepository
import myst.mostafayellowstone.timewiselearn.util.Priority
import myst.mostafayellowstone.timewiselearn.util.SnackBarEvent
import myst.navArgs
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val navArgs: TaskScreenNavArgs = savedStateHandle.navArgs()
    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subjectRepository.getAllSubject()
    ){
        state, subjects ->
        state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskState()
    )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    init {
        fetchTask()
        fetchSubject()
    }

    fun onEvent(event: TaskEvent){
        when(event){
            TaskEvent.DeleteTask -> deleteTask()
            is TaskEvent.OnDateChange -> {
                _state.update { it.copy(dueDate = event.millis) }
            }
            is TaskEvent.OnDescriptionChange -> {
                _state.update { it.copy(description = event.description) }
            }
            TaskEvent.OnIsCompleteChange -> {
                _state.update { it.copy(isTaskCompletet  =! _state.value.isTaskCompletet) }
            }
            is TaskEvent.OnPriorityChange -> {
                _state.update { it.copy(priority = event.priority) }
            }
            is TaskEvent.OnRelatedSubjectSelect -> {
                _state.update { it.copy(relatedToSubject = event.subject.name,
                    subjectId = event.subject.subject_Id) }
            }
            is TaskEvent.OnTitleChange -> {
                _state.update { it.copy(title = event.title) }
            }
            TaskEvent.SaveTask -> saveTask()
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
        try {
            val currentTaskId = state.value.currentTaskId
            if (currentTaskId != null) {
                withContext(Dispatchers.IO) {
                    taskRepository.deleteTask(taskId = currentTaskId)
                }
                _snackBarEventFlow.emit(
                    SnackBarEvent.showSnackBar(msg = "Task deleted successfully")
                )
                _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
            } else {
                _snackBarEventFlow.emit(
                    SnackBarEvent.showSnackBar(msg = "No Task to delete")
                )
            }
        } catch (e: Exception) {
            _snackBarEventFlow.emit(
                SnackBarEvent.showSnackBar(
                    msg = "Couldn't delete Task. ${e.message}",
                    duration = SnackbarDuration.Long
                )
            )
        }
    }
}

    private fun saveTask() {
        viewModelScope.launch {

            val state = _state.value
            if (state.subjectId == null || state.relatedToSubject == null) {
                _snackBarEventFlow.emit(SnackBarEvent.showSnackBar(
                    "please select subject related to this task" , SnackbarDuration.Long
                ))
                return@launch
            }
            try {
            taskRepository.upsertTask(
                task = Task(
                    title = state.title,
                    description = state.description,
                    dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                    relatedToSubject = state.relatedToSubject,
                    priority = state.priority.value,
                    isComplete = state.isTaskCompletet,
                    subjectTask_id = state.subjectId,
                    task_Id = state.currentTaskId,
                )
            )
                _snackBarEventFlow.emit(SnackBarEvent.showSnackBar(
                    "Task Saved Successfully"
                ))
                _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
           }catch (e:Exception){
                _snackBarEventFlow.emit(SnackBarEvent.showSnackBar(
                    "Invalid Task. ${e.message}" , SnackbarDuration.Long
                ))
            }
        }
     }
    private fun fetchTask(){
        viewModelScope.launch {
            navArgs.taskId?.let { id ->
                taskRepository.getTaskById(id)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.dueDate,
                            isTaskCompletet = task.isComplete,
                            relatedToSubject = task.relatedToSubject,
                            priority = Priority.fromInt(task.priority),
                            subjectId = task.subjectTask_id,
                            currentTaskId = task.task_Id
                        )
                    }
                }
            }
        }
    }
    private fun fetchSubject(){
        viewModelScope.launch {
            navArgs.subjectId?.let { id ->
                subjectRepository.getSubjectById(id)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectId = subject.subject_Id,
                            relatedToSubject = subject.name
                        )
                    }
                }
            }
        }
    }
}