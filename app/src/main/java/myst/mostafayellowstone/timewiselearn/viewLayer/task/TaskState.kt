package myst.mostafayellowstone.timewiselearn.viewLayer.task

import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.util.Priority


data class TaskState(
    val title: String ="",
    val description: String ="",
    val dueDate: Long? = null,
    val isTaskCompletet: Boolean = false,
    val priority: Priority = Priority.Low,
    val relatedToSubject: String? = null,
    val subjects: List<Subject> = emptyList(),
    val subjectId: Int? = null,
    val currentTaskId: Int? = null
)
