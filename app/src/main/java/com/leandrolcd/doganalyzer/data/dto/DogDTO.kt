package com.leandrolcd.doganalyzer.data.dto

import com.google.firebase.firestore.PropertyName

data class DogDTO(


    @get:PropertyName("mlId") @set:PropertyName("mlId")
    var mlId: String = "",

    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl")
    var imageUrl: String,

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String ,

    @get:PropertyName("race") @set:PropertyName("race")
    var race: String ,

    @get:PropertyName("raceEs") @set:PropertyName("raceEs")
    var raceEs: String ,

    @get:PropertyName("temperament") @set:PropertyName("temperament")
    var temperament: String,

    @get:PropertyName("temperamentEs") @set:PropertyName("temperamentEs")
    var temperamentEs: String,

    @get:PropertyName("lifeExpectancy") @set:PropertyName("lifeExpectancy")
    var lifeExpectancy: String,

    @get:PropertyName("weightMale") @set:PropertyName("weightMale")
    var weightMale: String,

    @get:PropertyName("weightFemale") @set:PropertyName("weightFemale")
    var weightFemale: String,

    @get:PropertyName("heightMale") @set:PropertyName("heightMale")
    var heightMale: String,

    @get:PropertyName("heightFemale") @set:PropertyName("heightFemale")
    var heightFemale: String,

    @get:PropertyName("curiosities") @set:PropertyName("curiosities")
    var curiosities: String,

    @get:PropertyName("weightMaleEs") @set:PropertyName("weightMaleEs")
    var weightMaleEs: String,

    @get:PropertyName("weightFemaleEs") @set:PropertyName("weightFemaleEs")
    var weightFemaleEs: String,

    @get:PropertyName("heightMaleEs") @set:PropertyName("heightMaleEs")
    var heightMaleEs: String,

    @get:PropertyName("heightFemaleEs") @set:PropertyName("heightFemaleEs")
    var heightFemaleEs: String,

    @get:PropertyName("curiositiesEs") @set:PropertyName("curiositiesEs")
    var curiositiesEs: String,

    @get:PropertyName("index") @set:PropertyName("index")
    var index: Int,
){
    constructor() : this("", "","", "", "", "", "","","","",
        "","","", "", "", "", "", "", 0 )
}
