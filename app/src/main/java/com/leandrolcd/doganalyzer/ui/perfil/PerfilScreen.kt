package com.leandrolcd.doganalyzer.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.authentication.utilities.MyButton
import com.leandrolcd.doganalyzer.ui.authentication.utilities.SemiCircularGauge
import com.leandrolcd.doganalyzer.ui.dogdetail.TextDescription
import com.leandrolcd.doganalyzer.ui.dogdetail.TextTitle
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.model.Values
import com.leandrolcd.doganalyzer.ui.ui.theme.Marron
import com.leandrolcd.doganalyzer.ui.ui.theme.primaryColor
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalTextApi::class)
@ExperimentalCoroutinesApi
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = hiltViewModel()) {
    ConstraintLayout(
        Modifier
            .background(Color.Gray)
            .fillMaxSize()
    ) {
        val (header, body, backButton) = createRefs()
        val topGuide = createGuidelineFromTop(0.35f)

        IconButton(onClick = { navController.popBackStack() },
                    modifier = Modifier.constrainAs(backButton){
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = stringResource(R.string.back),
                tint = primaryColor
            )
        }

        Box(modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .fillMaxHeight(0.70f)
            .constrainAs(body) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            ProfileInformation(viewModel, navController)
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
            ProfileAnimation()
        }


    }
}

@Composable
fun ProfileAnimation() {
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
        modifier = Modifier.fillMaxSize()
    )
}


@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalTextApi
@Composable
fun ProfileInformation(viewModel: ProfileViewModel, navController: NavHostController) {
    val user = viewModel.userCurrent
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val dogCount by produceState(
        initialValue = 0,
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.dogCollection.collect { value = it }
        }
    }
    val croquettesCount by produceState(
        initialValue = 0,
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.croquettes.collect { value = it }
        }
    }


    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(36.dp, 36.dp, 0.dp, 0.dp),
        backgroundColor = Color.White,
        elevation = 8.dp
    ) {
        Column(
            Modifier.padding(top = 36.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val email = if (user?.email?.isNotEmpty() == true) user.email else "Anonymous"
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextTitle(
                    textSp = stringResource(R.string.account_Es),
                    textEn = stringResource(R.string.account_En), color = Color.Black
                )
                TextDescription(
                    textSp = email!!,
                    textEn = email,
                    Modifier.padding(horizontal = 8.dp)
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextTitle(
                    textSp = stringResource(R.string.croquettes_Es),
                    textEn = stringResource(R.string.croquettes_En), color = Color.Black,
                    fontSize = 30.sp,

                    )
                Icon(
                    painter = painterResource(id = R.drawable.croquette),
                    contentDescription = stringResource(id = R.string.croquettes_En),
                    modifier = Modifier.size(50.dp), tint = Marron
                )

                TextDescription(
                    textSp = croquettesCount.toString(),
                    textEn = croquettesCount.toString(),
                    Modifier.padding(horizontal = 8.dp),
                    color = Color.Black,
                    fontSize = 30.sp,
                )
            }

            val value = Values(
                min = 0f,
                max = 120f,
                dogCount.toFloat(),
                unit = stringResource(id = R.string.dogs)
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                SemiCircularGauge(
                    values = value,
                    indicatorColor = primaryColor,
                    strokeIndicator = 40f,
                    modifier = Modifier.size(200.dp)
                )
            }
            Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
                if (user?.isAnonymous == true) {

                    MyButton(
                        label = stringResource(id = R.string.link_account_En),
                        isButtonEnabled = true
                    ) {
                        // navController.popBackStack()
                        navController.navigate(Routes.ScreenLogin.route)
                    }

                } else {
                    MyButton(label = stringResource(id = R.string.logout), isButtonEnabled = true) {
                        //dialog
                        viewModel.logout()
                        navController.popBackStack()
                        navController.navigate(Routes.ScreenLoading.route)

                    }

                }

            }
        }

    }
}

