package com.leandrolcd.dogedexmvvm.api.models

import com.google.gson.annotations.SerializedName


data class UserDTO (
    val id: Long,
    val email: String,
    @SerializedName("authentication_token")
    val authenticationToken:String)
