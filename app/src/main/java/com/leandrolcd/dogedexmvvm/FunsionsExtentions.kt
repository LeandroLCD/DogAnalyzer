package com.leandrolcd.dogedexmvvm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.ConnectivityManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.leandrolcd.dogedexmvvm.data.dto.DogDTO
import com.leandrolcd.dogedexmvvm.ui.model.Dog
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(
                cameraProvider.get()
            )
        }, ContextCompat.getMainExecutor(this))
    }
}

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
            race = this.race,
            raceEs = this.raceEs,
            weightMale = this.weightMale,
            weightFemale = this.weightFemale,
            heightMale = this.heightMale,
            heightFemale = this.heightFemale,
            curiosities = this.curiosities,
            weightMaleEs = this.weightMaleEs,
            weightFemaleEs = this.weightFemale,
            heightMaleEs = this.heightMaleEs,
            heightFemaleEs = this.heightFemaleEs,
            curiositiesEs = this.curiositiesEs,
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
            race = it.race,
            raceEs = it.raceEs,
            weightMale = it.weightMale,
            weightFemale = it.weightFemale,
            heightMale = it.heightMale,
            heightFemale = it.heightFemale,
            curiosities = it.curiosities,
            weightMaleEs = it.weightMaleEs,
            weightFemaleEs = it.weightFemaleEs,
            heightMaleEs = it.heightMaleEs,
            heightFemaleEs = it.heightFemaleEs,
            curiositiesEs = it.curiositiesEs,
            mlId = it.mlId
        )

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