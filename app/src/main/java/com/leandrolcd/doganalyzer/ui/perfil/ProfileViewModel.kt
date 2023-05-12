package com.leandrolcd.doganalyzer.ui.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandrolcd.doganalyzer.data.repositoty.IFireStoreRepository
import com.leandrolcd.doganalyzer.data.repositoty.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProfileViewModel @Inject constructor (private val loginRepository: LoginRepository,
private val dataStore: IFireStoreRepository):ViewModel() {

    var userCurrent by mutableStateOf(loginRepository.getUser())
    private set

   var dogCollection: StateFlow<Int>
   private set

    var croquettes: StateFlow<Int>
    private set

     init {

         dogCollection =
             getDogUser().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

         croquettes =
             getCroquettes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)



     }

    fun logout() {
        viewModelScope.launch {
            loginRepository.logout()
            dataStore.clearCache()
            dataStore.getCroquettes().apply {
                    dataStore.setCroquettes(this *- 1)
                }
            }

    }

    private fun getDogUser(): Flow<Int> = flow{

        var count = 0
        dataStore.getDogCollection().map {
            if(it.inCollection){
                count++
            }}

              emit(count)
        }
    private fun  getCroquettes(): Flow<Int> = flow{

        emit(dataStore.getCroquettes())
    }

}

