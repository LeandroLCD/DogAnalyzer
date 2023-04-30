package com.leandrolcd.doganalyzer.data.repositoty

import androidx.camera.core.ImageProxy
import com.leandrolcd.doganalyzer.core.camera.Classifier
import com.leandrolcd.doganalyzer.rotate
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
 interface IClassifierRepository{
     suspend fun recognizeImage(imageProxy: ImageProxy): List<DogRecognition>
 }
class ClassifierRepository @Inject constructor (private val classifier: Classifier): IClassifierRepository {

    override suspend fun recognizeImage(imageProxy: ImageProxy): List<DogRecognition> {
        return withContext(Dispatchers.IO){
            imageProxy.imageInfo.rotationDegrees
            val bitmap = imageProxy.toBitmap()
            classifier.recognizeImage(bitmap.rotate()).take(5)
        }

    }
}