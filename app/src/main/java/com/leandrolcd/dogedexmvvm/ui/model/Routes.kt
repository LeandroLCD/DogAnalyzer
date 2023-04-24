package com.leandrolcd.dogedexmvvm.ui.model

sealed class Routes(val route:String) {
    object ScreenLogin: Routes("LoginScreen")
    object ScreenSignUp: Routes("SignUpScreen")
    object ScreenLoading: Routes("LoadingScreen")

    object ScreenCamera: Routes("CameraScreen")
    object ScreenDogList: Routes("DogListScreen")
    object ScreenDogDetail: Routes("DogDetailScreen/{isRecognition}/{dogList}") {
        fun routeName(isRecognition: Boolean, dogList: List<DogRecognition>) =
            "DogDetailScreen/$isRecognition/${dogList.joinToString(",") { "${it.id}:${it.confidence}" }}"
    }
    object ScreamCamera: Routes("CameraScream")
}
