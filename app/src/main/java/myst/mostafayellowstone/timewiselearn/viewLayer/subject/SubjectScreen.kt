package myst.mostafayellowstone.timewiselearn.viewLayer.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import myst.destinations.TaskScreenRouteDestination
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.sessions
import myst.mostafayellowstone.timewiselearn.tasks
import myst.mostafayellowstone.timewiselearn.util.SnackBarEvent
import myst.mostafayellowstone.timewiselearn.viewLayer.components.AddSubjectDialog
import myst.mostafayellowstone.timewiselearn.viewLayer.components.CountCard
import myst.mostafayellowstone.timewiselearn.viewLayer.components.DeleteSubjectDialog
import myst.mostafayellowstone.timewiselearn.viewLayer.components.SessionList
import myst.mostafayellowstone.timewiselearn.viewLayer.components.taskList
import myst.mostafayellowstone.timewiselearn.viewLayer.task.TaskScreenNavArgs

data class SubjectScreenNavArgs(
    val subjectId: Int
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(navigator: DestinationsNavigator){
    val viewModel: SubjectViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SubjectScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackBarEvent = viewModel.snackBarEventFlow ,
        onBackButtonClick = {navigator.navigateUp()},
        onAddTaskClick = {
            val navArg = TaskScreenNavArgs(taskId = null, subjectId = -1)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
        onTaskCardClick = {
                taskId ->
            val navArg = TaskScreenNavArgs(taskId = taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(
    state: SubjectState,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onEvent: (SubjectEvent) -> Unit,
    onBackButtonClick: () -> Unit,
    onAddTaskClick: ()-> Unit,
    onTaskCardClick:(Int?) -> Unit
){

    val snackBarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val isFABExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
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

    LaunchedEffect(key1 = state.studiedHours , key2 = state.goalStudyHours){
        onEvent(SubjectEvent.UpdateProgress)
    }

    var isAddSubjectDialogOpen by remember { mutableStateOf(false) }
    var isDeleteSubjectDialogOpen by remember { mutableStateOf(false)}
    var isEditSubjectDialogOpen by remember { mutableStateOf(false)}
    var isDeleteSessionDialogOpen by remember { mutableStateOf(false) }
        AddSubjectDialog(
        isOpen = isEditSubjectDialogOpen ,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = {onEvent(SubjectEvent.onSubjectNameChange(it))} ,
        onGoalHoursChange = {onEvent(SubjectEvent.onGoalStudyHoursChange(it))} ,
        selectedColor = state.subjectCardColors,
        onColorChanged = {onEvent(SubjectEvent.onSubjectCardColorChange(it))},
        onDismissRequest = { isAddSubjectDialogOpen = false},
        onConfirmButtonClick = {
            onEvent(SubjectEvent.UpdateSubject)
            isEditSubjectDialogOpen = false
        }
    )
    DeleteSubjectDialog(
        title = "Delete Session?",
        isOpen = isDeleteSessionDialogOpen,
        bodyText = "Are you sure ?, you want to delete this session! your studied hours will be reduced" +
                " by this session time, this action can not be restore. " ,
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSession)
            isDeleteSessionDialogOpen = false },
        onDismissRequest = { isDeleteSessionDialogOpen = false })


    DeleteSubjectDialog(
        title = "Delete Subject?",
        isOpen = isDeleteSubjectDialogOpen,
        bodyText = "Are you sure ?, you want to delete this Subject! all related tasks and study sessions will be removed" +
                " , this action can not be restore." ,
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSubject)
            isDeleteSubjectDialogOpen = false},
        onDismissRequest = { isDeleteSubjectDialogOpen = false })


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState)},
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopAppBar(
                title = state.subjectName,
                onBackButtonClicks = onBackButtonClick,
                onDeleteButtonClicks = { isDeleteSubjectDialogOpen = true },
                onEditButtonClicks = { isEditSubjectDialogOpen = true},
                scrollBehavior = scrollBehavior
                )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskClick,
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription ="Add")},
                text = { Text(text = "Add Task")},
                expanded = isFABExpanded
                )
        }
    ) {
        paddingValues -> LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
        item {
            SubjectOverViewSection(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
                studiedHours = state.studiedHours.toString(),
                goalHours = state.goalStudyHours,
                progress = state.progress )
        }
        taskList(
            sectionTitle = "UPCOMING TASKS",
            emptyListText = "You don't have any upcoming tasks.\n" +
                    " Click + button in subject screen to do your tasks",
            tasks = state.upComingTasks,
            onCheckBoxClick = {onEvent(SubjectEvent.onTaskCompleteChange(it))},
            onTaskCardClick = onTaskCardClick
        )
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }
        taskList(
            sectionTitle = "COMPLETED TASKS",
            emptyListText = "You don't have any completed tasks.\n" +
                    "  Click check button on completion of tasks",
            tasks = state.completedTasks,
            onCheckBoxClick = {onEvent(SubjectEvent.onTaskCompleteChange(it))},
            onTaskCardClick = onTaskCardClick
        )
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }
        SessionList(
            sectionTitle = "RECENT STUDY SESSION",
            emptyListText = "You don't have any recent sessions.\n" +
                    "Start Your session to record your progress",
            sessions = state.recentSessions,
            onDeleteIconClick = {
                onEvent(SubjectEvent.onDeleteSessionButtonClick(it))
                isDeleteSessionDialogOpen = true
            }
        )
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreenTopAppBar(
    title: String,
    onBackButtonClicks:()-> Unit,
    onDeleteButtonClicks:()-> Unit,
    onEditButtonClicks:()-> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
){

    LargeTopAppBar(
        scrollBehavior =scrollBehavior ,
        navigationIcon = {
            IconButton(onClick = onBackButtonClicks) {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "navigate back")
            }
        },
        title = { Text(text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineSmall)},
        actions = {
            IconButton(onClick = onDeleteButtonClicks) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Subject"
                )
            }
            IconButton(onClick = onEditButtonClicks) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Subject"
                )
            }
        }
     )
}
@Composable
private fun SubjectOverViewSection(
    modifier: Modifier,
    studiedHours: String,
    goalHours: String,
    progress: Float
){
    val percentageProgress = remember(progress) {
        (progress * 100).toInt().coerceIn(0,100)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText ="Goal Study Hours",
            count = goalHours)
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText ="Study Hours",
            count = studiedHours)
        Spacer(modifier = Modifier.width(10.dp))
        Box (
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(text = "$percentageProgress%")
        }
    }

}
