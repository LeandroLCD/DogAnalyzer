package com.leandrolcd.doganalyzer.core.interfaces

import androidx.annotation.Keep
import com.leandrolcd.doganalyzer.data.repository.CameraRepository
import com.leandrolcd.doganalyzer.data.repository.ICameraRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Keep
@Module
@InstallIn(SingletonComponent::class)
abstract class CameraXBinds {
    @Binds
    abstract fun bindsCameraX(cameraX: CameraRepository):ICameraRepository
}