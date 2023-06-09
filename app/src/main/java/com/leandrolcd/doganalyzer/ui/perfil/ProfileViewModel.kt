package com.leandrolcd.doganalyzer.ui.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandrolcd.doganalyzer.domain.repository.IFireStoreRepository
import com.leandrolcd.doganalyzer.domain.repository.LoginRepository
import com.leandrolcd.doganalyzer.ui.states.DogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProfileViewModel @Inject constructor (private val loginRepository: LoginRepository,
                                            private val dataStore: IFireStoreRepository
):ViewModel() {

    var userCurrent by mutableStateOf(loginRepository.getUser())
    private set

   var dogCollection by mutableStateOf(0)
   private set
    var croquettes  by mutableStateOf(0)
    private set

     init {

         getDogUser()
     }

    fun logout() {
        viewModelScope.launch {

            loginRepository.logout()
            dataStore.clearCache()
        }

    }

    private fun getDogUser(){
        viewModelScope.launch(Dispatchers.IO) {

            dataStore.getDogListAndCroquettes().collect(){
                var count = 0
                if(it is DogUiState.Success){
                    it.data.dogList.map{dog->
                        if(dog.inCollection){
                            count++
                        }}
                     croquettes = it.data.croquettes
                    dogCollection = count
                    }

                }



        }
        }



}

