package com.leandrolcd.doganalyzer.ui.doglist

import android.app.Activity
import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.leandrolcd.doganalyzer.data.repositoty.IClassifierRepository
import com.leandrolcd.doganalyzer.domain.IGetDogListUseCase
import com.leandrolcd.doganalyzer.ui.admob.RewardAdView
import com.leandrolcd.doganalyzer.ui.camera.ICameraX
import com.leandrolcd.doganalyzer.ui.model.*
import com.leandrolcd.doganalyzer.ui.model.UiStatus.Success
import com.leandrolcd.doganalyzer.ui.utilits.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@Suppress("ThrowableNotThrown")
@HiltViewModel
class DogListViewModel@Inject constructor(
    cameraX: ICameraX,
    private val classifierRepository: IClassifierRepository,
    private val dogUseCase: IGetDogListUseCase,
    private val rewardAdView: RewardAdView,
    private val contextApp: Context,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    var cameraX = mutableStateOf(cameraX)
        private set

    lateinit var uiStatus: StateFlow<UiStatus<List<Dog>>>



    lateinit var navHostController: NavHostController

    val dogRecognition = mutableStateOf(listOf(DogRecognition("", 0f)))

    var counterAdReward by mutableStateOf(0)
        private set
    var croquettes by mutableStateOf(0)
        private set

    init {
        startStatus()
        onCheckedReward()
    }

    private fun onCheckedReward() {
        val cReward = contextApp.getAdRewardClick()
        val dateReward = contextApp.getDateAdReward()
        val calendar = Calendar.getInstance()
        val date = calendar.toDay()
        if(date != dateReward){
            contextApp.setAdRewardClick(-cReward)
            contextApp.setDateAdReward(date)
        }
        counterAdReward = contextApp.getAdRewardClick()
    }

    private fun startStatus(){
        viewModelScope.launch(dispatcher) {

            uiStatus = dogUseCase().map(::Success)
                .catch { Error(it) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), UiStatus.Loading())
              dogUseCase.getCroquettes().collect{
                  croquettes = it
            }



        }
    }
    fun recognizerImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            dogRecognition.value = classifierRepository.recognizeImage(imageProxy)

            imageProxy.close()
        }
    }
    fun onUnCoverRequest(mlId: String) {
        viewModelScope.launch {
            dogUseCase.addDogByMlId(mlId, 0)
        }
    }

    fun onRewardShow(context: Activity){
        rewardAdView.show(context) {
                    viewModelScope.launch {
                dogUseCase.setCroquettes(it)
                        context.setAdRewardClick(1)
                        counterAdReward = contextApp.getAdRewardClick()
            }
        }
    }


}