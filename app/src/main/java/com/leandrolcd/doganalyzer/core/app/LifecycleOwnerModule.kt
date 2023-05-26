package com.leandrolcd.doganalyzer.core.app

import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Keep
@Module
@InstallIn(SingletonComponent::class)
object LifecycleOwnerModule {

    @Provides
    fun provideLifecycleOwner(): LifecycleOwner {
        return ProcessLifecycleOwner.get()

    }


}