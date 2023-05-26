package com.leandrolcd.doganalyzer.core.interfaces

import androidx.annotation.Keep
import com.leandrolcd.doganalyzer.data.repository.FireStoreRepository
import com.leandrolcd.doganalyzer.data.repository.IFireStoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Keep
@Module
@InstallIn(SingletonComponent::class)
abstract class FireStoreBinds {
    @Binds
    abstract fun bindsFireStore(fireStoreRepository : FireStoreRepository): IFireStoreRepository

}