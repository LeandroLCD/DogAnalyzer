package com.leandrolcd.doganalyzer.domain.model

sealed class ResultType<T>{
    class Success<T>(val data: T) : ResultType<T>()

    class Error<T>(val message: String) : ResultType<T>()
}

