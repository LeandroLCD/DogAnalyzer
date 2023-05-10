package com.leandrolcd.doganalyzer.core.interfaces

import com.leandrolcd.doganalyzer.domain.GetDogListUseCase
import com.leandrolcd.doganalyzer.domain.IGetDogListUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GetDogListUseCaseBinds {
    @Binds
    abstract fun bindsGetDogListUseCase(getDogListUseCase: GetDogListUseCase): IGetDogListUseCase
}