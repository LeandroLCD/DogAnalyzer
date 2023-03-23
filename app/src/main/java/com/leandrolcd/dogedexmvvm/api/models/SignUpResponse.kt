package com.leandrolcd.dogedexmvvm.api.models

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("data")
    val data: UserResponse,
    @SerializedName("is_success")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String
)
