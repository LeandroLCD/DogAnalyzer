package com.leandrolcd.dogedexmvvm.ui.authentication

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.leandrolcd.dogedexmvvm.data.dto.NetworkCallAnswer
import com.leandrolcd.dogedexmvvm.domain.AuthLoginUseCase
import com.leandrolcd.dogedexmvvm.domain.AuthLoginWithGoogleUseCase
import com.leandrolcd.dogedexmvvm.domain.ForgotPasswordUseCase
import com.leandrolcd.dogedexmvvm.ui.model.LoginUser
import com.leandrolcd.dogedexmvvm.ui.model.Routes
import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginComposeViewModel @Inject constructor(
    private val authLoginWithGoogleUseCase: AuthLoginWithGoogleUseCase,
    private val authLoginUseCase: AuthLoginUseCase,
    private val forgotUseCase: ForgotPasswordUseCase
): ViewModel() {

    //region Properties
    var email = mutableStateOf<String>("")
        private set

    var isDialogForgot = mutableStateOf(false)
        private set

    var userCurrent = mutableStateOf<FirebaseUser?>(null)
        private set

    var password = mutableStateOf("")
        private set

    var isButtonEnabled = mutableStateOf(false)
        private set

    var uiStatus = mutableStateOf<UiStatus<Any>>(UiStatus.Loaded())
        private set
    //endregion

    //region Methods
    fun onLoginChange(_email:String, pwd:String){
         email.value = _email
        password.value = pwd
        isButtonEnabled.value = enabledLogin(email = _email, password = pwd)
    }
    fun onLoginClicked(navHostController: NavHostController){
        uiStatus.value = UiStatus.Loading()
        viewModelScope.launch {

            uiStatus.value = authLoginUseCase.invoke(LoginUser(email.value,password.value))
            onNavigate(navHostController)
        }
    }
    fun onLoginWithGoogle(idToken: String, navHostController: NavHostController) {
        uiStatus.value = UiStatus.Loading()
        viewModelScope.launch {
            uiStatus.value = authLoginWithGoogleUseCase.invoke(idToken)
            onNavigate(navHostController)

        }
    }
    private fun onNavigate(navHostController: NavHostController){
    if(uiStatus.value is UiStatus.Success){
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
        uiStatus.value = UiStatus.Loaded()
    }
    fun onCheckedUserCurrent(navHostController: NavHostController) {
        authLoginUseCase.getUser()?.let { user ->
            userCurrent.value = user
        }
        if(userCurrent.value == null){
            navHostController.popBackStack()
            navHostController.navigate(Routes.ScreenLogin.route)
        }else{

            navHostController.popBackStack()
            navHostController.navigate(Routes.ScreenDogList.route)
        }
    }
    fun onForgotPassword(email: String, context: Context){
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            viewModelScope.launch {
                val resp = forgotUseCase(email)
                Log.d("TAG", "onForgotPassword: $resp")
                when(resp){
                    is NetworkCallAnswer.Error -> {
                        uiStatus.value = UiStatus.Error(resp.message)

                    }
                    is NetworkCallAnswer.Success -> {
                        isDialogForgot.value = false
                        Toast.makeText(context,"Password reset email sent to $email", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }


    }
    fun onDismissDialog(dismiss: Boolean = false) {
        isDialogForgot.value = dismiss
    }

    //endregion

}