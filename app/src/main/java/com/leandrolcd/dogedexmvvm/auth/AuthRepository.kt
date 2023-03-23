package com.leandrolcd.dogedexmvvm.auth

import android.util.Log
import com.leandrolcd.dogedexmvvm.api.DogsApi
import com.leandrolcd.dogedexmvvm.api.makeNetworkCall
import com.leandrolcd.dogedexmvvm.api.models.SignUpDTO
import com.leandrolcd.dogedexmvvm.api.models.UserDTO
import com.leandrolcd.dogedexmvvm.auth.model.User
import com.leandrolcd.dogedexmvvm.dogslist.UiStatus

class AuthRepository {

    suspend fun signUp(
        email: String,
        password: String,
        passwordConfirmation: String
    ): UiStatus<User> {
        return makeNetworkCall {

            val user = SignUpDTO(
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation
            )
            Log.i("SignUpDTO", "$email $password $passwordConfirmation")
            val response = DogsApi.retrofitService.signUp(signUpDTO = user)

            if (!response.isSuccess) {
                throw Exception(response.message)
            }

            userDTOmaper(response.data.user)
        }

    }


}

private fun userDTOmaper(data: UserDTO): User {
    return User(
        email = data.email,
        id = data.id,
        authenticationToken = data.authenticationToken
    )
}
