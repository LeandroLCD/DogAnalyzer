package com.leandrolcd.doganalyzer.core.interfaces

import com.leandrolcd.doganalyzer.data.repositoty.FireStoreRepository
import com.leandrolcd.doganalyzer.data.repositoty.IFireStoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FireStoreBinds {
    @Binds
    abstract fun bindsFireStore(fireStoreRepository : FireStoreRepository): IFireStoreRepository

}
