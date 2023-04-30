package com.leandrolcd.doganalyzer.domain

import com.leandrolcd.doganalyzer.data.repositoty.LoginRepository
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.model.LoginUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SignUpUseCase @Inject constructor(
    private val repository: LoginRepository
) {

    suspend operator fun invoke(user: LoginUser): UiStatus<Any> {
        return repository.createUserWithEmailAndPassword(user)
    }
}