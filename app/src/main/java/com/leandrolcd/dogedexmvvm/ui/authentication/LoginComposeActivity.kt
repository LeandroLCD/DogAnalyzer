package com.leandrolcd.dogedexmvvm.ui.authentication

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.leandrolcd.dogedexmvvm.R
import com.leandrolcd.dogedexmvvm.ui.doglist.DogListScreen
import com.leandrolcd.dogedexmvvm.ui.authentication.utilities.LoginScreen
import com.leandrolcd.dogedexmvvm.ui.model.Routes
import com.leandrolcd.dogedexmvvm.ui.authentication.utilities.SignUpScreen
import com.leandrolcd.dogedexmvvm.ui.authentication.utilities.StartScreen
import com.leandrolcd.dogedexmvvm.ui.dogdetail.DogDetailScreen
import com.leandrolcd.dogedexmvvm.ui.ui.theme.DogedexMVVMTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class LoginComposeActivity : ComponentActivity() {


    private val loginViewModel: LoginComposeViewModel by viewModels()

    private val requiredPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("tag", "permiso consedido")
        } else {
            Log.i("tag", "permisno denegado")
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
        requestCameraPermission()
    }


    //region NavigationRoutes
    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    fun OnRegisterRoutes() {
        val navigationController = rememberNavController()

        NavHost(navController = navigationController, startDestination = Routes.ScreenLoading.route) {
            composable(route = Routes.ScreenLoading.route) {
                StartScreen(navigationController)
            }
            composable(route = Routes.ScreenLogin.route) {
                LoginScreen(navigationController, viewModel = loginViewModel){
                    onLoginWithGoogleClicked()
                }
            }
            composable(route = Routes.ScreenSignUp.route) { SignUpScreen(navigationController) }

            composable(route = Routes.ScreenDogList.route) { DogListScreen(navigationController) }


            composable(
                Routes.ScreenDogDetail.route,
                arguments = listOf(navArgument("isRecognition") { type = NavType.BoolType },
                    navArgument("dogId") { defaultValue = "" })
            ) {
                    backStackEntry ->
                DogDetailScreen(
                    navigationController = navigationController,
                    backStackEntry.arguments?.getBoolean("isRecognition", false)!!,
                    backStackEntry.arguments?.getString("dogId")!!
                )
            }
        }

    }


    //endregion
    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("TAG", "requestCameraPermission: permiso otorgado")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            ) -> Log.i("tag", "requestCameraPermission: Show permission")

            else -> requiredPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

    }
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

        // There are no request codes
        val data: Intent? = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {

            val account = task.getResult(ApiException::class.java)!!
            loginViewModel.onLoginWithGoogle(account.idToken!!)
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
