package com.leandrolcd.doganalyzer.core.interfaces

import androidx.annotation.Keep
import com.leandrolcd.doganalyzer.domain.repository.ClassifierRepository
import com.leandrolcd.doganalyzer.domain.repository.IClassifierRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Keep
@Module
@InstallIn(SingletonComponent::class)
abstract class ClassifierBinds {
    @Binds
    abstract fun bindsClassifier(classifierRepository: ClassifierRepository): IClassifierRepository

}