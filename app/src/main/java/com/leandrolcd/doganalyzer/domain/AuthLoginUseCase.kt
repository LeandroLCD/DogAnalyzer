package com.leandrolcd.doganalyzer.domain

import com.google.firebase.auth.FirebaseUser
import com.leandrolcd.doganalyzer.data.repositoty.LoginRepository
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.model.LoginUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

interface IAuthLoginUseCase{
    suspend operator fun invoke(user: LoginUser): UiStatus<Any>

    fun getUser():FirebaseUser?

    fun logout()
     suspend fun onSignInAnonymously(): FirebaseUser?


}
@ExperimentalCoroutinesApi
class AuthLoginUseCase @Inject constructor(
                       private val repository: LoginRepository
): IAuthLoginUseCase {
    override suspend operator fun invoke(user: LoginUser): UiStatus<Any> {
        return repository.authLogin(user)
    }

    override fun getUser():FirebaseUser?{
        return repository.getUser()
    }

    override fun logout(){
        repository.logout()
    }

    override suspend fun onSignInAnonymously(): FirebaseUser? {
        return repository.onSignInAnonymously()
    }

}