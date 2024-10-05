package myst.mostafayellowstone.timewiselearn.viewLayer.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteSubjectDialog(
    title: String,
    isOpen: Boolean,
    bodyText: String,
    onDismissRequest:() -> Unit,
    onConfirmButtonClick:() -> Unit,
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },

            text = {
                Text(text = bodyText)
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick){
              Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Dismiss")
                }
            }
        )
    }
}