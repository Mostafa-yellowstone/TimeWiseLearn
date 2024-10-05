package myst.mostafayellowstone.timewiselearn.viewLayer.session

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import myst.mostafayellowstone.timewiselearn.sessions
import myst.mostafayellowstone.timewiselearn.subjects
import myst.mostafayellowstone.timewiselearn.viewLayer.components.DeleteSubjectDialog
import myst.mostafayellowstone.timewiselearn.viewLayer.components.SessionList
import myst.mostafayellowstone.timewiselearn.viewLayer.components.SubjectListBottomSheet

@Destination
@Composable
fun SessionScreenRoute(navigator: DestinationsNavigator){

    val viewModel: SessionViewModel = hiltViewModel()
    SessionScreen(onBackButtonClick = {navigator.navigateUp() } )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(onBackButtonClick: () -> Unit){
    val sheetState = rememberModalBottomSheetState()
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
                    .aspectRatio(1f))
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
                    startButtonClick ={},
                    dismissButtonClick = {},
                    finishButtonClick = {}
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
    modifier: Modifier
){
    Box(modifier=modifier,
        contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .size(250.dp)
            .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape))
            Text(text = "00:05:32",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))
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
){
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = dismissButtonClick) {
            Text(text = "Dismiss" ,modifier = Modifier.padding(horizontal = 10.dp , vertical = 5.dp))
        }
        Button(onClick = startButtonClick) {
            Text(text = "Start" ,modifier = Modifier.padding(horizontal = 10.dp , vertical = 5.dp))
        }
        Button(onClick = finishButtonClick) {
            Text(text = "Finish" ,modifier = Modifier.padding(horizontal = 10.dp , vertical = 5.dp))
        }
    }
}