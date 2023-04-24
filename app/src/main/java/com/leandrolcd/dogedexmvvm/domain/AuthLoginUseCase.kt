package com.leandrolcd.dogedexmvvm.domain

import com.google.firebase.auth.FirebaseUser
import com.leandrolcd.dogedexmvvm.data.repositoty.LoginRepository
import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import com.leandrolcd.dogedexmvvm.ui.model.LoginUser
import javax.inject.Inject

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