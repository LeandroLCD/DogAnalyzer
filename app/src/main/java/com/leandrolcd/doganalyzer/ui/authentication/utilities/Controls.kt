package com.leandrolcd.doganalyzer.ui.authentication.utilities

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.authentication.LoginComposeViewModel
import com.leandrolcd.doganalyzer.ui.doglist.DogAnimation
import com.leandrolcd.doganalyzer.ui.ui.theme.backGroupTextField
import com.leandrolcd.doganalyzer.ui.ui.theme.colorGray
import com.leandrolcd.doganalyzer.ui.ui.theme.primaryColor
import com.leandrolcd.doganalyzer.ui.ui.theme.textColor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlin.math.floor

@Composable
fun MyButton(label: String, isButtonEnabled: Boolean, onClickSignUp: () -> Unit) {

    Button(
        onClick = { onClickSignUp() },
        modifier = Modifier.width(150.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = primaryColor,
            contentColor = textColor,
            disabledContentColor = backGroupTextField
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp,
            disabledElevation = 4.dp
        ),
        enabled = isButtonEnabled
    ) {
        Text(text = label)
    }
}

@Composable
fun Huellas() {
    val configuration = LocalConfiguration.current
    Icon(
        painter = painterResource(id = R.drawable.huellas),
        contentDescription = "",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = floor(configuration.screenHeightDp * 0.3).toInt().dp),
        tint = colorGray
    )
}


@Composable
fun MyIcon(image: ImageVector) {
    Icon(imageVector = image, contentDescription = "Icons", tint = primaryColor)
}

@Composable
fun EmailFields(
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    icons: @Composable () -> Unit,
    onComplete: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.fillMaxWidth()
) {
    TextField(
        placeholder = { Text(text = label, color = textColor) }, value = text,
        onValueChange = {
            val pattern = "^\\S*$".toRegex()
            if (pattern.matches(it)) {
                onValueChange(it)
            }
        },
        leadingIcon = icons,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onDone = {
            onComplete()
        }),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = textColor,
            disabledLabelColor = Color.Gray,
            errorBorderColor = Color.Red,
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = Color.Transparent,
            backgroundColor = backGroupTextField.toAlpha(0.6f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(bottom = 12.dp)
            .onFocusEvent {
                onComplete()

            }


    )
}

private fun Color.toAlpha(alpha: Float): Color {
    return Color(this.red, this.green, this.blue, alpha)
}

@Composable
fun PasswordFields(

    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    icons: @Composable () -> Unit,
    action: ImeAction = ImeAction.Done,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onComplete: () -> Unit
) {
    var passwordVisibility by remember {
        mutableStateOf(false)
    }


    TextField(
        placeholder = { Text(text = label, color = textColor) },
        value = text,
        onValueChange = {
            val pattern = "^\\S*$".toRegex()
            if (pattern.matches(it)) {
                onValueChange(it)
            }

        },
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            visualTransformation
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = action
        ),
        keyboardActions = KeyboardActions(onDone = { onComplete() }),
        leadingIcon = icons,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = textColor,
            disabledLabelColor = Color.Gray,
            errorBorderColor = Color.Red,
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = Color.Transparent,
            backgroundColor = backGroupTextField.toAlpha(0.6f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(bottom = 12.dp)
            .onFocusEvent {
                onComplete()

            },
        trailingIcon = {
            val img = if (passwordVisibility) {
                Icons.Outlined.Visibility
            } else {
                Icons.Outlined.VisibilityOff
            }
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    imageVector = img,
                    contentDescription = stringResource(R.string.password_visibility)
                )
            }
        },


        )
}

@Composable
fun LogoAnimationView(isPlaying: Boolean, onComplete: () -> Unit) {

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.dalmata))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        restartOnPlay = false
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier.fillMaxSize()
    )
    LaunchedEffect(isPlaying) {
        delay(1500)
        onComplete()
    }

}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.huella))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        LottieAnimation(
            composition,
            progress,
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        )
    }


}

@ExperimentalCoroutinesApi
@Composable
fun StartScreen(
    navController: NavHostController,
    viewModel: LoginComposeViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {

        viewModel.onCheckedUserCurrent(navController)
        delay(2000)

    }

    LoadingScreen()

}


@Composable
fun ScreenError(error: String) {
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(colorGray)
    ) {
        val (header, body, button) = createRefs()
        val topGuide = createGuidelineFromTop(0.35f)


        Box(modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .fillMaxHeight(0.70f)
            .constrainAs(body) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(36.dp, 36.dp, 0.dp, 0.dp),
                color = Color.White,
                elevation = 8.dp,
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Text(text = error, Modifier.padding(top = 46.dp))
                }

            }
        }
        Box(
            modifier = Modifier
                .size(220.dp)
                .constrainAs(header) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(topGuide)
                }, contentAlignment = Alignment.BottomCenter
        ) {
            DogAnimation()
        }
        Button(
            onClick = { },
            modifier = Modifier.constrainAs(button) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            colors = ButtonDefaults.buttonColors(primaryColor)
        ) {
            Text(text = stringResource(R.string.ok))
        }

    }
}
