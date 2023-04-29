package com.leandrolcd.dogedexmvvm.ui.doglist

import androidx.camera.core.ImageProxy
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.leandrolcd.dogedexmvvm.data.repositoty.IClassifierRepository
import com.leandrolcd.dogedexmvvm.domain.AuthLoginUseCase
import com.leandrolcd.dogedexmvvm.domain.GetDogListUseCase
import com.leandrolcd.dogedexmvvm.ui.camera.CameraX
import com.leandrolcd.dogedexmvvm.ui.model.Dog
import com.leandrolcd.dogedexmvvm.ui.model.DogRecognition
import com.leandrolcd.dogedexmvvm.ui.model.Routes
import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import com.leandrolcd.dogedexmvvm.ui.model.UiStatus.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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