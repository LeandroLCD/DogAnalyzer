package com.leandrolcd.doganalyzer.data.repositoty

import android.content.Context
import com.leandrolcd.doganalyzer.core.makeNetworkCall
import com.leandrolcd.doganalyzer.data.dto.DogDTO
import com.leandrolcd.doganalyzer.data.network.FireStoreService
import com.leandrolcd.doganalyzer.isNetworkConnected
import com.leandrolcd.doganalyzer.toDog
import com.leandrolcd.doganalyzer.toDogList
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.floor

interface IFireStoreRepository{
    suspend fun addDogToUser(dogId: String): UiStatus<Boolean>
    suspend fun getDogCollection(): List<Dog>
    suspend fun getDogById(id:String):UiStatus<Dog>
    fun clearCache()
    suspend fun getDogsByIds(list: List<DogRecognition>): UiStatus<List<Dog>>
}

class FireStoreRepository @Inject constructor(
    private val fireStore: FireStoreService,
    private val context: Context,
    private val dispatcher: CoroutineDispatcher):IFireStoreRepository {

    companion object{
        private lateinit var dogCollection:List<Dog>
        private var dogListApp: MutableList<DogDTO> = mutableListOf()
        private var dogIdUser: MutableList<String> = mutableListOf()

    }
    override suspend fun addDogToUser(dogId: String): UiStatus<Boolean> {
        return makeNetworkCall(dispatcher) {
            if (!isNetworkConnected(context)) {
                throw Exception("El dispositivo no cuenta con conexión a internet")
            }
            val resp = fireStore.addDogIdToUser(dogId)
            if (resp) {
                dogIdUser.add(dogId)
            }
            resp
        }
    }

    override suspend fun getDogCollection(): List<Dog> {

        return withContext(dispatcher) {

            dogCollection = if(dogListApp.isEmpty()){
                val dogUserDeferred = async { fireStore.getDogListUser() }
                val allDogDeferred = async { fireStore.getDogListApp() }
                dogListApp.clear()
                dogIdUser.clear()
                val dogUser = dogUserDeferred.await()
                val dogApp = allDogDeferred.await()
                dogListApp.addAll(dogApp)
                dogIdUser.addAll(dogUser)
                getCollectionList(dogApp, dogUser)
            }else{
                getCollectionList(dogListApp, dogIdUser)
            }

            dogCollection
        }
    }
    override suspend fun getDogById(id:String):UiStatus<Dog>{
        return makeNetworkCall(dispatcher) {
            if (!isNetworkConnected(context)) {
                throw Exception("El dispositivo no cuenta con conexión a internet")
            }
            fireStore.getDogById(id).toDog()
        }
    }

    override fun clearCache() {
        dogIdUser.clear()
        dogListApp.clear()
    }

    override suspend fun getDogsByIds(list: List<DogRecognition>): UiStatus<List<Dog>> {
        return makeNetworkCall(dispatcher) {
            if (!isNetworkConnected(context)) {
                throw Exception("El dispositivo no cuenta con conexión a internet")
            }
            if (dogListApp.isEmpty()){
                fireStore.getDogsByIds(list.map { it.id }).toDogList()
            }
            val filteredDogs = dogListApp.toDogList().filter { list.map { r -> r.id }.contains(it.mlId) }
           filteredDogs.map {
                it.confidence = floor( list.find { r-> r.id == it.mlId }?.confidence ?: 0f)
               it
            }

        }
    }
    private fun getCollectionList(allDogList: List<DogDTO>, userDogList: List<String>): List<Dog> {
        val dog = allDogList.toDogList().map {
            if (userDogList.contains(it.mlId)) {
                it.inCollection = true
                it
            } else {
                Dog(mlId = it.mlId, index = it.index)
            }
        }.sorted().sortedBy { it.index }
        return dog
    }
}