package com.leandrolcd.dogedexmvvm.api.models


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("dogs")
    val dogs: List<DogDTO>
)