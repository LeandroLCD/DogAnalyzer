package com.leandrolcd.doganalyzer.ui.doglist

import androidx.camera.core.ImageProxy
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.leandrolcd.doganalyzer.data.repositoty.IClassifierRepository
import com.leandrolcd.doganalyzer.domain.AuthLoginUseCase
import com.leandrolcd.doganalyzer.domain.GetDogListUseCase
import com.leandrolcd.doganalyzer.ui.camera.CameraX
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.model.UiStatus
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
    cameraX: CameraX,
    private val classifierRepository: IClassifierRepository,
    private val dogUseCase: GetDogListUseCase,
    private val loginUseCase: AuthLoginUseCase
) : ViewModel() {

    var cameraX = mutableStateOf(cameraX)
        private set

    lateinit var uiStatus: StateFlow<UiStatus<List<Dog>>>

    lateinit var navHostController: NavHostController

    val dogRecognition = mutableStateOf(listOf(DogRecognition("", 0f)))

    init {

        viewModelScope.launch {

            uiStatus = dogUseCase().map(::Success)
                .catch { Error(it) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), UiStatus.Loading())


        }

        /*List de Dog
             val jsonList="[]"
        val gson = Gson()
        val listType = object : TypeToken<List<Dogfb>>() {}.type
        val dogList: List<Dogfb> = gson.fromJson(jsonList, listType)
        viewModelScope.launch {


           var list = mutableListOf<String>()
                for( dog in dogList){
                    //list.add(dataStore.addDog(dog))

                }
            Log.d("ListCoun", ": ${list.count()}")
      }
        */

    }


    fun recognizerImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            dogRecognition.value = classifierRepository.recognizeImage(imageProxy)

            imageProxy.close()
        }
    }

    fun logout() {
        loginUseCase.logout()
        dogUseCase.clearCache()
        navHostController.popBackStack()
        navHostController.navigate(Routes.ScreenLogin.route)
    }


}