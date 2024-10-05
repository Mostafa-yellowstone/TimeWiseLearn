package myst.mostafayellowstone.timewiselearn.domin.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import myst.mostafayellowstone.timewiselearn.ui.theme.gradient1
import myst.mostafayellowstone.timewiselearn.ui.theme.gradient2
import myst.mostafayellowstone.timewiselearn.ui.theme.gradient3
import myst.mostafayellowstone.timewiselearn.ui.theme.gradient4
import myst.mostafayellowstone.timewiselearn.ui.theme.gradient5

@Entity
data class Subject (
    val name: String,
    val goalHours: Float,
    val colors: List<Int>,
    @PrimaryKey(autoGenerate = true)
    val subject_Id : Int? = null,

    ){
    companion object{
        val subjectCardColor = listOf(gradient1 , gradient2 , gradient3 , gradient4 , gradient5)
    }
}


