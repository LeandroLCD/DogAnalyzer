package com.leandrolcd.doganalyzer.core.interfaces

import com.leandrolcd.doganalyzer.domain.AuthLoginUseCase
import com.leandrolcd.doganalyzer.domain.IAuthLoginUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthLoginUseCaseBinds {
    @Binds
   abstract fun bindsAuthLoginUseCase(authLoginUseCase: AuthLoginUseCase): IAuthLoginUseCase

}