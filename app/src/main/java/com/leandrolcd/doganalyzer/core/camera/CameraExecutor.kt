package com.leandrolcd.doganalyzer.core.camera

import androidx.annotation.Keep
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Keep
@Module
@InstallIn(SingletonComponent::class)
object CameraExecutor {
    @Provides
    fun providesCameraExecutor(): ExecutorService = Executors.newSingleThreadExecutor()
}