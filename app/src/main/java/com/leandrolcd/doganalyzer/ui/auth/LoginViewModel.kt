package com.leandrolcd.doganalyzer.ui.auth

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.leandrolcd.doganalyzer.domain.repository.IFireStoreRepository
import com.leandrolcd.doganalyzer.domain.repository.LoginRepository
import com.leandrolcd.doganalyzer.ui.model.LoginUser
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.states.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val fireStore: IFireStoreRepository
): ViewModel() {

    //region Properties
    var email = mutableStateOf("")
        private set

    var isDialogForgot = mutableStateOf(false)
        private set

    var userCurrent = mutableStateOf<FirebaseUser?>(null)
        private set

    var password = mutableStateOf("")
        private set

    var isButtonEnabled = mutableStateOf(false)
        private set

    var uiStatus = mutableStateOf<LoginUiState<FirebaseUser>>(LoginUiState.Loaded())
        private set


    //endregion

    init {
        Log.d("TAG", "login: viemodel on")
    }

    //region Methods
    fun onLoginChange(_email:String, pwd:String){
        email.value = _email
        password.value = pwd
        isButtonEnabled.value = enabledLogin(email = _email, password = pwd)
    }
    fun onLoginClicked(navHostController: NavHostController){
        uiStatus.value = LoginUiState.Loading()
        viewModelScope.launch {

            uiStatus.value = repository.authLogin(LoginUser(email.value,password.value))
            onNavigate(navHostController)
        }
    }

    fun onLoginWithGoogle(idToken: String, navHostController: NavHostController) {
        uiStatus.value = LoginUiState.Loading()

        val currentUser = repository.getUser()
        if (currentUser != null && currentUser.isAnonymous) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            currentUser.linkWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uiStatus.value = LoginUiState.Success(task.result?.user!!)
                    onNavigate(navHostController)

                } else {
                    val error = task.exception?.message
                    if (error != null && error.contains("credential is already")) {
                        currentUser.delete().addOnSuccessListener {
                            viewModelScope.launch {
                                val croquettes = fireStore.getCroquettes().first()
                                uiStatus.value =  repository.authLoginWithGoogle(idToken)
                                fireStore.synchronizeNow(currentUser.uid, croquettes)
                                onNavigate(navHostController)
                            }
                        }

                    }else{
                        uiStatus.value =
                            LoginUiState.Error(task.exception?.message ?: "Unknown error")

                    }


                }

            }
        }else{
            uiStatus.value = LoginUiState.Error("User is not anonymous or user is null")
        }


    }

    private fun onNavigate(navHostController: NavHostController){
        if(uiStatus.value is LoginUiState.Success){
            navHostController.popBackStack()
            navHostController.navigate(Routes.ScreenDogList.route)
            viewModelScope.launch {
                delay(5000)
                onTryAgain()
            }

        }
    }
    private fun enabledLogin(email:String, password:String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 5
    fun onTryAgain() {
        uiStatus.value = LoginUiState.Loaded()
    }
    fun onCheckedUserCurrent(navHostController: NavHostController) {
        userCurrent.value = null
        viewModelScope.launch {
            when (val user = repository.getUser()) {
                null -> {
                    userCurrent.value = repository.onSignInAnonymously()
                }
                else -> {
                    userCurrent.value = user
                }
            }

            if(userCurrent.value != null){
                dogListNavigate(navHostController)
            }

        }



    }

    private suspend fun loginNavigate(navHostController: NavHostController) {
        withContext(Dispatchers.Main){
            navHostController.popBackStack()
            navHostController.navigate(Routes.ScreenLogin.route)
        }
    }

    fun onForgotPassword(email: String, context: Context){
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            viewModelScope.launch {
                when(val resp = repository.onForgotPassword(email)){
                    is LoginUiState.Error -> {
                        uiStatus.value = LoginUiState.Error(resp.message)
                    }
                    is LoginUiState.Success -> {
                        isDialogForgot.value = false
                        Toast.makeText(context,"Password reset email sent to $email", Toast.LENGTH_LONG).show()
                    }
                    else ->{

                    }

                }

            }
        }
    }

    fun onDismissDialog(dismiss: Boolean = false) {
        isDialogForgot.value = dismiss
    }

    private suspend fun dogListNavigate(navHostController: NavHostController){
        withContext(Dispatchers.Main){
            navHostController.popBackStack()
            navHostController.navigate(Routes.ScreenDogList.route)
        }
    }
    //endregion

}