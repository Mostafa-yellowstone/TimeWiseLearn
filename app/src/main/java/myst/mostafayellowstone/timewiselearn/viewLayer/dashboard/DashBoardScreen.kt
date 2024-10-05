package myst.D
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import myst.destinations.SessionScreenRouteDestination
import myst.destinations.SubjectScreenRouteDestination
import myst.destinations.TaskScreenRouteDestination
import myst.mostafayellowstone.timewiselearn.R
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.domin.model.Task
import myst.mostafayellowstone.timewiselearn.sessions
import myst.mostafayellowstone.timewiselearn.subjects
import myst.mostafayellowstone.timewiselearn.tasks
import myst.mostafayellowstone.timewiselearn.util.SnackBarEvent
import myst.mostafayellowstone.timewiselearn.viewLayer.components.AddSubjectDialog
import myst.mostafayellowstone.timewiselearn.viewLayer.components.CountCard
import myst.mostafayellowstone.timewiselearn.viewLayer.components.DeleteSubjectDialog
import myst.mostafayellowstone.timewiselearn.viewLayer.components.SessionList
import myst.mostafayellowstone.timewiselearn.viewLayer.components.SubjectCard
import myst.mostafayellowstone.timewiselearn.viewLayer.components.taskList
import myst.mostafayellowstone.timewiselearn.viewLayer.dashboard.DashBoardEvent
import myst.mostafayellowstone.timewiselearn.viewLayer.dashboard.DashBoardState
import myst.mostafayellowstone.timewiselearn.viewLayer.dashboard.DashBoardViewModel
import myst.mostafayellowstone.timewiselearn.viewLayer.subject.SubjectScreenNavArgs
import myst.mostafayellowstone.timewiselearn.viewLayer.task.TaskScreenNavArgs


@Destination(start = true)
@Composable
fun DashBoardScreenRoute(
    navigator: DestinationsNavigator
){
    val viewModel: DashBoardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSession.collectAsStateWithLifecycle()
    DashBoardScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackBarEvent = viewModel.snackBarEventFlow ,
        tasks = tasks,
        recentSessions = recentSessions,
        onSubjectCardClick = {
        subjectId ->
        subjectId?.let { val navArg = SubjectScreenNavArgs(subjectId = subjectId)
            navigator.navigate(SubjectScreenRouteDestination(navArgs = navArg))
        }
    },
        onTaskCardClick = {
            taskId ->
            val navArg = TaskScreenNavArgs(taskId = taskId, subjectId = null)
                navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        } ,
        onStartSessionCardClick = {
            navigator.navigate(SessionScreenRouteDestination())
        })
}


@Composable
private fun DashBoardScreen(
    state: DashBoardState,
    tasks: List<Task>,
    recentSessions: List<Session>,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onEvent: (DashBoardEvent) -> Unit,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit,
    onStartSessionCardClick: () -> Unit,

) {
    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }


    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event ->
            when(event){
                is SnackBarEvent.showSnackBar -> {
                    snackBarHostState.showSnackbar(
                        message = event.msg,
                        duration = event.duration
                    )
                }

                SnackBarEvent.NavigateUp -> {}
            }
        }
    }


    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen ,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = {onEvent(DashBoardEvent.onSubjectNameChange(it))} ,
        onGoalHoursChange = {onEvent(DashBoardEvent.onGoalStudyHoursChange(it))} ,
        selectedColor = state.subjectCardColors,
        onColorChanged = {onEvent(DashBoardEvent.onSubjectCardColorChange(it))},
        onDismissRequest = { isAddSubjectDialogOpen = false},
        onConfirmButtonClick = {
            onEvent(DashBoardEvent.SaveSubject)
            isAddSubjectDialogOpen = false
        }
    )
    DeleteSubjectDialog(
        title = "Delete Session?",
        isOpen = isDeleteSubjectDialogOpen,
        bodyText = "Are you sure ?, you want to delete this session! your studied hours will be reduced" +
                " by this session time, this action can not be restore. " ,
        onConfirmButtonClick = {isDeleteSubjectDialogOpen = false},
        onDismissRequest = {
            onEvent(DashBoardEvent.DeleteSession)
            isDeleteSubjectDialogOpen = false })

    Scaffold (
        snackbarHost = {SnackbarHost(hostState = snackBarHostState)},
        topBar = { DashBoardScreenTopBar()}
    ) { paddingValues ->
       LazyColumn(
           modifier = Modifier
               .fillMaxSize()
               .padding(paddingValues)
       )
       {
           item {
               CountCardView(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(12.dp),
                   subjectCount = state.totalSubjectCount,
                   studiedHours = state.totalStudeiedHours.toString() ,
                   goalHours = state.totalGoalStudyHours.toString())
           }
           item {
               SubjectCardView(
                   modifier = Modifier.fillMaxWidth(),
                   subjectList = state.subjects ,
                   onAddIconClicked = { isAddSubjectDialogOpen = true},
                   onSubjectCardClick = onSubjectCardClick)
           }
           item{
               Button(onClick = onStartSessionCardClick,
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 48.dp, vertical = 20.dp)) {
                   
                   Text(text = "Start Your Session")
               }
           }
           taskList(
               sectionTitle = "UPCOMING TASKS",
               emptyListText = "You don't have any upcoming tasks.\n" +
                       " Click + button in subject screen to do your tasks",
               tasks = tasks,
               onCheckBoxClick = {onEvent(DashBoardEvent.onTaskIsCompleteChange(it))},
               onTaskCardClick = onTaskCardClick
           )
           item{
               Spacer(modifier = Modifier.height(20.dp))
           }
           SessionList(
               sectionTitle = "Recent Study Session",
               emptyListText = "You don't have any recent sessions.\n" +
                       "Start Your session to record your progress",
               sessions = recentSessions,
               onDeleteIconClick = {
                   onEvent(DashBoardEvent.onDeleteSessionButtonClick(it))
                   isDeleteSubjectDialogOpen = true}
           )
       }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashBoardScreenTopBar(){
    CenterAlignedTopAppBar(
        title = { Text(text = "Feed one's mind", style = MaterialTheme.typography.headlineMedium) })
}

@Composable
private fun CountCardView(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalHours: String
){
    Row (modifier = modifier){
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Subject Count",
            count = "$subjectCount")
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours)
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours)
    }
}


@Composable
private fun SubjectCardView(
    modifier: Modifier,
    subjectList: List<Subject>,
    onAddIconClicked:() -> Unit,
    onSubjectCardClick: (Int?) -> Unit,
    emptyListText: String = "You don't have any subjects.\n Click the + button to add new subject. "
){
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp))
            IconButton(onClick = onAddIconClicked) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject")
            }
        }
        if (subjectList.isEmpty()){
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.book),
                contentDescription = emptyListText)
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp , end = 12.dp)
        ) {
           items(subjectList){ subject ->
               SubjectCard(
                   subjectName = subject.name,
                   gradientColors = subject.colors.map { Color(it) },
                   onClick = { onSubjectCardClick(subject.subject_Id) })
           }
        }
    }
}