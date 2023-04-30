package com.leandrolcd.doganalyzer.domain

import com.leandrolcd.doganalyzer.data.repositoty.LoginRepository
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AuthLoginWithGoogleUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(idToken: String): UiStatus<Any> = repository.authLoginWithGoogle(idToken)
}