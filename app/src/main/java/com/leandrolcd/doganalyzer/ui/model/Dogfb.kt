package com.leandrolcd.doganalyzer.ui.model


import com.google.gson.annotations.SerializedName

data class Dogfb(
    @SerializedName("curiosities")
    val curiosities: String,
    @SerializedName("curiositiesEs")
    val curiositiesEs: String,
    @SerializedName("heightFemale")
    val heightFemale: String,
    @SerializedName("heightFemaleEs")
    val heightFemaleEs: String,
    @SerializedName("heightMale")
    val heightMale: String,
    @SerializedName("heightMaleEs")
    val heightMaleEs: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("lifeExpectancy")
    val lifeExpectancy: String,
    @SerializedName("mlId")
    val mlId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("race")
    val race: String,
    @SerializedName("raceEs")
    val raceEs: String,
    @SerializedName("temperament")
    val temperament: String,
    @SerializedName("temperamentEs")
    val temperamentEs: String,
    @SerializedName("weightFemale")
    val weightFemale: String,
    @SerializedName("weightFemaleEs")
    val weightFemaleEs: String,
    @SerializedName("weightMale")
    val weightMale: String,
    @SerializedName("weightMaleEs")
    val weightMaleEs: String
)