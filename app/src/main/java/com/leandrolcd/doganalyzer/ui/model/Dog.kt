package com.leandrolcd.doganalyzer.ui.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude

import kotlinx.parcelize.Parcelize

@Parcelize
data class Dog(
    var mlId: String = "",
    val croquettes:Int = 0,
    val curiosities: String = "",
    val curiositiesEs: String = "",
    val heightFemale: String = "",
    val heightMale: String = "",
    val heightFemaleEs: String = "",
    val heightMaleEs: String = "",
    val imageUrl: String = "",
    val index: Int,
    val lifeExpectancy: String = "",
    val name: String = "",
    val race: String = "",
    val raceEs: String = "",
    val temperament: String = "",
    val temperamentEs: String = "",
    val weightFemale: String = "",
    val weightMale: String = "",
    val weightFemaleEs: String = "",
    val weightMaleEs: String = "",
    @Exclude
    var inCollection:Boolean = false,
    @Exclude
    var confidence:Float = 0f

) : Parcelable, Comparable<Dog>{
    override fun compareTo(other: Dog): Int = if (this.mlId != other.mlId) 1 else -1


}
