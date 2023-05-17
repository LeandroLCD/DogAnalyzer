package com.leandrolcd.doganalyzer.ui.authentication.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.airbnb.lottie.compose.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.leandrolcd.doganalyzer.BuildConfig
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.MainActivity.Companion.MAXADSREWARD
import com.leandrolcd.doganalyzer.ui.authentication.LoginComposeViewModel
import com.leandrolcd.doganalyzer.ui.dogdetail.TitleDialog
import com.leandrolcd.doganalyzer.ui.doglist.ButtonDialog
import com.leandrolcd.doganalyzer.ui.ui.theme.*
import com.leandrolcd.doganalyzer.ui.utilits.LANGUAGE
import com.leandrolcd.doganalyzer.ui.utilits.isSpanish
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
            contentColor = Color.White,
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
        tint = if(isSystemInDarkTheme()) {
            Color.Black
        }else{
            colorGray
        }
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
            backgroundColor = backGroupTextField.toAlpha(0.8f)
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
            backgroundColor = backGroupTextField.toAlpha(0.8f)
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

@ExperimentalCoilApi
@ExperimentalMaterialApi
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ResourceType")
@ExperimentalCoroutinesApi
@Composable
fun StartScreen(
    navController: NavHostController,
    remoteConfig: FirebaseRemoteConfig,
    viewModel: LoginComposeViewModel = hiltViewModel()
) {
    var urlPlayStore by remember {
        mutableStateOf("")
    }
    var isRequireUpdate by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    LaunchedEffect(true) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val versionMinima = remoteConfig.getString("version_minima")
                urlPlayStore = remoteConfig.getString("url_play_store")
                MAXADSREWARD = remoteConfig.getLong("max_ads_reward").toInt()
                val compare = versionMinima.compareTo(BuildConfig.VERSION_NAME)
                if (compare > 0) {
                    val defaults = hashMapOf<String, Any>(
                        "version_minima" to versionMinima,
                        "url_play_store" to urlPlayStore,
                        "max_ads_reward" to MAXADSREWARD
                    )
                    val inputStream =
                        context.resources.openRawResource(R.xml.remote_config_defaults)
                    val xmlBytes = inputStream.readBytes()
                    val outputStream =
                        context.openFileOutput("remote_config_defaults.xml", Context.MODE_PRIVATE)
                    outputStream.write(xmlBytes)
                    for ((key, value) in defaults) {
                        outputStream.write("<entry>\n<key>$key</key>\n<value>$value</value>\n</entry>\n".toByteArray())
                    }
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()
                    isRequireUpdate = true
                } else {
                    viewModel.onCheckedUserCurrent(navController)
                }
            } else {
                viewModel.onCheckedUserCurrent(navController)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        LoadingScreen()
        UpdateDialog(isVisible = isRequireUpdate, onUpdateClicked = {

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlPlayStore))
            startActivity(context, intent, null)

        })
    }


}

@Composable
fun UpdateDialog(isVisible: Boolean, onUpdateClicked: () -> Unit) {
    if (isVisible) {
        AlertDialog(onDismissRequest = {},
            title = {
                TitleDialog(text = stringResource(R.string.app_name))
            },
            text = {
                Text(
                    text = if (LANGUAGE.isSpanish()) {
                        stringResource(R.string.update_require_es)
                    } else {
                        stringResource(R.string.update_require_en)
                    },
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                ButtonDialog(text = stringResource(R.string.update)) {
                    onUpdateClicked()
                }
            }
        )
    }

}



