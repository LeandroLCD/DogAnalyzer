package com.leandrolcd.doganalyzer.ui.doglist

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.domain.repository.ICameraRepository
import com.leandrolcd.doganalyzer.domain.repository.IClassifierRepository
import com.leandrolcd.doganalyzer.domain.repository.IFireStoreRepository
import com.leandrolcd.doganalyzer.ui.admob.RewardAdView
import com.leandrolcd.doganalyzer.ui.model.DogListScreen
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.states.DogUiState
import com.leandrolcd.doganalyzer.utility.getAdRewardClick
import com.leandrolcd.doganalyzer.utility.getDateAdReward
import com.leandrolcd.doganalyzer.utility.setAdRewardClick
import com.leandrolcd.doganalyzer.utility.setDateAdReward
import com.leandrolcd.doganalyzer.utility.toDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DogListViewModel@Inject constructor(
    cameraX: ICameraRepository,
    private val classifierRepository: IClassifierRepository,
    private val repository: IFireStoreRepository,
    private val rewardAdView: RewardAdView,
    private val contextApp: Context,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    var cameraX = mutableStateOf(cameraX)
        private set

    var uiStatus by mutableStateOf<DogUiState<DogListScreen>>(DogUiState.Loading())
        private set


    lateinit var navHostController: NavHostController

    val dogRecognition = mutableStateOf(listOf(DogRecognition("", 0f)))

    var counterAdReward by mutableStateOf(0)
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

            repository.getDogListAndCroquettes().collect{
                uiStatus = it
            }
        }
    }
    fun recognizerImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            dogRecognition.value = classifierRepository.recognizeImage(imageProxy)

            imageProxy.close()
        }
    }
    fun onUnCoverRequest(mlId: String, croquettes:Int) {
        viewModelScope.launch {
            repository.addDogToUser(mlId, croquettes * -1)
            withContext(Dispatchers.Main){
                Toast.makeText(contextApp, contextApp.getString(R.string.dog_reward), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onRewardShow(context: Activity){
        rewardAdView.show(context) {
                    viewModelScope.launch {
                repository.setCroquettes(it)
                        context.setAdRewardClick(1)
                        counterAdReward = contextApp.getAdRewardClick()
            }
        }
    }


}