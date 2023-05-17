package com.leandrolcd.doganalyzer.core.app

import androidx.lifecycle.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LifecycleOwnerModule {

    @Provides
    fun provideLifecycleOwner(): LifecycleOwner {
        return ProcessLifecycleOwner.get()

    }


}

