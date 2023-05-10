package com.leandrolcd.doganalyzer.domain

import com.leandrolcd.doganalyzer.data.repositoty.FireStoreRepository
import javax.inject.Inject

class GetDogListAppUseCase @Inject constructor(
    private val repository:FireStoreRepository) {
    suspend operator fun invoke() {
        repository.getDogCollection()
    }
}