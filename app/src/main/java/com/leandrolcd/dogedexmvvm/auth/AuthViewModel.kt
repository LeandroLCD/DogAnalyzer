package com.leandrolcd.dogedexmvvm.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandrolcd.dogedexmvvm.auth.model.User
import com.leandrolcd.dogedexmvvm.dogslist.UiStatus
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    //region fields
    private val repository = AuthRepository()
    private val _user = MutableLiveData<User>()
    private val _status = MutableLiveData<UiStatus<User>>()
    //endregion

    //region Properties
    val uiState: LiveData<UiStatus<User>> = _status
    val user: LiveData<User> = _user
    //endregion

    //region Methods
    fun signUp(email: String, password: String, passwordConfirmation: String) {
        viewModelScope.launch {
            _status.value = UiStatus.Loading()
            handleResponseStatus(repository.signUp(
                email,
                password,
                passwordConfirmation))
        }
    }

    private fun handleResponseStatus(uiStatus: UiStatus<User>) {
        if (uiStatus is UiStatus.Success) {
            _user.value = uiStatus.data!!
        }
        _status.value = uiStatus
    }
//endregion
}