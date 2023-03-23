package com.leandrolcd.dogedexmvvm.dogslist


import com.leandrolcd.dogedexmvvm.Dog
import com.leandrolcd.dogedexmvvm.api.DogsApi.retrofitService
import com.leandrolcd.dogedexmvvm.api.makeNetworkCall

class DogRepository {
    suspend fun dowloadDogs(): UiStatus<List<Dog>> {
        return makeNetworkCall {
            val response = retrofitService.getAllDogs()
            response.data.dogs.map {
                Dog(
                    id = it.id,
                    index = it.index,
                    name = it.nameEs,
                    type = it.dogType,
                    temperament = it.temperament,
                    heightFemale = it.heightFemale,
                    heightMale = it.heightMale,
                    weightFemale = it.weightFemale,
                    weightMale = it.weightMale,
                    lifeExpectancy = it.lifeExpectancy,
                    imageUrl = it.imageUrl
                )

            }

        }
    }
//            return withContext(Dispatchers.IO) {
//                try {    val response = retrofitService.getAllDogs()
//                var data = response.data.dogs.map {
//                    Dog(
//                        id = it.id,
//                        index = it.index,
//                        name = it.nameEs,
//                        type = it.dogType,
//                        temperament = it.temperament,
//                        heightFemale = it.heightFemale,
//                        heightMale = it.heightMale,
//                        weightFemale = it.weightFemale,
//                        weightMale = it.weightMale,
//                        lifeExpectancy = it.lifeExpectancy,
//                        imageUrl = it.imageUrl
//                    )
//
//                }
//                UiStatus.Success(data)
//                }catch (e: UnknownHostException){
//                    UiStatus.Error(message = "El dispositivo no puede conectar con el server, revise la conexión a internet.")
//
//                }
//                catch (e: Exception) {
//                    UiStatus.Error(message = "Error al descargar datos.\n Detalles: ${e.message.toString()}")
//                }
    //}

}