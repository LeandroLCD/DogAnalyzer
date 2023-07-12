package com.leandrolcd.doganalyzer.ui.states

sealed class LoginUiState<T> {

     class Loaded<T> : LoginUiState<T>()

     class Loading<T> : LoginUiState<T>()

    data class Success<T>(val data: T) : LoginUiState<T>()

    data class Error<T>(val message: String) : LoginUiState<T>()

}
