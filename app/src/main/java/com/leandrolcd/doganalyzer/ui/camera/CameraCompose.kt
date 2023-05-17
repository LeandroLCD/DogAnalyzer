package com.leandrolcd.doganalyzer.ui.camera

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Camera
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.leandrolcd.doganalyzer.ui.camera.utils.Commons.REQUIRED_PERMISSIONS
import com.leandrolcd.doganalyzer.ui.doglist.DogListViewModel
import com.leandrolcd.doganalyzer.ui.ui.theme.primaryColor
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@Composable
fun CameraCompose(
    viewModel: DogListViewModel = hiltViewModel()
) {
    //region Permission Cam
    val context = LocalContext.current
    var hasCamPermission by remember {
        mutableStateOf(
            REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(context, it) ==
                        PackageManager.PERMISSION_GRANTED
            })
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { granted ->
            hasCamPermission = granted.size == 2
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }
    //endregion
    Surface(modifier = Modifier.fillMaxSize()) {
        if (hasCamPermission) {
            Column(Modifier.fillMaxSize()) {
                AndroidView(modifier = Modifier.fillMaxSize(),
                    factory = {
                        viewModel.cameraX.value.startCameraPreviewView {
                            viewModel.recognizerImage(it)
                        }
                    }
                )
            }


        }
    }

}

@Composable
fun ButtonCamera(enabled: Boolean = true, onCaptureClick: () -> Unit) {
    FloatingActionButton(
        onClick = { if(enabled){
            onCaptureClick()
        } },
        backgroundColor = Color.Transparent,
        modifier = Modifier.background(
            Color.Transparent
        )
    ) {
        if (enabled) {
            Icon(imageVector = Icons.Sharp.Camera,
                contentDescription = "Capture dog",
                tint = primaryColor,
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp))
        } else {
            Icon(
                imageVector = Icons.Sharp.Camera,
                contentDescription = "Capture dog",
                tint = Color.Gray,
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
            )
        }
    }


}
