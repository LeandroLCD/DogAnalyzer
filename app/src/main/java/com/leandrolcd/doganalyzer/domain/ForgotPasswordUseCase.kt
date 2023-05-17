package com.leandrolcd.doganalyzer.domain

import com.leandrolcd.doganalyzer.data.dto.NetworkCallAnswer
import com.leandrolcd.doganalyzer.data.repositoty.LoginRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ForgotPasswordUseCase @Inject constructor(private val loginRepository: LoginRepository) {
    suspend operator fun invoke(email: String): NetworkCallAnswer<Boolean> {
        return loginRepository.onForgotPassword(email)
    }
}