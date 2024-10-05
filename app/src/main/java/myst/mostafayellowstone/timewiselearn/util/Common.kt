package myst.mostafayellowstone.timewiselearn.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import myst.mostafayellowstone.timewiselearn.ui.theme.Green
import myst.mostafayellowstone.timewiselearn.ui.theme.Orange
import myst.mostafayellowstone.timewiselearn.ui.theme.Red
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(val title: String,
    val color: Color,
    val value: Int,
){
    Low(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);


    companion object{
        fun fromInt(value: Int) = values().firstOrNull { it.value == value } ?: MEDIUM
    }

}

fun Long?.changeMillisToDateString(): String{
    val date: LocalDate = this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

fun Long.toHours(): Float{
    val hours = this.toFloat() / 3600f
    return "%.2f".format(hours).toFloat()
}

sealed class SnackBarEvent{
    data class showSnackBar(val msg: String,
                            val duration: SnackbarDuration =  SnackbarDuration.Short): SnackBarEvent()
    data object NavigateUp: SnackBarEvent()
}

fun Int.pad(): String{
    return this.toString().padStart(length = 2, padChar = '0' )
}
