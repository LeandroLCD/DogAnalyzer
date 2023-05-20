package com.leandrolcd.doganalyzer.domain

import com.leandrolcd.doganalyzer.data.repositoty.IFireStoreRepository
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface IGetDogListUseCase{
    suspend operator fun invoke(): Flow<List<Dog>>
    suspend fun addDogByMlId(mlId:String, croquettes: Int): UiStatus<Boolean>

    suspend fun addDogForReward(mlId:String, croquettes: Int): UiStatus<Boolean>
    suspend fun setCroquettes(croquettes:Int)
    suspend fun getCroquettes():Flow<Int>
    fun clearCache()
}

class GetDogListUseCase @Inject constructor(
    private val repository: IFireStoreRepository
): IGetDogListUseCase{
    override suspend operator fun invoke(): Flow<List<Dog>> = flow {
        while(true) {
            val dogs = repository.getDogCollection()
            delay(2000L)
            emit(dogs)
        }
    }

    override suspend fun addDogByMlId(mlId: String, croquettes: Int):UiStatus<Boolean> =
        repository.addDogToUser(mlId, croquettes)

    override suspend fun addDogForReward(mlId: String, croquettes: Int): UiStatus<Boolean> =
        repository.addDogToUser(mlId, croquettes)


    override fun clearCache() = repository.clearCache()

    override suspend fun getCroquettes(): Flow<Int> = repository.getCroquettes()

    override suspend fun setCroquettes(croquettes:Int){
        repository.setCroquettes(croquettes)
    }
}