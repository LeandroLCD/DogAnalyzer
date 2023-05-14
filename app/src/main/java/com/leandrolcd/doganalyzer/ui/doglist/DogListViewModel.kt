package com.leandrolcd.doganalyzer.ui.doglist

import androidx.camera.core.ImageProxy
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.leandrolcd.doganalyzer.data.repositoty.IClassifierRepository
import com.leandrolcd.doganalyzer.domain.IGetDogListUseCase
import com.leandrolcd.doganalyzer.ui.camera.ICameraX
import com.leandrolcd.doganalyzer.ui.model.*
import com.leandrolcd.doganalyzer.ui.model.UiStatus.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@Suppress("ThrowableNotThrown")
@HiltViewModel
class DogListViewModel@Inject constructor(
    cameraX: ICameraX,
    private val classifierRepository: IClassifierRepository,
    private val dogUseCase: IGetDogListUseCase
) : ViewModel() {

    var cameraX = mutableStateOf(cameraX)
        private set

    lateinit var uiStatus: StateFlow<UiStatus<List<Dog>>>

    lateinit var croquettes: StateFlow<Int>

    lateinit var navHostController: NavHostController

    val dogRecognition = mutableStateOf(listOf(DogRecognition("", 0f)))

    init {
        startStatus()
    }

    private fun startStatus(){
        viewModelScope.launch {

            uiStatus = dogUseCase().map(::Success)
                .catch { Error(it) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), UiStatus.Loading())
            croquettes =
                dogUseCase.getCroquettes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


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


}