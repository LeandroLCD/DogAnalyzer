package com.leandrolcd.doganalyzer.ui.auth

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
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
import com.leandrolcd.doganalyzer.ui.theme.Purple200
import com.leandrolcd.doganalyzer.ui.theme.primaryColor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.floor


@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable
fun LoginScreen(
    navigationController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginWithGoogleClicked: () -> Unit
) {

    when (val status = viewModel.uiStatus.value) {
        is LoginUiState.Error -> {
            ErrorLoginScreen(status.message) { viewModel.onTryAgain() }
        }
        is LoginUiState.Loaded -> {
            LoginContent(viewModel, navigationController, onLoginWithGoogleClicked)
        }
        is LoginUiState.Loading -> {
            LoadingScreen()
        }
        is LoginUiState.Success -> {
            Log.d("LoginScreen", "Status: Success")
        }
    }


}



@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalCoroutinesApi
@Composable
fun LoginContent(
    viewModel: LoginViewModel,
    navigationController: NavHostController,
    onLoginWithGoogleClicked: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(primaryColor)
    ) {
        MyCardLogin(viewModel, navigationController, onLoginWithGoogleClicked)
    }
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MyCardLogin(
    viewModel: LoginViewModel,
    navigationController: NavHostController,
    onLoginWithGoogleClicked: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = floor(configuration.screenWidthDp * 0.85).toInt()
    val screenHeightDp = floor(configuration.screenHeightDp * 0.96).toInt()
    var isPlaying by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val email = viewModel.email.value
    Card(
        shape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp),

        modifier = Modifier
            .width(screenWidthDp.dp)
            .height(screenHeightDp.dp)
    ) {
        Box{
            Huellas()
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                HeaderLogin(Modifier, isPlaying) {
                    isPlaying = false
                }
                BodyLogin(Modifier.weight(1f), viewModel, onVisibleForgot = {
                    viewModel.onDismissDialog(true)
                }) {
                    isPlaying = true
                }
                FooterLogin(
                    Modifier.weight(1f),
                    viewModel,
                    navigationController,
                    onLoginWithGoogleClicked
                )
                ForgotPasswordDialog(email = email,
                    isVisible = viewModel.isDialogForgot.value,
                    onDismissRequest = { viewModel.onDismissDialog() }, onChangedText = {
                        viewModel.onLoginChange(it, "")
                    }) {
                    viewModel.onForgotPassword(email, context)
                }
            }

        }

    }
}



@Composable
fun HeaderLogin(modifier: Modifier, isPlaying: Boolean, stopPlaying: () -> Unit) {
    val activity = LocalContext.current as Activity
    val configuration = LocalConfiguration.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(floor(configuration.screenHeightDp * 0.3).toInt().dp)
    ) {
        Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
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


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun FooterLogin(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    navigationController: NavHostController,
    onLoginWithGoogleClicked: () -> Unit
) {
    Box(contentAlignment = BottomCenter) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            MyButton(label = stringResource(R.string.sign_in), viewModel.isButtonEnabled.value) {
                viewModel.onLoginClicked(navigationController)
            }
            LoginWithGoogle(Modifier) {
                onLoginWithGoogleClicked()
            }
            SignUp(Modifier) {

                navigationController.navigate(Routes.ScreenSignUp.route){
                    popUpTo(Routes.ScreenLogin.route){
                        inclusive = true
                    }
                }

            }
        }
    }
    OnBackToPressedToProfile(navigationController)
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun BodyLogin(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    onVisibleForgot: () -> Unit,
    onComplete: () -> Unit
) {
    val email = viewModel.email.value
    val password = viewModel.password.value
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier
            .padding(16.dp)
    ) {

        EmailFields(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.email),
            text = email,
            icons = { MyIcon(Icons.Default.Email) },
            onValueChange = {
                viewModel.onLoginChange(it, password)
            },
            onComplete = { onComplete() }
        )
        PasswordFields(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.password),
            text = password,
            icons = { MyIcon(Icons.Outlined.Key) },
            onValueChange = {
                viewModel.onLoginChange(email, it)
            },
            visualTransformation = PasswordVisualTransformation('*'),
            onComplete = {
                onComplete()
                keyboardController?.hide()
            }

        )
        ForgotPassword(modifier = Modifier
            .align(Alignment.End)
            .clickable {
                onVisibleForgot()
            })
    }
}

@ExperimentalMaterial3Api
@Composable
fun ErrorLoginScreen(message: String, onTryAgain: () -> Unit) {

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onTryAgain() }, containerColor = primaryColor) {
            Icon(
                imageVector = Icons.Outlined.SyncProblem,
                contentDescription = stringResource(R.string.try_again),
                tint = Color.White
            )
        }
    }, floatingActionButtonPosition = FabPosition.Center) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.dalmata))
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever,
                isPlaying = true,
                restartOnPlay = true
            )

            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(200.dp)
            )
            Text(text = message, softWrap = true)

        }
    }

}

@Composable
fun ForgotPasswordDialog(
    isVisible: Boolean = false,
    email: String,
    onDismissRequest: () -> Unit,
    onChangedText: (String) -> Unit, sendClicked: () -> Unit
) {
    if (isVisible) {
        AlertDialog(onDismissRequest = { onDismissRequest() },
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Outlined.Pets,
                        contentDescription = stringResource(id = R.string.dog_image)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.forgot_password_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                EmailFields(label = stringResource(id = R.string.email),
                    text = email,
                    onValueChange = {
                        onChangedText(it)
                    },
                    icons = { Icon(imageVector = Icons.Outlined.Email, contentDescription = null) },
                    onComplete = {})
            },
            confirmButton = {
                TextButton(onClick = { sendClicked() }) {
                    Text(text = stringResource(R.string.send))
                }
            }
        )
    }

}

//region Body


//region Controls Body


@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = stringResource(R.string.forgot),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if(isSystemInDarkTheme()) {
            Purple200
        }else{
            primaryColor
        },
        modifier = modifier
    )
}

@Composable
fun LoginWithGoogle(modifier: Modifier, onClickAction: () -> Unit) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(top = 32.dp)
    ) {
        Row(Modifier.padding(vertical = 16.dp)) {

            MyDivider(
                Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .align(CenterVertically)
            )
            Text(stringResource(R.string.continue_with_login), color = if(isSystemInDarkTheme()) {
                Color.White
            }else{
                Color.Black
            })
            MyDivider(
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .align(CenterVertically)
            )

        }
        IconButton(
            onClickAction,
            modifier = modifier
                .width(60.dp)
                .height(60.dp)
                .padding(bottom = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "LoginWithGoogle"
            )
        }
    }


}

@Composable
fun MyDivider(modifier: Modifier = Modifier) {
    Divider(
        color = if(isSystemInDarkTheme()) {
            Color.White
        }else{
            Color.Gray
        },
        thickness = 1.dp,
        modifier = modifier
            .padding(vertical = 4.dp)
    )
}


//endregion

//endregion

//region Footer


@Composable
fun SignUp(
    modifier: Modifier = Modifier,
    onSignUpClicked: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Bottom
    ) {
        Text(stringResource(R.string.dont_have_an_account), fontSize = 12.sp, color = if(isSystemInDarkTheme()) {
            Color.White
        }else{
            Color.Black
        })
        Text(
            stringResource(R.string.sign_up),
            Modifier
                .padding(start = 8.dp)
                .clickable { onSignUpClicked() },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color  = if(isSystemInDarkTheme()) {
                Purple200
            }else{
                primaryColor
            }
        )

    }
}
//endregion
