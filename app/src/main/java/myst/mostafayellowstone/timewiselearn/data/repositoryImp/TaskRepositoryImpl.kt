package myst.mostafayellowstone.timewiselearn.data.repositoryImp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import myst.mostafayellowstone.timewiselearn.data.local.TaskDao
import myst.mostafayellowstone.timewiselearn.domin.model.Task
import myst.mostafayellowstone.timewiselearn.domin.repository.TaskRepository
import myst.mostafayellowstone.timewiselearn.tasks
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
): TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        TODO("Not yet implemented")
    }

    override fun getUpComingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTaskForSubject(subjectId)
            .map { tasks -> tasks.filter { it.isComplete.not() } }
            .map { tasks -> sortTask(tasks)}
    }

    override fun getCompletedTaskForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTaskForSubject(subjectId)
            .map { tasks -> tasks.filter { it.isComplete } }
            .map { tasks -> sortTask(tasks)}
    }

    override fun getAllUpComingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
            .map { tasks -> tasks.filter {
                it.isComplete.not()
            }}.map { tasks -> sortTask(tasks)
        }
    }
    private fun sortTask(tasks: List<Task>): List<Task>{
        return tasks.sortedWith(compareBy<Task>{ it.dueDate }.thenByDescending { it.priority})
    }

}