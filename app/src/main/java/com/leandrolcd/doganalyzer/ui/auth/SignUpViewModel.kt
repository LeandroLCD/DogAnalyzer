package com.leandrolcd.doganalyzer.ui.auth

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.leandrolcd.doganalyzer.domain.repository.LoginRepository
import com.leandrolcd.doganalyzer.ui.states.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {


    //region Properties
    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var passwordConfirmation = mutableStateOf("")
        private set

    var isButtonEnabled = mutableStateOf(false)
        private set

    var uiStatus = mutableStateOf<LoginUiState<Any>>(LoginUiState.Loaded())
        private set

    //endregion
    fun onSignUpChanged(mail: String, pwd: String, pswConf: String) {
        email.value = mail
        password.value = pwd
        passwordConfirmation.value = pswConf
        isButtonEnabled.value =
            enabledButton(email = mail, password = pwd, passwordConfirmation = pswConf)
    }

    private fun enabledButton(email: String, password: String, passwordConfirmation: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() && onValuePassword(password, passwordConfirmation)

    private fun onValuePassword(password: String, passwordConfirmation: String): Boolean {
        val pattern = buildString {
            append("^[a-zA-Z0-9]{6,}$")
        }.toRegex()
        val isEqual = passwordConfirmation == password
        return pattern.matches(passwordConfirmation) && pattern.matches(password) && isEqual
    }

    fun onTryAgain() {
        uiStatus.value = LoginUiState.Loaded()
    }

    fun onSignUpClicked() {
        val currentUser = loginRepository.getUser()
        uiStatus.value = LoginUiState.Loading()
        viewModelScope.launch {
            if (currentUser != null && currentUser.isAnonymous) {
                val credential = EmailAuthProvider.getCredential(email.value, password.value)
                currentUser.linkWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        uiStatus.value = LoginUiState.Success(Unit)
                    } else {
                        uiStatus.value = LoginUiState.Error(task.exception?.message ?: "Unknown error")
                    }
                }
            } else {
                uiStatus.value = LoginUiState.Error("User is not anonymous or user is null")
            }

        }
    }

}