package myst.mostafayellowstone.timewiselearn.viewLayer.session

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import myst.mostafayellowstone.timewiselearn.domin.repository.SessionRepository
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
): ViewModel() {


}