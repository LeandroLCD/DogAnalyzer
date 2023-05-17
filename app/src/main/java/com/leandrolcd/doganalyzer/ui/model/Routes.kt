package com.leandrolcd.doganalyzer.ui.model

sealed class Routes(val route:String) {
    object ScreenLogin: Routes("LoginScreen")
    object ScreenSignUp: Routes("SignUpScreen")
    object ScreenLoading: Routes("LoadingScreen")
    object ScreenDogList: Routes("DogListScreen")

    object ScreenProfile: Routes("ProfileScreen")
    object ScreenDogDetail: Routes("DogDetailScreen/{isRecognition}/{dogList}") {
        fun routeName(isRecognition: Boolean, dogList: List<DogRecognition>) =
            "DogDetailScreen/$isRecognition/${dogList.joinToString(",") { "${it.id}:${it.confidence}" }}"
    }
}
