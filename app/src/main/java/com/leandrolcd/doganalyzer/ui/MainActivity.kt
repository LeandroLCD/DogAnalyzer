package com.leandrolcd.doganalyzer.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.admob.InterstitialAdMod
import com.leandrolcd.doganalyzer.ui.admob.removeInterstitial
import com.leandrolcd.doganalyzer.ui.authentication.LoginComposeViewModel
import com.leandrolcd.doganalyzer.ui.authentication.utilities.LoginScreen
import com.leandrolcd.doganalyzer.ui.authentication.utilities.SignUpScreen
import com.leandrolcd.doganalyzer.ui.authentication.utilities.StartScreen
import com.leandrolcd.doganalyzer.ui.dogdetail.DogDetailScreen
import com.leandrolcd.doganalyzer.ui.doglist.DogListScreen
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.ui.theme.DogedexMVVMTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var interstitialAdMod: InterstitialAdMod
    lateinit var navigationController:NavHostController
    private val loginViewModel: LoginComposeViewModel by viewModels()
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        interstitialAdMod.load(this)
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        setContent {
            DogedexMVVMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    OnRegisterRoutes()

                }
            }
        }
    }
    override fun onDestroy() {
        removeInterstitial()
        super.onDestroy()
    }

    //region NavigationRoutes
    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    fun OnRegisterRoutes() {
        navigationController = rememberNavController()

        NavHost(navController = navigationController, startDestination = Routes.ScreenLoading.route) {
            composable(route = Routes.ScreenLoading.route) {
                StartScreen(navigationController, remoteConfig = remoteConfig)
            }

            composable(route = Routes.ScreenLogin.route) {
                LoginScreen(navigationController, viewModel = loginViewModel){
                    onLoginWithGoogleClicked()
                }
            }

            composable(route = Routes.ScreenSignUp.route) { SignUpScreen(navigationController) }

            composable(route = Routes.ScreenDogList.route) { DogListScreen(navigationController) }


            composable(
                route = Routes.ScreenDogDetail.route,
                arguments = listOf(
                    navArgument("isRecognition") { type = NavType.BoolType },
                    navArgument("dogList") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val isRecognition = backStackEntry.arguments?.getBoolean("isRecognition") ?: false
                val dogListString = backStackEntry.arguments?.getString("dogList") ?: ""
                val dogList = dogListString.split(",").map {
                    val (id, confidence) = it.split(":")
                    DogRecognition(id, confidence.toFloat())
                }
                DogDetailScreen(navController = navigationController, isRecognition = isRecognition, dogList = dogList)
            }


        }

    }


    //endregion

    private fun onLoginWithGoogleClicked() {

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val intent = googleSignInClient.signInIntent

        resultLauncher.launch(intent)

    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        val data: Intent? = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {

            val account = task.getResult(ApiException::class.java)!!
            loginViewModel.onLoginWithGoogle(account.idToken!!, navigationController)
        } catch (e: ApiException) {
            Log.w("log", "signInResult:failed code=${e.statusCode} ")
            Toast.makeText(
                this,
                "No se logro ingresar por favor ingrese correo y password",
                Toast.LENGTH_LONG
            ).show()
        }

    }


}