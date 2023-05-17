package com.leandrolcd.doganalyzer.core.interfaces

import com.leandrolcd.doganalyzer.ui.camera.CameraX
import com.leandrolcd.doganalyzer.ui.camera.ICameraX
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CameraXBinds {
    @Binds
    abstract fun bindsCameraX(cameraX: CameraX):ICameraX
}