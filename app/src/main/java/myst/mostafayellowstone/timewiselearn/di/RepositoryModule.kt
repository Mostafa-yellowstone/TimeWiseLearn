package myst.mostafayellowstone.timewiselearn.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import myst.mostafayellowstone.timewiselearn.data.repositoryImp.SessionRepositoryImpl
import myst.mostafayellowstone.timewiselearn.data.repositoryImp.SubjectRepositoryImpl
import myst.mostafayellowstone.timewiselearn.data.repositoryImp.TaskRepositoryImpl
import myst.mostafayellowstone.timewiselearn.domin.repository.SessionRepository
import myst.mostafayellowstone.timewiselearn.domin.repository.SubjectRepository
import myst.mostafayellowstone.timewiselearn.domin.repository.TaskRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSubjectRepository(
        impl: SubjectRepositoryImpl
    ): SubjectRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository
                (impl: SessionRepositoryImpl
    ): SessionRepository


}