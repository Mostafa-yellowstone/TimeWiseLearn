package myst.mostafayellowstone.timewiselearn.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import myst.mostafayellowstone.timewiselearn.domin.model.Session

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: Session)
    @Delete
    suspend fun deleteSession(session: Session)
    @Query("SELECT * FROM Session")
    fun getAllSessions(): Flow<List<Session>>
    @Query("SELECT * FROM Session WHERE sessionSubjectId = :subjectId")
    fun getRecentSessionForSubject(subjectId:Int): Flow<List<Session>>
    @Query("SELECT SUM(duration) FROM Session")
    fun getTotalSessionDuration(): Flow<Long>
    @Query("SELECT SUM(duration) FROM Session WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long>
    @Query("DELETE FROM Session WHERE sessionSubjectId = :subjectId")
    fun deleteSessionBySubjectId(subjectId: Int)
}