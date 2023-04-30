package com.leandrolcd.doganalyzer.ui.dogdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.leandrolcd.doganalyzer.data.repositoty.IFireStoreRepository
import com.leandrolcd.doganalyzer.ui.admob.InterstitialAdMod
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.model.UiStatus.Error
import com.leandrolcd.doganalyzer.ui.model.UiStatus.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogDetailViewModel @Inject constructor(
    private val repository: IFireStoreRepository,
    private val interstitialAdMod: InterstitialAdMod
): ViewModel() {

    //region Fields
    var uiStatus = mutableStateOf<UiStatus<List<Dog>>>(UiStatus.Loaded())
        private set

    var dogStatus = mutableStateOf<List<Dog>?>(null)
        private set

    private lateinit var navHostController: NavHostController
    //endregion
    @SuppressLint("SuspiciousIndentation")
    fun getDogsById(dogs:List<DogRecognition>){
        uiStatus.value = UiStatus.Loading()

        viewModelScope.launch {
            uiStatus.value = repository.getDogsByIds(dogs)

            if (uiStatus.value is Success){
                dogStatus.value = (uiStatus.value as Success<List<Dog>>).data.sortedByDescending {
                    it.confidence
                }

            }
        }
    }
    fun addDogToUser(dogId: String){
        uiStatus.value = UiStatus.Loading()
        viewModelScope.launch {
            val resp = repository.addDogToUser(dogId)
            if(resp is Error) {
                uiStatus.value = Error(resp.message)
            }else if(resp is Success){
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