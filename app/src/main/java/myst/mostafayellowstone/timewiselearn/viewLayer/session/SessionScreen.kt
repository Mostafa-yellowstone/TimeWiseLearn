package myst.mostafayellowstone.timewiselearn.viewLayer.session

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import myst.mostafayellowstone.timewiselearn.sessions
import myst.mostafayellowstone.timewiselearn.subjects
import myst.mostafayellowstone.timewiselearn.ui.theme.Red
import myst.mostafayellowstone.timewiselearn.util.Constant.ACTION_SERVICE_DISMISS
import myst.mostafayellowstone.timewiselearn.util.Constant.ACTION_SERVICE_START
import myst.mostafayellowstone.timewiselearn.util.Constant.ACTION_SERVICE_STOP
import myst.mostafayellowstone.timewiselearn.viewLayer.components.DeleteSubjectDialog
import myst.mostafayellowstone.timewiselearn.viewLayer.components.SessionList
import myst.mostafayellowstone.timewiselearn.viewLayer.components.SubjectListBottomSheet

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = "time_wise_learn://dashboard/session"
        )
    ]
)
@Composable
fun SessionScreenRoute(navigator: DestinationsNavigator,
                       timerService: StudySessionTimerService){

    val viewModel: SessionViewModel = hiltViewModel()
    SessionScreen(onBackButtonClick = {navigator.navigateUp() },
        timerService = timerService)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(onBackButtonClick: () -> Unit,
                          timerService: StudySessionTimerService){
    val hours by timerService.hours
    val minuts by timerService.minuts
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimeState
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    SubjectListBottomSheet(sheetState = sheetState,
        isOpen = isBottomSheetOpen ,
        subjects = subjects ,
        onDismissRequest = {isBottomSheetOpen = false},
        onSubjectClicked = {
            scope.launch { sheetState.hide()}.invokeOnCompletion {
                if(!sheetState.isVisible) isBottomSheetOpen = false
            }
        }
    )


    DeleteSubjectDialog(title = "Delete Task?" ,
        isOpen = isDeleteDialogOpen,
        bodyText = "Are You sure?, you want to delete this task!" +
                " this action can not be restored",
        onDismissRequest = {isDeleteDialogOpen = false},
        onConfirmButtonClick = {isDeleteDialogOpen = false})


    Scaffold(
        topBar = {
            SessionScreenTopAppBar(onBackButtonClick = onBackButtonClick)
        }
    ) {
        paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TimerSection(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                    hours = hours,
                    minuts = minuts,
                    seconds = seconds
                )

            }
            item {
                RelatedToSubject(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                    relatedToSubject = "English",
                    selectSubjectButtonClick = {isBottomSheetOpen = true}
                )
            }
            item { 
                ButtonsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick ={
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = if (currentTimerState == StudySessionTimerService.TimerState.STARTED){
                                ACTION_SERVICE_STOP
                            }else ACTION_SERVICE_START
                        )
                    },
                    dismissButtonClick = {
                        ServiceHelper.triggerForegroundService(
                        context = context,
                        action = ACTION_SERVICE_DISMISS,
                    )},
                    finishButtonClick = {},
                    timerState = currentTimerState,
                    seconds = seconds
                )
            }
            SessionList(
                sectionTitle = " SESSION LAB HISTORY",
                emptyListText = "You don't have any recent sessions.\n" +
                        "Start Your session to record your progress",
                sessions = sessions,
                onDeleteIconClick = {
                    isDeleteDialogOpen = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreenTopAppBar(
    onBackButtonClick:() -> Unit
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "navigate back")
            }
        },
        title = { Text(text = "Session Lab" , style = MaterialTheme.typography.headlineSmall) },

    )
}


@Composable
private fun TimerSection(
    modifier: Modifier,
    hours: String,
    minuts: String,
    seconds: String
){
    Box(modifier=modifier,
        contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .size(250.dp)
            .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape))

        Row {
            AnimatedContent(
                targetState = hours,
                label = hours,
                transitionSpec = { timeTextAnimation() }) { hours ->
                Text(text = "$hours:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))

            }
            AnimatedContent(
                targetState = minuts,
                label = minuts,
                transitionSpec = { timeTextAnimation() }) { minuts ->
                Text(text = "$minuts:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))

            }
            AnimatedContent(
                targetState = seconds,
                label = "$seconds:",
                transitionSpec = { timeTextAnimation() }) { seconds ->
                Text(text = seconds,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))

            }
        }
    }
}
@Composable
private fun RelatedToSubject(
    modifier: Modifier,
    relatedToSubject: String,
    selectSubjectButtonClick:() -> Unit
){
    Column(modifier = modifier) {
    Text(
        text = "Related to ",
        style = MaterialTheme.typography.bodySmall
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "English",
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(onClick = selectSubjectButtonClick) {
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "select subject")
        }
    }
  }
}

@Composable
private fun ButtonsSection(
    modifier: Modifier,
    startButtonClick:() -> Unit,
    dismissButtonClick:() -> Unit,
    finishButtonClick:() -> Unit,
    timerState: StudySessionTimerService.TimerState,
    seconds: String
){
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = dismissButtonClick,
            enabled = seconds != "00" && timerState != StudySessionTimerService.TimerState.STARTED ) {
            Text(text = "Dismiss" ,modifier = Modifier.padding(horizontal = 10.dp , vertical = 5.dp))
        }
        Button(onClick = startButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == StudySessionTimerService.TimerState.STARTED) Red
                else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )) {
            Text(text = when(timerState){
                StudySessionTimerService.TimerState.IDLE -> "Start"
                StudySessionTimerService.TimerState.STARTED -> "Stop"
                StudySessionTimerService.TimerState.STOPPED -> "Resume"
                else -> "Start"
            } ,modifier = Modifier.padding(horizontal = 10.dp , vertical = 5.dp))
        }
        Button(onClick = finishButtonClick,
            enabled = seconds != "00" && timerState != StudySessionTimerService.TimerState.STARTED
            ) {
            Text(text = "Finish" ,modifier = Modifier.padding(horizontal = 10.dp , vertical = 5.dp))
        }
    }
}


private fun timeTextAnimation(duration: Int = 600): ContentTransform{
    return slideInVertically(animationSpec = tween(duration)) {
        fullHeight -> fullHeight
    } + fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically (animationSpec = tween(duration)){ fullHeight -> fullHeight } + fadeOut(animationSpec = tween(duration))
}