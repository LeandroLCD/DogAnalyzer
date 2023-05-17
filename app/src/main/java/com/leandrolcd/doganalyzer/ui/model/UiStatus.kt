package com.leandrolcd.doganalyzer.ui.model

sealed class UiStatus<T>() {

    class Loading<T>() : UiStatus<T>()
    class Loaded<T>() : UiStatus<T>()
   data class Success<T>(val data: T) : UiStatus<T>()
   data class Error<T>(val message: String) : UiStatus<T>()

}