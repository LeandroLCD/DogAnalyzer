package com.leandrolcd.doganalyzer.domain

import com.leandrolcd.doganalyzer.data.repositoty.FireStoreRepository
import com.leandrolcd.doganalyzer.ui.model.Dog
import javax.inject.Inject

class AddDogToUserUseCase @Inject constructor(
    private val repository: FireStoreRepository
)  {
    suspend operator fun invoke(dog: Dog, croquettes: Int) {
        dog.mlId.let { repository.addDogToUser(it, croquettes) }
    }
}