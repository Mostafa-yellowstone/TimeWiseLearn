package myst.mostafayellowstone.timewiselearn.domin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session (
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int? = null,
    val relatedToSubject: String,
    val date: Long,
    val duration: Long,
    val sessionSubjectId: Int
)