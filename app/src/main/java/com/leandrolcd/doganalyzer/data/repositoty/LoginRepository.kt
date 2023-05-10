package com.leandrolcd.doganalyzer.data.repositoty

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.leandrolcd.doganalyzer.data.dto.NetworkCallAnswer
import com.leandrolcd.doganalyzer.data.network.LoginService
import com.leandrolcd.doganalyzer.ui.model.LoginUser
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@ExperimentalCoroutinesApi
class LoginRepository @Inject constructor(private val loginService: LoginService) {
    suspend fun onForgotPassword(email: String): NetworkCallAnswer<Boolean> =
        suspendCancellableCoroutine { continuation ->
            loginService.forgotPassword(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(
                        NetworkCallAnswer.Success(true), null)
                    } else {
                        continuation.resume(
                            NetworkCallAnswer.Error(
                                task.exception?.message!!
                            ), null
                        )
                    }
                }

        }

    suspend fun authLogin(user: LoginUser): UiStatus<Any> =

        suspendCancellableCoroutine { continuation ->
            val userCurrent = getUser()
                loginService.signInWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener { task ->
                        userCurrent?.delete()
                        if (task.isSuccessful) {

                            val userD = task.result?.user
                            continuation.resume(UiStatus.Success(userD!!), null)
                        } else {
                            continuation.resume(
                                UiStatus.Error(
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

    suspend fun authLoginWithGoogle(idToken: String): UiStatus<Any> =
        suspendCancellableCoroutine { continuation ->
            loginService.signInWithGoogle(idToken)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userD = task.result?.user
                        continuation.resume(UiStatus.Success(userD!!), null)
                    } else {
                        Log.d("authLogin", "Error al iniciar sesión con Google")
                        continuation.resume(
                            UiStatus.Error(
                                task.exception?.message ?: "Error desconocido"
                            ), null
                        )
                    }
                }
                .addOnCanceledListener {
                    continuation.cancel()
                }
        }

    suspend fun createUserWithEmailAndPassword(user: LoginUser): UiStatus<Any> =
        suspendCancellableCoroutine { continuation ->
                loginService.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userD = task.result?.user
                            continuation.resume(UiStatus.Success(userD!!), null)
                        } else {
                            continuation.resume(
                                UiStatus.Error(
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