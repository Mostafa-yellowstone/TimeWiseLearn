package myst.mostafayellowstone.timewiselearn.data.repositoryImp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import myst.mostafayellowstone.timewiselearn.data.local.SessionDao
import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.repository.SessionRepository
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
): SessionRepository {
    override suspend fun insetSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    override fun getAllSession(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { sessions -> sessions.sortedByDescending { it.date } }
    }

    override fun getRecentFiveSession(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
            .map { sessions -> sessions.sortedByDescending { it.date }}
            .take(count = 5)

    }

    override fun getRecentTenSessionForSubject(subjectId: Int): Flow<List<Session>> {
        return sessionDao.getRecentSessionForSubject(subjectId)
            .map { sessions -> sessions.sortedByDescending { it.date }}
            .take(count = 10)
    }

    override fun getTotalSessionDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSesssionsDurationBySubject(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubject(subjectId)
    }
}