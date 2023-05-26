package com.leandrolcd.doganalyzer.ui.dogdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.annotation.Keep
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.leandrolcd.doganalyzer.data.repository.IFireStoreRepository
import com.leandrolcd.doganalyzer.ui.admob.InterstitialAdMod
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.states.DogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Keep
@HiltViewModel
class DogDetailViewModel @Inject constructor(
    private val repository: IFireStoreRepository,
    private val interstitialAdMod: InterstitialAdMod
): ViewModel() {

    //region Fields
    var uiStatus = mutableStateOf<DogUiState<List<Dog>>>(DogUiState.Loaded())
        private set

    var dogStatus = mutableStateOf<List<Dog>?>(null)
        private set



    private lateinit var navHostController: NavHostController
    //endregion

    @SuppressLint("SuspiciousIndentation")
    fun getDogsById(dogs:List<DogRecognition>){
        uiStatus.value = DogUiState.Loading()

        viewModelScope.launch {
            uiStatus.value = repository.getDogsByIds(dogs)

            if (uiStatus.value is DogUiState.Success){
                dogStatus.value = (uiStatus.value as DogUiState.Success<List<Dog>>).data.sortedByDescending {
                    it.confidence
                }

            }
        }
    }
    fun addDogToUser(dogId: String, croquettes: Int){
        uiStatus.value = DogUiState.Loading()
        viewModelScope.launch {
            val resp = repository.addDogToUser(dogId, croquettes)
            if(resp is DogUiState.Error) {
                uiStatus.value = DogUiState.Error(resp.message)
            }else if(resp is DogUiState.Success){
                navHostController.popBackStack(
                    Routes.ScreenDogList.route,
                    false
                )

            }
        }
    }
    fun setNavHostController(navController: NavHostController){
        navHostController = navController

    }
    fun interstitialShow(activity: Activity) {
        interstitialAdMod.show(activity) {
            Log.d("TAG", "interstitialShow: ViewModelShow")
        }
    }

}