package com.leandrolcd.dogedexmvvm.api


import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

suspend fun <T> makeNetworkCall(
    call: suspend () -> T
): UiStatus<T> {
    return withContext(Dispatchers.IO) {
        try {
            UiStatus.Success(call())
        } catch (e: UnknownHostException) {
            UiStatus.Error(message = "El dispositivo no puede conectar con el server, revise la conexión a internet.")

        } catch (e: Exception) {
          val msj = when(e.message){
                "sign_up_error" -> "Credenciales invalidas"
                "sign_in_error" -> "Error en el Login in"
                "user_not_found" -> "user_not_found"
                    "user_already_exists" -> "El usuario ya existe"
                else -> "Error al descargar datos.\n Detalles: ${e.message.toString()}"
            }

             UiStatus.Error(message = msj)
        }
    }
}