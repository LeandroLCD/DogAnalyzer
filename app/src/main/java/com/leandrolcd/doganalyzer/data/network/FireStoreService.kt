package com.leandrolcd.doganalyzer.data.network

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.leandrolcd.doganalyzer.data.dto.DogDTO
import com.leandrolcd.doganalyzer.ui.model.Dogfb
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireStoreService @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val loginService: LoginService
) {
    val newUser = hashMapOf(
        "DogList" to arrayListOf<String>(),
        "CroquettesCount" to 0
    )
    @SuppressLint("SuspiciousIndentation")
    suspend fun getDogListApp(): List<DogDTO> {
        val dogList = mutableListOf<DogDTO>()
        val querySnapshot = fireStore.collection("DogListApp")
            .get()
            .await()

        Log.d("TAG", "getDogListApp: ${querySnapshot.documents}")
        for (document in querySnapshot.documents) {
            val dog = document.toObject<DogDTO>()
            dog?.let {
                dogList.add(it)
            }
        }

        return dogList
    }

    suspend fun getDogListUser(): List<String> {
        val user = loginService.getUser()
        val querySnapshot = user?.uid?.let { uid ->
            val db =
                fireStore.collection("Users").document(uid)  //..collection("DogList")document(uid)
            db.get().await()
        }
        val dogList = mutableListOf<String>()
        if (querySnapshot != null && querySnapshot.exists()) {
            val data = querySnapshot.data
            val dogIds = data?.get("DogList") as? ArrayList<String>
            dogIds?.let {
                dogList.addAll(it)
            }
        }
        return dogList
    }

    suspend fun deleteDataUser(uid: String):Boolean {
        try {

        val db = fireStore
        val batch = db.batch()

        // Eliminar la lista de perros del usuario, si existe
        val userDocRef = db.collection("Users").document(uid)
        val userSnapshot = userDocRef.get().await()
        if (userSnapshot.exists()) {
            val dogList = userSnapshot.get("DogList") as? ArrayList<String>
            dogList?.let {
                batch.update(userDocRef, "DogList", FieldValue.delete())
                it.forEach { dogId ->
                    val dogDocRef = db.collection("Dogs").document(dogId)
                    batch.delete(dogDocRef)
                }
            }
        }

        // Eliminar el documento del usuario
        batch.delete(userDocRef)

        // Ejecutar las operaciones en lote
        batch.commit().await()
            return true
        }catch (e:Exception){
            return false
        }
    }


    suspend fun addDogIdToUser(dogId: String): Boolean {
        val user = loginService.getUser()
        user?.run {
            val db = fireStore.collection("Users").document(uid)

            // Verifica si el documento existe
            val snapshot = db.get().await()
            if (!snapshot.exists()) {
                // El documento no existe, crea un nuevo documento con el ID de usuario y la lista de perros vacía

                db.set(newUser).await()
            }

            // Obtiene la lista existente
            val dogList = snapshot.get("DogList") as? ArrayList<String> ?: arrayListOf()

            // Agrega el nuevo elemento a la lista existente si no existe el dog
            if (!dogList.contains(dogId)) {
                dogList.add(dogId)
                // Actualiza el documento en Firestore
                db.update("DogList", dogList).await()
            }
            return true
        }
        return false
    }
    suspend fun addCroquettes(croquettes: Int): Boolean {
        val user = loginService.getUser()
        user?.run {
            val db = fireStore.collection("Users").document(uid)
            val snapshot = db.get().await()
            if (!snapshot.exists()) {
                 db.set(newUser).await()
            } else {

                 val currentCroquettesCount = snapshot.getLong("CroquettesCount") ?: 0
                val newCroquettesCount = currentCroquettesCount + croquettes
                db.update("CroquettesCount", newCroquettesCount).await()
            }
            return true
        }
        return false
    }
    suspend fun getCroquettes(): Int {
        val user = loginService.getUser()
        user?.let {
            val db = fireStore.collection("Users").document(it.uid)
            val snapshot = db.get().await()
            if (snapshot.exists()) {
                return snapshot.getLong("CroquettesCount")?.toInt() ?: 0
            }
            return 0
        }
        return 0
    }


    suspend fun addDog(dog: Dogfb): String {
        val db = fireStore.collection("DogListApp")
        val documentRef = db.add(dog).await()
        return documentRef.id
    }

    suspend fun getDogById(mlId: String): DogDTO {
        val querySnapshot =
            fireStore.collection("DogListApp").whereEqualTo("mlId", mlId).get().await()
        if (querySnapshot.isEmpty) {
            throw Exception("No se encontró ningún documento con mlId")
        }

        val docSnapshot = querySnapshot.documents.first()
        val dog = docSnapshot.toObject<DogDTO>()
        return dog ?: DogDTO()

    }


    suspend fun getDogsByIds(mlIds: List<String>): List<DogDTO> {
        val querySnapshot = fireStore.collection("DogListApp")
            .whereIn("mlId", mlIds)
            .get().await()

        if (querySnapshot.isEmpty) {
            throw Exception("No se encontró ningún documento con mlId")
        }

        val dogList = mutableListOf<DogDTO>()
        for (document in querySnapshot) {
            val dog = document.toObject(DogDTO::class.java)
            dogList.add(dog)
        }
        return dogList

    }

}



