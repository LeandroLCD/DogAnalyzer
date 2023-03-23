package com.leandrolcd.dogedexmvvm.auth.model


data class User(
    val id: Long,
    val email: String,
    val authenticationToken:String)

