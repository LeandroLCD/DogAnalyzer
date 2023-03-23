package com.leandrolcd.dogedexmvvm.api.models


import com.google.gson.annotations.SerializedName

data class DogResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("is_success")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String
)