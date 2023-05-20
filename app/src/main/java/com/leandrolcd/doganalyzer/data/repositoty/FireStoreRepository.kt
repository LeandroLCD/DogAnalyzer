package com.leandrolcd.doganalyzer.data.repositoty

import android.content.Context
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.core.makeNetworkCall
import com.leandrolcd.doganalyzer.data.dto.DogDTO
import com.leandrolcd.doganalyzer.data.network.FireStoreService
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.utilits.isNetworkConnected
import com.leandrolcd.doganalyzer.ui.utilits.setZeroAdRewardClick
import com.leandrolcd.doganalyzer.ui.utilits.toDog
import com.leandrolcd.doganalyzer.ui.utilits.toDogList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.floor

interface IFireStoreRepository {
    suspend fun addDogToUser(dogId: String, croquettes: Int): UiStatus<Boolean>
    suspend fun getDogCollection(): List<Dog>
    suspend fun getDogById(id: String): UiStatus<Dog>
    fun clearCache()

    suspend fun getCroquettes(): Flow<Int>

    suspend fun setCroquettes(croquettes: Int):Boolean
    suspend fun getDogsByIds(list: List<DogRecognition>): UiStatus<List<Dog>>
    suspend fun synchronizeNow(uid: String)
}

class FireStoreRepository @Inject constructor(
    private val fireStore: FireStoreService,
    private val context: Context,
    private val dispatcher: CoroutineDispatcher
) : IFireStoreRepository {

    companion object {
        private lateinit var dogCollection: List<Dog>
        private var dogListApp: MutableList<DogDTO> = mutableListOf()
        private var dogIdUser: MutableList<String> = mutableListOf()
        private var croquettesCache = 0
    }

    override suspend fun addDogToUser(dogId: String, croquettes: Int): UiStatus<Boolean> {
        return makeNetworkCall(dispatcher) {
            connectionChecked()
            val resp = fireStore.addDogIdToUser(dogId)
            if (resp && !dogIdUser.contains(dogId)) {
                dogIdUser.add(dogId)
                setCroquettes(croquettes)
            }
            resp
        }
    }

    override suspend fun getDogCollection(): List<Dog> {

        return withContext(dispatcher) {
            connectionChecked()
            dogCollection = if (dogListApp.isEmpty()) {
                val dogUserDeferred = async { fireStore.getDogListUser() }
                val allDogDeferred = async { fireStore.getDogListApp() }
                val dogUser = dogUserDeferred.await()
                val dogApp = allDogDeferred.await()
                dogListApp.addAll(dogApp)
                dogIdUser.addAll(dogUser)
                getCollectionList(dogApp, dogUser)
            } else {
                getCollectionList(dogListApp, dogIdUser)
            }

            dogCollection
        }
    }

    override suspend fun getDogById(id: String): UiStatus<Dog> {
        return makeNetworkCall(dispatcher) {
            connectionChecked()
            fireStore.getDogById(id).toDog()
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

    override suspend fun getDogsByIds(list: List<DogRecognition>): UiStatus<List<Dog>> {
        return makeNetworkCall(dispatcher) {
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