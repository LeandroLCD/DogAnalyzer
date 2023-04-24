package com.leandrolcd.dogedexmvvm.ui.authentication.utilities

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.leandrolcd.dogedexmvvm.R
import com.leandrolcd.dogedexmvvm.ui.model.Routes
import com.leandrolcd.dogedexmvvm.ui.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.floor

@Composable
fun MyButton(label: String, isButtonEnabled: Boolean, onClickSignUp: () -> Unit) {

    Button(
        onClick = { onClickSignUp() },
        modifier = Modifier.width(150.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = backGroudColor,
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
fun Huellas()
{
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
    Icon(imageVector = image, contentDescription = "Icons")
}

@Composable
fun EmailFields(
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    icons: @Composable () -> Unit,
    onComplete:()->Unit
) {
    TextField(
        placeholder = { Text(text = label, color = textColor) }, value = text,
        onValueChange = { onValueChange(it) },
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
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    icons: @Composable () -> Unit,
    action:ImeAction = ImeAction.Done,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onComplete:()->Unit
) {
    var passwordVisibility by remember {
        mutableStateOf(false)
    }


    TextField(
        placeholder = { Text(text = label, color = textColor) },
        value = text,
        onValueChange = { onValueChange(it) },
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            visualTransformation
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = action
        ),
        keyboardActions = KeyboardActions(onDone = {onComplete()}),
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
                Icon(imageVector = img, contentDescription = "Visibility Password")
            }
        },


    )
}

@Composable
fun LogoAnimationView(isPlaying:Boolean, onComplete: () -> Unit) {

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
     LaunchedEffect(isPlaying){
         delay(1500)
         onComplete()
     }

}
@Composable
fun LoadingScreen(modifier: Modifier = Modifier){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.huella))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center){

        LottieAnimation(
            composition,
            progress,
            modifier = Modifier
                .width(200.dp)
                .height(200.dp))
    }


}
@Composable
fun StartScreen(navController: NavHostController) {
    LaunchedEffect(true){
        delay(2000)
        navController.popBackStack()
        navController.navigate(Routes.ScreenLogin.route)

    }

    LoadingScreen()

}
