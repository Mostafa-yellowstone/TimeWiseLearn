package myst.mostafayellowstone.timewiselearn.data.repositoryImp

import kotlinx.coroutines.flow.Flow
import myst.mostafayellowstone.timewiselearn.data.local.SessionDao
import myst.mostafayellowstone.timewiselearn.data.local.SubjectDao
import myst.mostafayellowstone.timewiselearn.data.local.TaskDao
import myst.mostafayellowstone.timewiselearn.domin.model.Subject
import myst.mostafayellowstone.timewiselearn.domin.repository.SubjectRepository

import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
): SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDao.deleteTaskBySubjectId(subjectId)
        sessionDao.deleteSessionBySubjectId(subjectId)
        subjectDao.deleteSubject(subjectId)
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override fun getAllSubject(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }
}