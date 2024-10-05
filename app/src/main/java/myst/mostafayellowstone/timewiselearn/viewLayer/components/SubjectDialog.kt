package myst.mostafayellowstone.timewiselearn.viewLayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import myst.mostafayellowstone.timewiselearn.domin.model.Subject


@Composable
fun AddSubjectDialog(
    title: String = "Add/Update Subject",
    isOpen: Boolean,
    selectedColor: List<Color>,
    onColorChanged: (List<Color>)-> Unit,
    onDismissRequest:() -> Unit,
    onConfirmButtonClick:() -> Unit,
    subjectName: String,
    goalHours: String,
    onSubjectNameChange: (String)-> Unit,
    onGoalHoursChange: (String)-> Unit
) {
    var subjectNameValidation by remember { mutableStateOf<String?>(null) }
    var goalHoursValidation by remember { mutableStateOf<String?>(null) }

    subjectNameValidation = when{
        subjectName.isBlank() -> "Please Enter Subject name!"
        subjectName.length < 2 -> "Subject Name is too short!"
        subjectName.length > 20 -> "Subject Name is too Long!"
        else -> null
    }

    goalHoursValidation = when{
        goalHours.isBlank() -> "Please Enter Goal Study Hours!"
        goalHours.toFloatOrNull() == null -> "Invalid number"
        goalHours.toFloat() < 1f -> "Please Enter at least 1 hour!"
        goalHours.toFloat() > 1000f -> "Please set a maximum of 1000 hours!"
        else -> null
    }

    if (isOpen) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },

            text = {
                Column {
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround){
                    Subject.subjectCardColor.forEach{
                        colors -> Box(modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(colors)
                        )
                        .clickable { onColorChanged(colors) }
                        .border(
                            width = 1.dp, color = if (colors == selectedColor) {
                                Color.Black
                            } else
                                Color.Transparent, shape = CircleShape
                        ))

                    }
                  }
                    OutlinedTextField(value =subjectName , onValueChange = onSubjectNameChange,
                        label = { Text(text = "Subject Name")},
                        singleLine = true,
                        isError = subjectNameValidation != null && subjectName.isNotBlank(),
                        supportingText = { Text(text = subjectNameValidation.orEmpty())} )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(value =goalHours , onValueChange = onGoalHoursChange,
                        label = { Text(text = "Goal Study Hours")},
                        singleLine = true,
                        isError = goalHoursValidation != null && goalHours.isNotBlank(),
                        supportingText = { Text(text = goalHoursValidation.orEmpty())},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick ,
                    enabled = subjectNameValidation == null && goalHoursValidation == null) {
                    Text(text = "Save")

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