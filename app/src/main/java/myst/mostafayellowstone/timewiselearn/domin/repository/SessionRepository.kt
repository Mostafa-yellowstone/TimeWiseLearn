package myst.mostafayellowstone.timewiselearn.domin.repository

import kotlinx.coroutines.flow.Flow
import myst.mostafayellowstone.timewiselearn.domin.model.Session

interface SessionRepository {
    suspend fun insetSession(session: Session)

    suspend fun deleteSession(session: Session)

    fun getAllSession(): Flow<List<Session>>

    fun getRecentFiveSession(): Flow<List<Session>>

    fun getRecentTenSessionForSubject(subjectId: Int): Flow<List<Session>>

    fun getTotalSessionDuration(): Flow<Long>

    fun getTotalSesssionsDurationBySubject(subjectId: Int): Flow<Long>
}