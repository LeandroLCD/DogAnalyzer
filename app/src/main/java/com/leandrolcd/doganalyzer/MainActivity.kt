package com.leandrolcd.doganalyzer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.leandrolcd.doganalyzer.ui.admob.InterstitialAdMod
import com.leandrolcd.doganalyzer.ui.admob.RewardAdView
import com.leandrolcd.doganalyzer.ui.admob.removeInterstitial
import com.leandrolcd.doganalyzer.ui.auth.LoginScreen
import com.leandrolcd.doganalyzer.ui.auth.LoginViewModel
import com.leandrolcd.doganalyzer.ui.auth.SignUpScreen
import com.leandrolcd.doganalyzer.ui.auth.controls.StartScreen
import com.leandrolcd.doganalyzer.ui.dogdetail.DogDetailScreen
import com.leandrolcd.doganalyzer.ui.doglist.DogListScreen
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.perfil.ProfileScreen
import com.leandrolcd.doganalyzer.ui.theme.DogAnalyzerTheme
import com.leandrolcd.doganalyzer.utility.SERVER_CLIENT_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoilApi
@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var interstitialAdMod: InterstitialAdMod

    @Inject
    lateinit var rewardAdView: RewardAdView

    lateinit var navigationController: NavHostController
    private val loginViewModel: LoginViewModel by viewModels()
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}
        interstitialAdMod.load(this)
        rewardAdView.load(this)

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        setContent {
            DogAnalyzerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
            composable(route = Routes.ScreenProfile.route) { ProfileScreen(navController = navigationController) }

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
            .requestIdToken(SERVER_CLIENT_ID)
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

            Toast.makeText(
                this,
                getString(R.string.an_error_google),
                Toast.LENGTH_LONG
            ).show()
        }

    }


}