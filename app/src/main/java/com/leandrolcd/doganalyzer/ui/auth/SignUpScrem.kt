package com.leandrolcd.doganalyzer.ui.auth

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.auth.controls.EmailFields
import com.leandrolcd.doganalyzer.ui.auth.controls.Huellas
import com.leandrolcd.doganalyzer.ui.auth.controls.LoadingScreen
import com.leandrolcd.doganalyzer.ui.auth.controls.LogoAnimationView
import com.leandrolcd.doganalyzer.ui.auth.controls.MyButton
import com.leandrolcd.doganalyzer.ui.auth.controls.MyIcon
import com.leandrolcd.doganalyzer.ui.auth.controls.PasswordFields
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.states.LoginUiState
import com.leandrolcd.doganalyzer.ui.theme.primaryColor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.floor


@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@Composable
fun SignUpScreen(
    navigationController: NavHostController,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    when (val status = viewModel.uiStatus.value) {
        is LoginUiState.Error -> ErrorLoginScreen(message = status.message) {
            viewModel.onTryAgain()
        }
        is LoginUiState.Loaded -> {

            CardSignUp(viewModel, navigationController)
        }
        is LoginUiState.Loading -> LoadingScreen()
        is LoginUiState.Success -> {
            LaunchedEffect(true) {
                navigationController.popBackStack()
                navigationController.navigate(Routes.ScreenDogList.route)
            }

        }
    }

    OnBackToPressedToProfile(navigationController)
}
@SuppressLint("SuspiciousIndentation")
@Composable
fun OnBackToPressedToProfile(navController: NavHostController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current

            DisposableEffect(Unit) {
                val callback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // Realiza la navegación a la pantalla A
                        navController.navigate(Routes.ScreenProfile.route) {
                            // Limpiar la pila de navegación
                            popUpTo(Routes.ScreenDogList.route) {
                                saveState = false
                            }

                        }
                    }
                }

                backDispatcher?.onBackPressedDispatcher?.addCallback(callback)

                onDispose {
                    callback.remove()
                }
            }


}


@ExperimentalCoroutinesApi
@Composable
fun CardSignUp(viewModel: SignUpViewModel, navigationController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = floor(configuration.screenWidthDp * 0.85).toInt()
    val screenHeightDp = floor(configuration.screenHeightDp * 0.96).toInt()
    var isPlaying by remember {
        mutableStateOf(true)
    }
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor)

    ) {
        Card(
            shape = RoundedCornerShape(topStart = 30.dp, bottomStart = 30.dp),
            modifier = Modifier
                .width(screenWidthDp.dp)
                .height(screenHeightDp.dp),

        ) {
            Box{
                Huellas()

                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {

                    MyHeader(Modifier, navigationController, isPlaying) {
                        isPlaying = false
                    }
                    MyBody(viewModel, Modifier.weight(1f))
                    MyFooter(viewModel, Modifier)
                }
            }


        }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MyFooter(viewModel: SignUpViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        MyButton(
            stringResource(R.string.sign_up),
            viewModel.isButtonEnabled.value
        ) { viewModel.onSignUpClicked() }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MyBody(viewModel: SignUpViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = viewModel.email.value
        val password = viewModel.password.value
        val passwordConfirmation = viewModel.passwordConfirmation.value
        val enableButton = viewModel.isButtonEnabled.value
        var isPlaying: Boolean by remember {
            mutableStateOf(true)
        }
        EmailFields(
            label = stringResource(R.string.email),
            text = email,
            icons = { MyIcon(Icons.Default.Email) },
            onValueChange = { viewModel.onSignUpChanged(it, password, passwordConfirmation) },
            onComplete = { isPlaying = false })
        PasswordFields(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.password),
            text = password,
            icons = { MyIcon(Icons.Outlined.Key) },
            onValueChange = { viewModel.onSignUpChanged(email, it, passwordConfirmation) },
            action = ImeAction.Next,
            visualTransformation = PasswordVisualTransformation('*'),
            onComplete = { isPlaying = false }
        )
        val keyboardController = LocalSoftwareKeyboardController.current
        PasswordFields(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.confirm_password),
            text = passwordConfirmation,
            icons = { MyIcon(Icons.Outlined.Key) },
            onValueChange = { viewModel.onSignUpChanged(email, password, it) },
            visualTransformation = PasswordVisualTransformation('*'),
            onComplete = { isPlaying = false
                keyboardController?.hide()}
        )
        if (!enableButton) {
            Text(
                text = stringResource(R.string.password_requirements),
                color = if(isSystemInDarkTheme()){
                             Color.White
                        }else{
                             Color.Red
                     },
                fontSize = 10.sp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    stringResource(R.string.valid),
                    tint = Color.Green
                )
                Text(
                    text = stringResource(R.string.valid),
                    color = Color.Green,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 10.sp
                )
            }

        }
    }
}


@Composable
fun MyHeader(
    modifier: Modifier = Modifier,
    navigationController: NavHostController,
    isPlaying: Boolean,
    stopPlaying: () -> Unit
) {
    val activity = LocalContext.current as Activity
    val configuration = LocalConfiguration.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(floor(configuration.screenHeightDp * 0.3).toInt().dp)
    ) {
        Row(modifier) {
            IconButton(
                onClick = {
                    navigationController.navigate(Routes.ScreenLogin.route){
                        popUpTo(Routes.ScreenLogin.route){
                            inclusive = true
                        }
                    }
                }) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIos,
                    contentDescription = stringResource(R.string.bach_to_login),
                    tint = primaryColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { activity.finish() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.exit_app),
                    tint = primaryColor
                )
            }

        }
        LogoAnimationView(isPlaying) {
            stopPlaying()
        }


    }
}