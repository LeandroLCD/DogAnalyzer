package com.leandrolcd.dogedexmvvm.ui.authentication.utilities

import android.app.Activity
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.airbnb.lottie.compose.*
import com.leandrolcd.dogedexmvvm.R
import com.leandrolcd.dogedexmvvm.ui.authentication.LoginComposeViewModel
import com.leandrolcd.dogedexmvvm.ui.model.Routes
import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import com.leandrolcd.dogedexmvvm.ui.ui.theme.primaryColor
import kotlinx.coroutines.delay
import kotlin.math.floor


@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    navigationController: NavHostController,
    viewModel: LoginComposeViewModel,
    onLoginWithGoogleClicked: () -> Unit
) {

    when(val status =viewModel.uiStatus.value){
        is UiStatus.Error -> {
            ErrorLoginScreen(status.message){viewModel.onTryAgain()}
        }
        is UiStatus.Loaded -> {
            LoginContent(viewModel, navigationController, onLoginWithGoogleClicked)
        }
        is UiStatus.Loading -> {
            LoadingScreen()
        }
        is UiStatus.Success -> {
            LaunchedEffect(true) {
                navigationController.popBackStack()
                navigationController.navigate(Routes.ScreenDogList.route)
                viewModel.onTryAgain()
            }
        }
    }


}


@Composable
fun GifScreen(drawable: Int) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Log.d("LoadingScreen", "LoadingScreen: $SDK_INT")
        val imageLoader = ImageLoader.Builder(LocalContext.current)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(
                painter = rememberAsyncImagePainter(drawable, imageLoader),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                )
            LinearProgressIndicator()
            if(SDK_INT<28){
                LinearProgressIndicator()
            }
        }

    }
}

@Composable
fun LoginContent(
    viewModel: LoginComposeViewModel,
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

@Composable
fun MyCardLogin(
    viewModel: LoginComposeViewModel,
    navigationController: NavHostController,
    onLoginWithGoogleClicked: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = floor(configuration.screenWidthDp * 0.85).toInt()
    val screenHeightDp = floor(configuration.screenHeightDp * 0.96).toInt()
    var isPlaying by remember {
        mutableStateOf(true)
    }
    Card(
        shape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp),

        modifier = Modifier
            .width(screenWidthDp.dp)
            .height(screenHeightDp.dp)
    ) {

        Huellas()
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            HeaderLogin(Modifier.weight(1f), isPlaying){
                isPlaying = false
            }
            BodyLogin(Modifier.weight(1f), viewModel){
                Log.d("TAG", "isPlaying: true")
                isPlaying = true
            }
            FooterLogin(Modifier.weight(1f), viewModel, navigationController, onLoginWithGoogleClicked)
        }

    }
}

@Composable
fun HeaderLogin(modifier: Modifier, isPlaying: Boolean,stopPlaying:()->Unit) {
    val activity = LocalContext.current as Activity
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { activity.finish() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Exits App"
                )
            }


        }


        LogoAnimationView(isPlaying){
            stopPlaying()
        }



    }
}



@Composable
fun FooterLogin(
    modifier: Modifier = Modifier,
    viewModel: LoginComposeViewModel,
    navigationController: NavHostController,
    onLoginWithGoogleClicked: () -> Unit
) {
    Box(contentAlignment = BottomCenter) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            MyButton(label = "Sign In", viewModel.isButtonEnabled.value) {
                viewModel.onLoginClicked()
            }
            LoginWithGoogle(Modifier) {
                onLoginWithGoogleClicked()
            }
            SignUp(Modifier) {
                navigationController.navigate(Routes.ScreenSignUp.route)
            }
        }
    }

}

@Composable
fun BodyLogin(modifier: Modifier = Modifier, viewModel: LoginComposeViewModel,onComplete:()->Unit) {
    val email = viewModel.email.value
    val password = viewModel.password.value

    Column(
        modifier
            .padding(16.dp)
    ) {

        EmailFields(
            Modifier.fillMaxWidth(),
            label = "Email",
            text = email,
            icons = { MyIcon(Icons.Default.Email) },
            onValueChange = {
                viewModel.onLoginChange(it, password)
            },
            onComplete = {onComplete()}
        )
        PasswordFields(
            label = "Password",
            text = password,
            icons = { MyIcon(Icons.Outlined.Key) },
            onValueChange = {
                viewModel.onLoginChange(email, it)
            },
            visualTransformation = PasswordVisualTransformation('*'),
            onComplete = {onComplete()}

        )
        ForgotPassword(modifier = Modifier.align(Alignment.End))
    }
}

@Composable
fun ErrorLoginScreen(message: String, onTryAgain: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ic_dog),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
        Text(text = message, softWrap = true)
        FloatingActionButton(onClick = { onTryAgain() }) {
            Icon(imageVector = Icons.Outlined.SyncProblem, contentDescription = "Try Again")
        }
    }
}


//region Body


//region Controls Body


@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = "Forgot Password?",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4Ea8E9),
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
                    .align(CenterVertically))
            Text("Continuar con")
            MyDivider(
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .align(CenterVertically))

        }
        Image(painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "LoginWithGoogle",
            modifier = modifier
                .width(40.dp)
                .height(40.dp)
                .padding(bottom = 16.dp)
                .clickable {
                    onClickAction()
                }
        )
    }


}

@Composable
fun MyDivider(modifier: Modifier = Modifier) {
    Divider(
        color = Color.Gray,
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
        Text("Don't have an account?", fontSize = 12.sp, color = Color.Black)
        Text(
            "Sign Up",
            Modifier
                .padding(start = 8.dp)
                .clickable { onSignUpClicked() },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

    }
}
//endregion
