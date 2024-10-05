package myst.mostafayellowstone.timewiselearn.viewLayer.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialogPicker(
    state: DatePickerState,
    isOpen: Boolean,
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Dismiss",
    onDismissRequest:()-> Unit,
    onConfirmButtonClicked:()-> Unit
){
 if (isOpen) {
     DatePickerDialog(onDismissRequest = onDismissRequest,
         confirmButton = {
             TextButton(onClick = onConfirmButtonClicked) {
                 Text(text = confirmButtonText)
             }
         },
         dismissButton = {
             TextButton(onClick = onDismissRequest) {
                 Text(text = dismissButtonText)
             }
         },
         content = { DatePicker(state = state) }
     )
 }
}
