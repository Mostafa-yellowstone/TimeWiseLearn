package myst.mostafayellowstone.timewiselearn.domin.repository

import kotlinx.coroutines.flow.Flow
import myst.mostafayellowstone.timewiselearn.domin.model.Subject

interface SubjectRepository {
    suspend fun upsertSubject(subject: Subject)
    fun getTotalSubjectCount():Flow<Int>
    fun getTotalGoalHours(): Flow<Float>
    suspend fun deleteSubject(subjectId: Int)
    suspend fun getSubjectById(subjectId: Int): Subject?
    fun getAllSubject(): Flow<List<Subject>>
}