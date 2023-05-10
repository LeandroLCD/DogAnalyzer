package com.leandrolcd.doganalyzer.ui.authentication

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
import com.leandrolcd.doganalyzer.data.dto.NetworkCallAnswer
import com.leandrolcd.doganalyzer.data.repositoty.IFireStoreRepository
import com.leandrolcd.doganalyzer.data.repositoty.LoginRepository
import com.leandrolcd.doganalyzer.ui.model.LoginUser
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginComposeViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val fireStore: IFireStoreRepository
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

            uiStatus.value = repository.authLogin(LoginUser(email.value,password.value))
            onNavigate(navHostController)
        }
    }
    fun onLoginWithGoogle(idToken: String, navHostController: NavHostController) {
        uiStatus.value = UiStatus.Loading()

            val currentUser = repository.getUser()
            if (currentUser != null && currentUser.isAnonymous) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                currentUser.linkWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        uiStatus.value = UiStatus.Success(Unit)
                        onNavigate(navHostController)

                    } else {
                            val error = task.exception?.message
                            if (error != null && error.contains("credential is already")) {
                                currentUser.delete().addOnSuccessListener {
                                    viewModelScope.launch {
                                        uiStatus.value =  repository.authLoginWithGoogle(idToken)
                                        fireStore.synchronizeNow()
                                        onNavigate(navHostController)
                                    }
                                }

                            }else{
                                uiStatus.value =
                                    UiStatus.Error(task.exception?.message ?: "Unknown error")

                            }


                        }

                }
            }else{
                uiStatus.value = UiStatus.Error("User is not anonymous or user is null")
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
        repository.getUser()?.let { user ->
            userCurrent.value = user
        }
        if(userCurrent.value == null){
            viewModelScope.launch {
                userCurrent.value = repository.onSignInAnonymously()
            }

        }

            navHostController.popBackStack()
            navHostController.navigate(Routes.ScreenDogList.route)

    }
    fun onForgotPassword(email: String, context: Context){
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            viewModelScope.launch {
                val resp = repository.onForgotPassword(email)
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