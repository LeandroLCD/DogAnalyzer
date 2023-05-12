package com.leandrolcd.doganalyzer.data.repositoty

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.leandrolcd.doganalyzer.core.makeNetworkCall
import com.leandrolcd.doganalyzer.data.dto.DogDTO
import com.leandrolcd.doganalyzer.data.network.FireStoreService
import com.leandrolcd.doganalyzer.isNetworkConnected
import com.leandrolcd.doganalyzer.preferencesDataStore
import com.leandrolcd.doganalyzer.toDog
import com.leandrolcd.doganalyzer.toDogList
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.floor

interface IFireStoreRepository {
    suspend fun addDogToUser(dogId: String): UiStatus<Boolean>
    suspend fun getDogCollection(): List<Dog>
    suspend fun getDogById(id: String): UiStatus<Dog>
    fun clearCache()

    suspend fun getCroquettes(): Int

    suspend fun setCroquettes(croquettes: Int)
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

    }

    override suspend fun addDogToUser(dogId: String): UiStatus<Boolean> {
        return makeNetworkCall(dispatcher) {
            if (!isNetworkConnected(context)) {
                throw Exception("El dispositivo no cuenta con conexión a internet")
            }
            val resp = fireStore.addDogIdToUser(dogId)
            if (resp && !dogIdUser.contains(dogId)) {
                dogIdUser.add(dogId)
                setCroquettes(2)
            }
            resp
        }
    }

    override suspend fun getDogCollection(): List<Dog> {

        return withContext(dispatcher) {

            dogCollection = if (dogListApp.isEmpty()) {
                val dogUserDeferred = async { fireStore.getDogListUser() }
                val allDogDeferred = async { fireStore.getDogListApp() }
                dogListApp.clear()
                dogIdUser.clear()
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

    override suspend fun getCroquettes(): Int = context.preferencesDataStore.data.map { pref ->
        pref[intPreferencesKey(name = "croquettes")] ?: 0
    }.first()

    override suspend fun setCroquettes(croquettes: Int) {
        val old = getCroquettes()
        context.preferencesDataStore.edit { pref ->
            if (old != 0) {
                pref[intPreferencesKey(name = "croquettes")] = old + croquettes
            } else {
                pref[intPreferencesKey(name = "croquettes")] = croquettes
            }
        }

    }

    override suspend fun getDogsByIds(list: List<DogRecognition>): UiStatus<List<Dog>> {
        return makeNetworkCall(dispatcher) {
            if (!isNetworkConnected(context)) {
                throw Exception("El dispositivo no cuenta con conexión a internet")
            }
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
            async { fireStore.deleteUser(uid) }
            val list = fireStore.getDogListUser()
            if (dogIdUser.isNotEmpty()) {
                dogIdUser.map {
                    if (!list.contains(it)) {
                        addDogToUser(it)
                    } else {
                        dogIdUser.remove(it)
                    }
                }

            }
            dogIdUser.addAll(list)
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