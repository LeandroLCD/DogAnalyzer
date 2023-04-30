package com.leandrolcd.doganalyzer.ui.authentication

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandrolcd.doganalyzer.domain.SignUpUseCase
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.model.LoginUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
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

    var uiStatus = mutableStateOf<UiStatus<Any>>(UiStatus.Loaded())
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
        uiStatus.value = UiStatus.Loaded()
    }

    fun onSignUpClicked() {
        uiStatus.value = UiStatus.Loading()
        viewModelScope.launch {

            uiStatus.value = signUpUseCase(LoginUser(email.value,password.value))

        }
    }

}