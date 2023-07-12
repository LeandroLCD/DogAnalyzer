package com.leandrolcd.doganalyzer.core.app

import androidx.annotation.Keep
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Keep
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    fun dispatcherIoProvider(): CoroutineDispatcher = Dispatchers.IO
}