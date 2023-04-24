package com.leandrolcd.dogedexmvvm.data.repositoty

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.leandrolcd.dogedexmvvm.data.network.LoginService
import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import com.leandrolcd.dogedexmvvm.ui.model.LoginUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@ExperimentalCoroutinesApi
class LoginRepository @Inject constructor(private val loginService: LoginService) {

    suspend fun authLogin(user: LoginUser): UiStatus<Any> =
        suspendCancellableCoroutine { continuation ->
                loginService.signInWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener { task ->
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

    suspend fun authLoginWithGoogle(idToken: String): UiStatus<Any> =
        suspendCancellableCoroutine { continuation ->
            loginService.signInWithGoogle(idToken)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(
                            "createUser",
                            "authLoginWithGoogle: El usuario ha registrado con exito"
                        )
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
                            Log.d("authLogin", "authLogin: El usuario ha iniciado sesión con éxito")
                            val userD = task.result?.user
                            continuation.resume(UiStatus.Success(userD!!), null)
                        } else {
                            Log.d("authLogin", "Error al iniciar sesión")
                            continuation.resume(
                                UiStatus.Error(
                                    task.exception?.message ?: "Error desconocido"
                                ), null
                            )
                        }
                    }
        }
}