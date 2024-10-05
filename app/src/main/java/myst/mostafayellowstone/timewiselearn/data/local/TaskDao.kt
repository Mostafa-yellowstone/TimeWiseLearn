package myst.mostafayellowstone.timewiselearn.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import myst.mostafayellowstone.timewiselearn.domin.model.Task


@Dao
interface TaskDao {
    @Upsert
    suspend fun upsertTask(task: Task)
    @Query("DELETE FROM Task WHERE task_Id = :taskId")
    suspend fun deleteTask(taskId: Int)
    @Query("DELETE FROM Task WHERE subjectTask_id = :subjectId")
    suspend fun deleteTaskBySubjectId(subjectId: Int)
    @Query("SELECT * FROM Task WHERE task_Id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?
    @Query("SELECT * FROM Task WHERE subjectTask_id = :subjectId")
    fun getTaskForSubject(subjectId: Int): Flow<List<Task>>
    @Query("SELECT * FROM Task")
    fun getAllTasks(): Flow<List<Task>>
}