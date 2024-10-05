package myst.mostafayellowstone.timewiselearn.domin.repository

import kotlinx.coroutines.flow.Flow
import myst.mostafayellowstone.timewiselearn.domin.model.Task

interface TaskRepository {
    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(taskId: Int)

    suspend fun getTaskById(taskId: Int): Task?

    fun getUpComingTasksForSubject(subjectId: Int): Flow<List<Task>>

    fun getCompletedTaskForSubject(subjectId: Int): Flow<List<Task>>

    fun getAllUpComingTasks(): Flow<List<Task>>
}