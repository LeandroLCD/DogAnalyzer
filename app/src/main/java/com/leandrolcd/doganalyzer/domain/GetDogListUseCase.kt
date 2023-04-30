package com.leandrolcd.doganalyzer.domain

import com.leandrolcd.doganalyzer.data.repositoty.IFireStoreRepository
import com.leandrolcd.doganalyzer.ui.model.Dog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDogListUseCase @Inject constructor(
    private val repository: IFireStoreRepository
) {
    suspend operator fun invoke(): Flow<List<Dog>> = flow {
        while(true) {
            val dogs = repository.getDogCollection()
            emit(dogs)
            delay(5000)
        }
    }

    fun clearCache() = repository.clearCache()
}