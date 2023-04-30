package com.leandrolcd.doganalyzer.domain

import com.google.firebase.auth.FirebaseUser
import com.leandrolcd.doganalyzer.data.repositoty.LoginRepository
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.model.LoginUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AuthLoginUseCase @Inject constructor(
                       private val repository: LoginRepository
) {
    suspend operator fun invoke(user: LoginUser): UiStatus<Any> {
        return repository.authLogin(user)
    }

    fun getUser():FirebaseUser?{
        return repository.getUser()
    }

    fun logout(){
        repository.logout()
    }

}