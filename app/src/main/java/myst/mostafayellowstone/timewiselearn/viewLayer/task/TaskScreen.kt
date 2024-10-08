package myst.mostafayellowstone.timewiselearn.viewLayer.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import myst.mostafayellowstone.timewiselearn.util.Priority
import myst.mostafayellowstone.timewiselearn.util.SnackBarEvent
import myst.mostafayellowstone.timewiselearn.util.changeMillisToDateString
import myst.mostafayellowstone.timewiselearn.viewLayer.components.DeleteSubjectDialog
import myst.mostafayellowstone.timewiselearn.viewLayer.components.SubjectListBottomSheet
import myst.mostafayellowstone.timewiselearn.viewLayer.components.TaskCheckBox
import myst.mostafayellowstone.timewiselearn.viewLayer.components.TaskDialogPicker
import java.time.Instant

data class TaskScreenNavArgs(
    val taskId: Int?,
    val subjectId: Int?
)

@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(
    navigator: DestinationsNavigator
){
    val viewModel: TaskViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    TaskScreen(
        state =  state,
        snackBarEvent = viewModel.snackBarEventFlow,
        onEvent = viewModel::onEvent,
        onBackButtonClick = {navigator.navigateUp()})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    state: TaskState,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onEvent: (TaskEvent) -> Unit,
    onBackButtonClick: () -> Unit
) {
    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    var taskTitleValidation by remember { mutableStateOf<String?>(null) }
    var isDatePickerDialogOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    taskTitleValidation = when {
        state.title.isBlank() -> "Please Enter Task Title!"
        state.title.length < 4 -> "Task Title is Too Short!"
        state.title.length > 30 -> "Task Title is Too Long!"
        else -> null
    }
    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event ->
            when(event){
                is SnackBarEvent.showSnackBar -> {
                    snackBarHostState.showSnackbar(
                        message = event.msg,
                        duration = event.duration
                    )
                }

                SnackBarEvent.NavigateUp -> {
                    onBackButtonClick()
                }
            }
        }
    }

    DeleteSubjectDialog(title = "Delete Session?" ,
        isOpen = isDeleteDialogOpen,
        bodyText ="Are you sure ?, you want to delete this session! your studied hours will be reduced" +
                " by this session time, this action can not be restored.",
                onDismissRequest = {isDeleteDialogOpen = false},
        onConfirmButtonClick = {
            onEvent(TaskEvent.DeleteTask)
            isDeleteDialogOpen = false})

    TaskDialogPicker(state = datePickerState ,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen = false },
        onConfirmButtonClicked = {
            onEvent(TaskEvent.OnDateChange(millis = datePickerState.selectedDateMillis))
            isDatePickerDialogOpen = false}
    )

    SubjectListBottomSheet(sheetState = sheetState,
        isOpen = isBottomSheetOpen ,
        subjects = state.subjects ,
        onSubjectClicked = { subject ->
            scope.launch { sheetState.hide()}.invokeOnCompletion {
                if(!sheetState.isVisible) isBottomSheetOpen = false }
            onEvent(TaskEvent.OnRelatedSubjectSelect(subject))
        },
        onDismissRequest = {isBottomSheetOpen = false},)
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TaskScreenTopAppBar(
                isTaskExist = state.currentTaskId != null,
                isComplete = state.isTaskCompletet,
                checkBoxBorderColor = state.priority.color,
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = {isDeleteDialogOpen = true},
                onCheckBoxClick = {
                    onEvent(TaskEvent.OnIsCompleteChange)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = { onEvent(TaskEvent.OnTitleChange(it)) },
                label = { Text(text = "Title") },
                singleLine = true,
                isError = taskTitleValidation != null && state.title.isNotBlank(),
                supportingText = {
                    Text(text = taskTitleValidation.orEmpty())
                })
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = { onEvent(TaskEvent.OnDescriptionChange(it)) },
                label = { Text(text = "Description") },
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Due Date",
                style = MaterialTheme.typography.bodySmall
            )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.dueDate.changeMillisToDateString(),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = {isDatePickerDialogOpen = true }) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "select due date")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Priority",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Priority.entries.forEach { priority ->
                PriorityButton(
                    modifier = Modifier.weight(1f),
                    label = priority.title,
                    background = priority.color,
                    labelColor = if (priority == state.priority) {
                        Color.White
                    } else Color.White.copy(alpha = 0.7f),
                    borderColor = if (priority == state.priority) {
                        Color.White
                    } else Color.Transparent,
                    onClick = {
                        onEvent(TaskEvent.OnPriorityChange(priority))
                    }
                )
            }
          }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Related to ",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val firstSubject = state.subjects.firstOrNull()?.name ?: ""
                Text(
                    text = state.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {isBottomSheetOpen = true }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "select subject")
                }
            }
            Button(
                enabled = taskTitleValidation == null,
                onClick = { onEvent(TaskEvent.SaveTask) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text(text = "Save")
            }
        }
     }
  }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopAppBar(
    isTaskExist: Boolean,

    isComplete: Boolean,
    checkBoxBorderColor: Color,
    onBackButtonClick:()-> Unit,
    onDeleteButtonClick:()-> Unit,
    onCheckBoxClick: ()-> Unit,
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick =  onBackButtonClick ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription =" navigate back" )
            }
        },
        title = { Text(text = "Tasks" , style = MaterialTheme.typography.headlineSmall) },
        actions = {
            if(isTaskExist){
                TaskCheckBox(isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick=  onCheckBoxClick)
            }
            IconButton(onClick =  onDeleteButtonClick ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription =" Delete" )
            }
        })
}

@Composable
private fun PriorityButton(modifier: Modifier = Modifier,
                           label: String,
                           background: Color,
                           labelColor: Color,
                           borderColor: Color,
                           onClick:() -> Unit){
    Box(modifier = modifier
        .background(background)
        .clickable { onClick() }
        .padding(5.dp)
        .border(
            1.dp, borderColor, RoundedCornerShape(5.dp)
        )
        .padding(5.dp),
        contentAlignment = Alignment.Center){
            Text(text = label, color = labelColor)
    }
}
