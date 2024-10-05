package myst.mostafayellowstone.timewiselearn.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import myst.mostafayellowstone.timewiselearn.domin.model.Subject


@Dao
interface SubjectDao {
    @Upsert
    suspend fun upsertSubject(subject: Subject)
    @Query("SELECT COUNT(*) FROM SUBJECT")
     fun getTotalSubjectCount(): Flow<Int>
    @Query("SELECT SUM(goalHours) FROM Subject")
     fun getTotalGoalHours():Flow<Float>
    @Query("SELECT * FROM Subject WHERE subject_Id = :subjectId")
    suspend fun getSubjectById(subjectId: Int): Subject?
    @Query("DELETE FROM Subject WHERE subject_Id = :subjectId")
    suspend fun deleteSubject(subjectId: Int)
    @Query("SELECT * FROM Subject")
    fun getAllSubjects(): Flow<List<Subject>>
}