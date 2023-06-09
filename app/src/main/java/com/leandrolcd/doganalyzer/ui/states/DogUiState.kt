package com.leandrolcd.doganalyzer.ui.states

sealed class DogUiState<T> {

    class Loaded<T>: DogUiState<T>()

    class Loading<T>: DogUiState<T>()

    data class Success<T>(val data: T) : DogUiState<T>()

    data class Error<T>(val message: String) : DogUiState<T>()

}