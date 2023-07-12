package com.leandrolcd.doganalyzer.domain.repository

import androidx.annotation.Keep
import androidx.camera.core.ImageProxy
import com.leandrolcd.doganalyzer.core.camera.Classifier
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.utility.rotate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Keep
interface IClassifierRepository{
    suspend fun recognizeImage(imageProxy: ImageProxy): List<DogRecognition>
}
@Keep
class ClassifierRepository @Inject constructor (private val classifier: Classifier):
    IClassifierRepository {

    override suspend fun recognizeImage(imageProxy: ImageProxy): List<DogRecognition> {
        return withContext(Dispatchers.IO){
            val bitmap = imageProxy.toBitmap()
            classifier.recognizeImage(bitmap.rotate()).take(5)
        }

    }
}