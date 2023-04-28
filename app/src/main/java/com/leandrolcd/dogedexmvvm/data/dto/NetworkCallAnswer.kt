package com.leandrolcd.dogedexmvvm.data.dto

sealed class NetworkCallAnswer<T>(){

    class Success<T>(val data: T) : NetworkCallAnswer<T>()

    class Error<T>(val message: String) : NetworkCallAnswer<T>()

}
