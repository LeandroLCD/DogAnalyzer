package com.leandrolcd.doganalyzer.utility

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.os.Build
import androidx.compose.ui.graphics.Color
import com.leandrolcd.doganalyzer.data.dto.DogDTO
import com.leandrolcd.doganalyzer.ui.model.Dog
import java.util.*


fun Color.opacity(alpha:Float): Color {

    return Color(this.red, this.green, this.blue, alpha)
}

fun DogDTO.toDog(): Dog {
    return this.run {
        Dog(
            index = this.index,
            mlId = this.mlId,
            imageUrl = this.imageUrl,
            lifeExpectancy = this.lifeExpectancy,
            name = this.name,
            temperament = this.temperament,
            temperamentEs = this.temperamentEs,
            race = this.race,
            raceEs = this.raceEs,
            weightMale = this.weightMale,
            weightFemale = this.weightFemale,
            heightMale = this.heightMale,
            heightFemale = this.heightFemale,
            curiosities = this.curiosities,
            weightMaleEs = this.weightMaleEs,
            weightFemaleEs = this.weightFemaleEs,
            heightMaleEs = this.heightMaleEs,
            heightFemaleEs = this.heightFemaleEs,
            curiositiesEs = this.curiositiesEs,
            croquettes = onCroquettes(this.race)
        )

    }
}

fun List<DogDTO>.toDogList(): List<Dog> {
    return this.map {
        Dog(
            index = it.index,
            imageUrl = it.imageUrl,
            lifeExpectancy = it.lifeExpectancy,
            name = it.name,
            temperament = it.temperament,
            temperamentEs = it.temperamentEs,
            race = it.race,
            raceEs = it.raceEs,
            weightMale = it.weightMale,
            weightFemale = it.weightFemale,
            heightMale = it.heightMale,
            heightFemale = it.heightFemale,
            curiosities = it.curiosities,
            curiositiesEs = it.curiositiesEs,
            weightMaleEs = it.weightMaleEs,
            weightFemaleEs = it.weightFemaleEs,
            heightMaleEs = it.heightMaleEs,
            heightFemaleEs = it.heightFemaleEs,
            mlId = it.mlId,
            croquettes = onCroquettes(it.race)
        )

    }
}

fun onCroquettes(race: String): Int {
    return when(race){
        "Small" -> { 24 }
        "Median" -> { 48 }
        "Big" -> { 72 }
        "Giant" -> { 98 }
        else -> { 100 }
    }
}

fun Bitmap.rotate(angle:Float=90f): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun String.isSpanish():Boolean = this == "es"

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun Context.getRecognitionClick(): Int {
    return getSharedPreferences(COUNTER_RECOGNITION_REFS, Context.MODE_PRIVATE).getInt(
        COUNTER_RECOGNITION_REFS, 0)
}
fun Context.setRecognitionClick():Int {
    val number = this.getRecognitionClick() + 1
    getSharedPreferences(COUNTER_RECOGNITION_REFS, Context.MODE_PRIVATE).also {
        it.edit().putInt(COUNTER_RECOGNITION_REFS, number).apply()
    }
    return number
}
fun Context.getDetailClick(): Int {
    return getSharedPreferences(COUNTER_DETAIL_PREFS, Context.MODE_PRIVATE).getInt(
        COUNTER_DETAIL_PREFS, 0)
}

fun Context.setDetailClick():Int {
    val number = this.getDetailClick() + 1
    getSharedPreferences(COUNTER_DETAIL_PREFS, Context.MODE_PRIVATE).also {
        it.edit().putInt(COUNTER_DETAIL_PREFS, number).apply()
    }
    return number
}

fun Context.getAdRewardClick(): Int {
    return getSharedPreferences(COUNTER_AD_REWARD, Context.MODE_PRIVATE).getInt(
        COUNTER_AD_REWARD, 0)
}
fun Context.setZeroAdRewardClick():Int {
    val number:Int = this.getAdRewardClick()
    getSharedPreferences(COUNTER_AD_REWARD, Context.MODE_PRIVATE).also {
        it.edit().putInt(COUNTER_AD_REWARD, -number).apply()
    }
    return number
}
fun Context.setAdRewardClick(click:Int):Int {
    val number = this.getAdRewardClick() + click
    getSharedPreferences(COUNTER_AD_REWARD, Context.MODE_PRIVATE).also {
        it.edit().putInt(COUNTER_AD_REWARD, number).apply()
    }
    return number
}

fun Context.getDateAdReward(): Int {
    return getSharedPreferences(DATE_AD_REWARD, Context.MODE_PRIVATE).getInt(
        DATE_AD_REWARD, 0)
}

fun Context.setDateAdReward(toDay:Int) {
    getSharedPreferences(DATE_AD_REWARD, Context.MODE_PRIVATE).also {
        it.edit().putInt(DATE_AD_REWARD, toDay).apply()
    }
}

fun Calendar.toDay():Int = this.get(Calendar.DAY_OF_YEAR)

fun Context.getPackageInfoCompat(flags: Int = 0): PackageInfo {
    val packageManager = packageManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, flags)
    }
}

