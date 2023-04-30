package com.leandrolcd.doganalyzer.data.dto

import com.google.firebase.firestore.PropertyName


data class DogDTO(
    @PropertyName("mlId")
    var mlId: String = "",
    @PropertyName("imageUrl")
    val imageUrl: String,
    @PropertyName("name")
    val name: String ,
    @PropertyName("race")
    val race: String ,
    @PropertyName("raceEs")
    val raceEs: String ,
    @PropertyName("temperament")
    val temperament: String,
    @PropertyName("temperamentEs")
    val temperamentEs: String,
    @PropertyName("lifeExpectancy")
    val lifeExpectancy: String,
    @PropertyName("weightMale")
    val weightMale: String,
    @PropertyName("weightFemale")
    val weightFemale: String,
    @PropertyName("heightMale")
    val heightMale: String,
    @PropertyName("heightFemale")
    val heightFemale: String,
    @PropertyName("curiosities")
    val curiosities: String,
    @PropertyName("weightMaleEs")
    val weightMaleEs: String,
    @PropertyName("weightFemaleEs")
    val weightFemaleEs: String,
    @PropertyName("heightMaleEs")
    val heightMaleEs: String,
    @PropertyName("heightFemaleEs")
    val heightFemaleEs: String,
    @PropertyName("curiositiesEs")
    val curiositiesEs: String,
    @PropertyName("index")
    val index: Int,
){
    constructor() : this("", "","", "", "", "", "","","","",
        "","","", "", "", "", "", "", 0 )
}
