package com.leandrolcd.doganalyzer.domain.repository

import android.content.Context
import androidx.annotation.Keep
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.data.services.FireStoreService
import com.leandrolcd.doganalyzer.data.dto.DogDTO
import com.leandrolcd.doganalyzer.domain.model.ResultType
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogListScreen
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.states.DogUiState
import com.leandrolcd.doganalyzer.utility.isNetworkConnected
import com.leandrolcd.doganalyzer.utility.setZeroAdRewardClick
import com.leandrolcd.doganalyzer.utility.toDog
import com.leandrolcd.doganalyzer.utility.toDogList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.floor

@Keep
interface IFireStoreRepository {
    suspend fun getDogListAndCroquettes(): Flow<DogUiState<DogListScreen>>

    suspend fun addDogToUser(dogId: String, croquettes: Int): DogUiState<Boolean>
    suspend fun getDogCollection(): DogUiState<List<Dog>>
    suspend fun getDogById(id: String): DogUiState<Dog>
    fun clearCache()

    suspend fun getCroquettes(): Flow<Int>

    suspend fun setCroquettes(croquettes: Int):Boolean
    suspend fun getDogsByIds(list: List<DogRecognition>): DogUiState<List<Dog>>
    suspend fun synchronizeNow(uid: String)
}
@Keep
class FireStoreRepository @Inject constructor(
    private val fireStore: FireStoreService,
    private val context: Context,
    private val dispatcher: CoroutineDispatcher
) : BaseRepository(dispatcher), IFireStoreRepository {

    companion object {
        private lateinit var dogCollection: List<Dog>
        private var dogListApp: MutableList<DogDTO> = mutableListOf()
        private var dogIdUser: MutableList<String> = mutableListOf()
        private var croquettesCache = 0
    }

    override suspend fun getDogListAndCroquettes(): Flow<DogUiState<DogListScreen>> = flow {
        while(true) {
            val croquet = getCroquettes().first()
            when(val dogs = getDogCollection()){
                is DogUiState.Error -> emit(DogUiState.Error(dogs.message))
                is DogUiState.Loaded -> emit(DogUiState.Loaded())
                is DogUiState.Loading -> emit(DogUiState.Loading())
                is DogUiState.Success -> emit(DogUiState.Success(DogListScreen(croquet, dogs.data)))
            }

            delay(2000L)

        }
    }

    override suspend fun addDogToUser(dogId: String, croquettes: Int): DogUiState<Boolean> {
        val resultType = makeNetworkCall {
            connectionChecked()
            val resp = fireStore.addDogIdToUser(dogId)
            if (resp && !dogIdUser.contains(dogId)) {
                dogIdUser.add(dogId)
                setCroquettes(croquettes)
            }
        }
        return when(resultType){
            is ResultType.Error -> DogUiState.Error(resultType.message)
            is ResultType.Success -> DogUiState.Success(true)
        }



    }

    override suspend fun getDogCollection(): DogUiState<List<Dog>> {

      val resultType = makeNetworkCall {
           connectionChecked()
           dogCollection = if (dogListApp.isEmpty()) {
               withContext(dispatcher){
                   val dogUserDeferred = async { fireStore.getDogListUser() }
                   val allDogDeferred = async { fireStore.getDogListApp() }
                   val dogUser = dogUserDeferred.await()
                   val dogApp = allDogDeferred.await()
                   dogListApp.addAll(dogApp)
                   dogIdUser.addAll(dogUser)
                   getCollectionList(dogApp, dogUser)
               }

           } else {
               getCollectionList(dogListApp, dogIdUser)
           }


       }

      return  when(resultType){
          is ResultType.Error -> DogUiState.Error(resultType.message)
          is ResultType.Success -> DogUiState.Success(dogCollection)
      }





    }

    override suspend fun getDogById(id: String): DogUiState<Dog> {
        val resultType = makeNetworkCall{
            connectionChecked()
            fireStore.getDogById(id).toDog()
        }
        return when(resultType){
            is ResultType.Error -> DogUiState.Error(resultType.message)
            is ResultType.Success -> DogUiState.Success(resultType.data)
        }


    }

    override fun clearCache() {
        dogIdUser.clear()
        dogListApp.clear()
        croquettesCache = 0
    }

    override suspend fun getCroquettes(): Flow<Int> = flow {
        while (true) {
            delay(2000L)
            if (croquettesCache == 0) {
                croquettesCache = fireStore.getCroquettes()
            }
            emit(croquettesCache)
        }
    }

    override suspend fun setCroquettes(croquettes: Int):Boolean {
        return withContext(dispatcher){
            connectionChecked()
            val deferred = async {fireStore.addCroquettes(croquettes) }
            val resp = deferred.await()
            if(resp){
                croquettesCache += croquettes
            }
            resp
        }
    }

    override suspend fun getDogsByIds(list: List<DogRecognition>): DogUiState<List<Dog>> {
        val resultType = makeNetworkCall{
            connectionChecked()
            if (dogListApp.isEmpty()) {
                fireStore.getDogsByIds(list.map { it.id }).toDogList()
            }
            val filteredDogs =
                dogListApp.toDogList().filter { list.map { r -> r.id }.contains(it.mlId) }
            filteredDogs.map {
                it.confidence = floor(list.find { r -> r.id == it.mlId }?.confidence ?: 0f)
                it
            }

        }
        return when(resultType){
            is ResultType.Error -> DogUiState.Error(resultType.message)
            is ResultType.Success -> DogUiState.Success(resultType.data)
        }
    }

    override suspend fun synchronizeNow(uid: String) {

        withContext(dispatcher) {
            connectionChecked()
            val deferred = async {  fireStore.deleteDataUser(uid) }
            val list = fireStore.getDogListUser()
            if (dogIdUser.isNotEmpty()) {
                dogIdUser.map {
                    if (!list.contains(it)) {
                        addDogToUser(it, 0)
                    } else {
                        dogIdUser.remove(it)
                    }
                }

            }
            val delete = deferred.await()
            if(delete){
                setCroquettes(croquettesCache)
                context.setZeroAdRewardClick()
                croquettesCache = 0
                dogIdUser.addAll(list)
            }

        }

    }



    private fun getCollectionList(allDogList: List<DogDTO>, userDogList: List<String>): List<Dog> {
        val dog = allDogList.toDogList().map {
            if (userDogList.contains(it.mlId)) {
                it.inCollection = true
                it
            } else {
                Dog(mlId = it.mlId, index = it.index, croquettes = it.croquettes)
            }
        }.sorted().sortedBy { it.index }
        return dog
    }

    private fun connectionChecked(){
        if (!isNetworkConnected(context)) {
            throw Exception(context.getString(R.string.connection_off))
        }
    }
}