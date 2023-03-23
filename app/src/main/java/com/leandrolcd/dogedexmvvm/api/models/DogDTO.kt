package com.leandrolcd.dogedexmvvm.api.models


import com.google.gson.annotations.SerializedName

data class DogDTO(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("dog_type")
    val dogType: String,
    @SerializedName("height_female")
    val heightFemale: String,
    @SerializedName("height_male")
    val heightMale: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("life_expectancy")
    val lifeExpectancy: String,
    @SerializedName("ml_id")
    val mlId: String,
    @SerializedName("name_en")
    val nameEn: String,
    @SerializedName("name_es")
    val nameEs: String,
    @SerializedName("temperament")
    val temperament: String,
    @SerializedName("temperament_en")
    val temperamentEn: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("weight_female")
    val weightFemale: String,
    @SerializedName("weight_male")
    val weightMale: String
)
