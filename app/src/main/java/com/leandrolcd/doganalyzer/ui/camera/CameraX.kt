package com.leandrolcd.doganalyzer.ui.camera

import android.content.Context
import android.util.Log
import android.view.Surface.ROTATION_90
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import javax.inject.Inject
interface ICameraX{
    fun startCameraPreviewView(recognizerImage:(image:ImageProxy)->Unit): PreviewView


}
class CameraX @Inject constructor(
    private var context: Context,
    private var owner: LifecycleOwner,
    private val cameraExecutors: ExecutorService,
): ICameraX {

    override fun startCameraPreviewView(recognizerImage:(image:ImageProxy)->Unit): PreviewView {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val previewView = PreviewView(context)

        previewView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->

            val preview = Preview.Builder().build().also {
                it.targetRotation = ROTATION_90
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageCapture = ImageCapture.Builder()
                .setTargetRotation(ROTATION_90)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(ROTATION_90)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutors) { imageProxy ->
                recognizerImage(imageProxy)
            }

            val camSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                cameraProviderFuture.get().unbindAll()
                cameraProviderFuture.get().bindToLifecycle(
                    owner,
                    camSelector,
                    preview,
                    imageAnalysis,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TAG", "CameraX: $e")
            }
        }

        return previewView
    }

}
/*
*
    fun capturePhoto() =owner.lifecycleScope.launch{
        val imageCapture = imageCapture ?: return@launch

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), object :
            ImageCapture.OnImageCapturedCallback(), ImageCapture.OnImageSavedCallback {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                owner.lifecycleScope.launch {
                    saveMediaToStorage(
                        imageProxyToBitmap(image),
                        System.currentTimeMillis().toString()
                    )
                }
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                showLog("onCaptureSuccess: Uri  ${outputFileResults.savedUri}")
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                showLog("onCaptureSuccess: onError")
            }
        })


    }

    private fun showLog(showLog: String) {
        Log.d("Error", showLog)
    }

    private suspend fun imageProxyToBitmap(image: ImageProxy): Bitmap =
        withContext(owner.lifecycleScope.coroutineContext) {
            val planeProxy = image.planes[0]
            val buffer: ByteBuffer = planeProxy.buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    private suspend fun saveMediaToStorage(bitmap: Bitmap, name: String) {
        withContext(dispatcher) {
            val filename = "$name.jpg"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->

                    val contentValues = ContentValues().apply {

                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_DCIM
                        )
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    fos = imageUri?.let { with(resolver) { openOutputStream(it) } }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val image = File(imagesDir, filename).also { fos = FileOutputStream(it) }
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    mediaScanIntent.data = Uri.fromFile(image)
                    context.sendBroadcast(mediaScanIntent)
                }
            }

            fos?.use {
                val success = async(dispatcher) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                if (success.await()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }
*/
