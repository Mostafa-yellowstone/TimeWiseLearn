package myst.mostafayellowstone.timewiselearn.viewLayer.session

import myst.mostafayellowstone.timewiselearn.domin.model.Session
import myst.mostafayellowstone.timewiselearn.domin.model.Subject

sealed class SessionEvent {
    data class OnRelatedSubjectChange(val subject: Subject): SessionEvent()
    data class SaveSession(val duration: Long): SessionEvent()
    data class OnDeleteSessionButtonClick(val session: Session): SessionEvent()
    data object DeleteSession: SessionEvent()
    data object CheckSubjectId: SessionEvent()
    data class UpdateSubjectIdAndRelatedSubject(
        val subjectId: Int?,
        val relatedToSubject: String?
    ): SessionEvent()
}