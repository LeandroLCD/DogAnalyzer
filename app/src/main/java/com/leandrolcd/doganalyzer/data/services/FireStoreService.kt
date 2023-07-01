package com.leandrolcd.doganalyzer.data.services

import android.annotation.SuppressLint
import androidx.annotation.Keep
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.leandrolcd.doganalyzer.data.dto.DogDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Keep
class FireStoreService @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val loginService: LoginService
) {
    private val DOG_APP = "DogListApp"
    private val  USERS = "Users"
    private val DOGLIST = "DogList"
    private val CROQUETTES_COUNT = "CroquettesCount"
    private val ML_ID = "mlId"
    private val newUser = hashMapOf(
        DOGLIST to arrayListOf<String>(),
        CROQUETTES_COUNT to 0
    )
    @SuppressLint("SuspiciousIndentation")
    suspend fun getDogListApp(): List<DogDTO> {
        val dogList = mutableListOf<DogDTO>()
        val user = loginService.getUser()
        user?.let {
            val querySnapshot = fireStore.collection(DOG_APP)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val dog = document.toObject<DogDTO>()
                dog?.let {
                    dogList.add(it)
                }
            }
        }

        return dogList
    }

    suspend fun getDogListUser(): List<String> {
        val dogList = mutableListOf<String>()
        val user = loginService.getUser()
        user?.apply {
        val querySnapshot = uid.let { uid ->
            val db =
                fireStore.collection(USERS).document(uid)
            db.get().await()
        }
        if (querySnapshot != null && querySnapshot.exists()) {
            val data = querySnapshot.data
            val dogIds = data?.get(DOGLIST) as? ArrayList<String>
            dogIds?.let {
                dogList.addAll(it)
            }
        }
        return dogList

        }
        return dogList
    }

    suspend fun deleteDataUser(uid: String):Boolean {
        try {

            val db = fireStore
            val batch = db.batch()

            // Eliminar la lista de perros del usuario, si existe
            val userDocRef = db.collection(USERS).document(uid)
            val userSnapshot = userDocRef.get().await()
            if (userSnapshot.exists()) {
                val dogList = userSnapshot.get(DOGLIST) as? ArrayList<String>
                dogList?.let {
                    batch.update(userDocRef, DOGLIST, FieldValue.delete())
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
            val db = fireStore.collection(USERS).document(uid)

            // Verifica si el documento existe
            val snapshot = db.get().await()
            if (!snapshot.exists()) {
                // El documento no existe, crea un nuevo documento con el ID de usuario y la lista de perros vacía

                db.set(newUser).await()
            }

            // Obtiene la lista existente
            val dogList = snapshot.get(DOGLIST) as? ArrayList<String> ?: arrayListOf()

            // Agrega el nuevo elemento a la lista existente si no existe el dog
            if (!dogList.contains(dogId)) {
                dogList.add(dogId)
                // Actualiza el documento en Firestore
                db.update(DOGLIST, dogList).await()
            }
            return true
        }
        return false
    }
    suspend fun addCroquettes(croquettes: Int): Boolean {
        val user = loginService.getUser()
        user?.run {
            val db = fireStore.collection(USERS).document(uid)
            val snapshot = db.get().await()
            if (!snapshot.exists()) {
                db.set(newUser).await()
            } else {

                val currentCroquettesCount = snapshot.getLong(CROQUETTES_COUNT) ?: 0
                val newCroquettesCount = currentCroquettesCount + croquettes
                db.update(CROQUETTES_COUNT, newCroquettesCount).await()
            }
            return true
        }
        return false
    }
    suspend fun getCroquettes(): Int {
        val user = loginService.getUser()
        user?.let {
            val db = fireStore.collection(USERS).document(it.uid)
            val snapshot = db.get().await()
            if (snapshot.exists()) {
                return snapshot.getLong(CROQUETTES_COUNT)?.toInt() ?: 0
            }
            return 0
        }
        return 0
    }


//    suspend fun addDog(dog: Dogfb): String {
//        val db = fireStore.collection("DogListApp")
//        val documentRef = db.add(dog).await()
//        return documentRef.id
//    }

    suspend fun getDogById(mlId: String): DogDTO {
        val querySnapshot =
            fireStore.collection(DOG_APP).whereEqualTo(ML_ID, mlId).get().await()
        if (querySnapshot.isEmpty) {
            throw Exception("No se encontró ningún documento con mlId")
        }

        val docSnapshot = querySnapshot.documents.first()
        val dog = docSnapshot.toObject<DogDTO>()
        return dog ?: DogDTO()

    }


    suspend fun getDogsByIds(mlIds: List<String>): List<DogDTO> {
        val querySnapshot = fireStore.collection(DOG_APP)
            .whereIn(ML_ID, mlIds)
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



