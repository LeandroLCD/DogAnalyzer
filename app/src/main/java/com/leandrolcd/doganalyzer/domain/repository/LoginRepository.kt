package com.leandrolcd.doganalyzer.domain.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.auth.FirebaseUser
import com.leandrolcd.doganalyzer.data.services.LoginService
import com.leandrolcd.doganalyzer.ui.model.LoginUser
import com.leandrolcd.doganalyzer.ui.states.LoginUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@Keep
@ExperimentalCoroutinesApi
class LoginRepository @Inject constructor(private val loginService: LoginService,
                private val dispatcher: CoroutineDispatcher): BaseRepository(dispatcher) {
    suspend fun onForgotPassword(email: String): LoginUiState<Boolean> =
        suspendCancellableCoroutine { continuation ->
            loginService.forgotPassword(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(
                            LoginUiState.Success(true), null)
                    } else {
                        continuation.resume(
                            LoginUiState.Error(task?.exception?.message!!), null
                        )
                    }
                }

        }


    @SuppressLint("SuspiciousIndentation")
    suspend fun authLogin(user: LoginUser): LoginUiState<FirebaseUser> =

        suspendCancellableCoroutine { continuation ->
            val userCurrent = getUser()
            loginService.signInWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    userCurrent?.delete()
                    if (task.isSuccessful) {

                        val userD = task.result?.user
                        continuation.resume(LoginUiState.Success(userD!!), null)
                    } else {
                        continuation.resume(
                            LoginUiState.Error(
                                task.exception?.message ?: "Error desconocido"
                            ), null
                        )
                    }
                }

        }

    fun getUser(): FirebaseUser? {
        return loginService.getUser()
    }

    fun logout(){
        loginService.signOut()

    }

    suspend fun authLoginWithGoogle(idToken: String): LoginUiState<FirebaseUser> =
        suspendCancellableCoroutine { continuation ->
            loginService.signInWithGoogle(idToken)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userD = task.result?.user
                        continuation.resume(LoginUiState.Success(userD!!), null)
                    } else {
                        Log.d("authLogin", "Error al iniciar sesión con Google")
                        continuation.resume(
                            LoginUiState.Error(
                                task.exception?.message ?: "Error desconocido"
                            ), null
                        )
                    }
                }
                .addOnCanceledListener {
                    continuation.cancel()
                }
        }

    suspend fun createUserWithEmailAndPassword(user: LoginUser): LoginUiState<FirebaseUser> =
        suspendCancellableCoroutine { continuation ->
            loginService.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userD = task.result?.user
                        continuation.resume(LoginUiState.Success(userD!!), null)
                    } else {
                        continuation.resume(
                            LoginUiState.Error(
                                task.exception!!.message!!
                            ), null
                        )
                    }
                }
        }

    suspend fun onSignInAnonymously(): FirebaseUser? =
        suspendCancellableCoroutine { continuation ->
            loginService.onSignInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userD = task.result?.user
                        continuation.resume(userD!!, null)
                    } else {

                        continuation.resume(
                            null, null
                        )
                    }
                }
        }
}