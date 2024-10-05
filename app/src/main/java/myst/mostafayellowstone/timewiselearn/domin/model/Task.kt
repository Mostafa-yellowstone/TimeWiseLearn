package myst.mostafayellowstone.timewiselearn.domin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task (
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority: Int,
    val relatedToSubject: String,
    val isComplete: Boolean,
    @PrimaryKey(autoGenerate = true)
    val task_Id: Int? = null,
    val subjectTask_id: Int
    )