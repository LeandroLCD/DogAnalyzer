@file:OptIn(ExperimentalMaterialApi::class)

package com.leandrolcd.doganalyzer.ui.doglist

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.*
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.MainActivity.Companion.MAXADSREWARD
import com.leandrolcd.doganalyzer.ui.admob.BannerAdView
import com.leandrolcd.doganalyzer.ui.authentication.utilities.ErrorLoginScreen
import com.leandrolcd.doganalyzer.ui.authentication.utilities.LoadingScreen
import com.leandrolcd.doganalyzer.ui.camera.ButtonCamera
import com.leandrolcd.doganalyzer.ui.camera.CameraCompose
import com.leandrolcd.doganalyzer.ui.dogdetail.TextDescription
import com.leandrolcd.doganalyzer.ui.dogdetail.TitleDialog
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.ui.theme.Marron
import com.leandrolcd.doganalyzer.ui.ui.theme.Purple500
import com.leandrolcd.doganalyzer.ui.ui.theme.primaryColor
import com.leandrolcd.doganalyzer.ui.ui.theme.textColor
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.R)
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
fun DogListScreen(
    navHostController: NavHostController,
    viewModel: DogListViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = LocalContext.current as Activity
    viewModel.navHostController = navHostController

    val uiState by produceState<UiStatus<List<Dog>>>(
        initialValue = UiStatus.Loading(),
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.uiStatus.collect { value = it }
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

    when (uiState) {
        is UiStatus.Error -> {
            ErrorLoginScreen(message = (uiState as UiStatus.Error<List<Dog>>).message) {
                activity.finish()
            }
        }
        is UiStatus.Loading -> {
            LoadingScreen()
        }
        is UiStatus.Success -> {
            DogListContent(
                navHostController,
                dogList = (uiState as UiStatus.Success<List<Dog>>).data,
                croquettesCount = croquettesCount,
                viewModel = viewModel
            ) {
                navHostController.navigate(
                    Routes.ScreenDogDetail.routeName(
                        false,
                        listOf(DogRecognition(id = it.mlId, 100f))
                    )
                )
            }
        }
        else -> {}
    }

}

@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.R)
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
fun DogListContent(
    navHostController: NavHostController, viewModel: DogListViewModel,
    dogList: List<Dog>,
    croquettesCount: Int,
    onItemSelected: (Dog) -> Unit

) {
    var index by remember {
        mutableStateOf(0)
    }
    var isBanner by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    var isVisibleStore by remember { mutableStateOf(false) }
    var dogSelect by remember { mutableStateOf<Dog?>(null) }


    val context = LocalContext.current
    Scaffold(
        topBar = {
            MyTopBar(croquettes = croquettesCount, onClickCroquettes = { isVisibleStore = true }) {
                navHostController.navigate(Routes.ScreenProfile.route)
            }

        },
        bottomBar = {
            MyBottomBar(index) {
                index = it
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (index == 1) {
                val dog = viewModel.dogRecognition.value.first()
                ButtonCamera(dog.confidence > 60) {
                    navHostController.navigate(
                        Routes.ScreenDogDetail.routeName(
                            true,
                            viewModel.dogRecognition.value
                        )
                    )
                }
            }

        },
        isFloatingActionButtonDocked = true
    ) {
        if (index == 0) {

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                DogCollection(
                    dogList,
                    modifier = Modifier.padding(bottom = if (isBanner) 50.dp else 0.dp),
                    onUnCoverRequest = { select ->
                        isVisible = true
                        dogSelect = select
                    }
                ) { dog ->
                    onItemSelected(dog)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(50.dp)
                        .background(Color.Transparent),
                    horizontalArrangement = Arrangement.Center,

                    ) {
                    BannerAdView {
                        isBanner = true
                    }

                }


            }

        } else {
            CameraCompose()

        }

        StoreDialog(
            isVisible = isVisible, viewModel = viewModel,
            onDismissRequest = { isVisible = false },
            arrayCroquettes = arrayOf(dogSelect?.croquettes ?: 0, croquettesCount)
        ) {
            if (dogSelect != null && croquettesCount > (dogSelect?.croquettes ?: 0)) {
                viewModel.onUnCoverRequest(dogSelect!!.mlId)
            } else {
                Toast.makeText(context, "Insufficient Croquettes", Toast.LENGTH_LONG).show()
            }
        }

        CroquettesDialog(
            isVisible = isVisibleStore,
            viewModel = viewModel,
            onDismissRequest = { isVisibleStore = false })

    }
}

@ExperimentalMaterialApi
@Composable
fun DogCollection(
    dogList: List<Dog>,
    modifier: Modifier,
    onUnCoverRequest: (Dog) -> Unit,
    onItemSelected: (Dog) -> Unit
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .background(Color.Transparent),
        content = {
            items(dogList) {
                ItemDog(it, onItemSelected, onUnCoverRequest)
            }
        },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )

}

@ExperimentalMaterialApi
@Composable
fun ItemDog(dog: Dog, onItemSelected: (Dog) -> Unit, onUnCoverRequest: (Dog) -> Unit) =
    if (dog.inCollection) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .height(100.dp)
                .width(100.dp),
            onClick = {
                onItemSelected(dog)
            },
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            SubcomposeAsyncImage(contentDescription = "${dog.name} image",
                modifier = Modifier.background(Color.White),
                model = dog.imageUrl,
                loading = {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(45.dp)
                                .width(45.dp)
                        )
                    }
                })
        }

    } else {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .height(100.dp)
                .width(100.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 16.dp,
            onClick = { onUnCoverRequest(dog) }
        ) {

            Text(text = "${dog.index}", Modifier.padding(horizontal = 8.dp))
            DogAnimation(R.raw.error_dog)

        }
    }

@Composable
fun DogAnimation(rawRes: Int, modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun MyTopBar(croquettes: Int = 0, onClickCroquettes: () -> Unit, onClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.Title)) },
        backgroundColor = primaryColor,
        contentColor = Color.White,
        elevation = 8.dp,
        actions = {

            CroquettesIcon(croquettes) {
                onClickCroquettes()
            }

            IconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.logout)
                )
            }
        }
    )
}

@Composable
fun CroquettesIcon(croquettes: Int = 0, onClickCroquettes: () -> Unit) {

    IconButton(onClick = { onClickCroquettes() }) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.croquette), contentDescription = "",
                tint = Marron, modifier = Modifier.size(20.dp)
            )
            Text(text = croquettes.toString(), modifier = Modifier.padding(horizontal = 8.dp))
    }
    }
}

@Composable
fun MyBottomBar(index: Int, onClickSelect: (Int) -> Unit) {
    BottomAppBar(
        backgroundColor = primaryColor,
        contentColor = Color.White,
        cutoutShape = CircleShape,
        elevation = 8.dp
    ) {
        BottomNavigationItem(selected = index == 0, onClick = { onClickSelect(0) }, icon = {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = stringResource(R.string.dog)
            )
        }, label = {
            Text(
                text = stringResource(R.string.home)
            )
        }, unselectedContentColor = textColor)
        BottomNavigationItem(selected = index == 1, onClick = { onClickSelect(1) }, icon = {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = stringResource(R.string.camera)
            )
        }, label = {
            Text(
                text = "Camera"
            )
        }, unselectedContentColor = textColor)
    }
}


@ExperimentalCoilApi
@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@Composable
fun StoreDialog(
    isVisible: Boolean,
    viewModel: DogListViewModel,
    arrayCroquettes: Array<Int>,
    onDismissRequest: () -> Unit,
    onUnCoverRequest: () -> Unit
) {
    val context = LocalContext.current

    if (isVisible) {
        AlertDialog(onDismissRequest = { onDismissRequest() },
            title = {
                TitleDialog(text = stringResource(R.string.dog_store))
            },
            text = {
                Box{
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextDescription(
                            textSp = stringResource(
                                id = R.string.store_description_Es,
                                formatArgs = arrayCroquettes
                            ),
                            textEn = stringResource(
                                R.string.store_description_En,
                                formatArgs = arrayCroquettes
                            ),
                            fontSize = 16.sp, textAlign = TextAlign.Justify,
                            color = if (isSystemInDarkTheme()) {
                                Color.White
                            } else {
                                Color.Black
                            }
                        )
                        if (viewModel.counterAdReward <= MAXADSREWARD) {
                            PlayAdReward {
                                viewModel.onRewardShow(context as Activity)
                            }
                        }else{
                            DogAnimation(rawRes = R.raw.dalmata, Modifier.size(100.dp))
                        }
                    }

                }
            },
            confirmButton = {
                ButtonDialog(
                    text = stringResource(R.string.uncover)
                ) {
                    onUnCoverRequest()
                    onDismissRequest()
                }

            },
            dismissButton = {
                ButtonDialog(text = stringResource(R.string.cancel)) {
                    onDismissRequest()
                }
            }
        )
    }
}

@Composable
fun ButtonDialog(text: String, onClick: () -> Unit) {
    val color = Purple500
    TextButton(onClick = {
        onClick()
    }, colors = ButtonDefaults.textButtonColors(contentColor = color)) {
        Text(text = text, fontWeight = FontWeight.ExtraBold)
    }
}

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CroquettesDialog(
    isVisible: Boolean,
    viewModel: DogListViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    if (isVisible) {
        AlertDialog(onDismissRequest = { onDismissRequest() },
            title = {
                TitleDialog(text = stringResource(R.string.dog_store))
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextDescription(
                        textSp = stringResource(
                            id = R.string.dog_store_message_ES
                        ),
                        textEn = stringResource(
                            R.string.dog_store_message_En
                        ),
                        fontSize = 16.sp, textAlign = TextAlign.Justify,
                        color = if (isSystemInDarkTheme()) {
                            Color.White
                        } else {
                            Color.Black
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (viewModel.counterAdReward <= MAXADSREWARD) {
                        PlayAdReward {
                            viewModel.onRewardShow(context as Activity)
                        }
                    }else{
                        DogAnimation(rawRes = R.raw.dalmata, Modifier.size(100.dp))
                    }

                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onDismissRequest()
                }, colors = ButtonDefaults.textButtonColors(contentColor = Purple500)) {
                    Text(text = stringResource(R.string.cancel), fontWeight = FontWeight.Bold)
                }
            }
        )

    }

}

@Composable
fun PlayAdReward(onRewardRequest: () -> Unit) {
    Box(
        Modifier
            .size(120.dp, 100.dp), contentAlignment = Alignment.Center
    ) {
        DogAnimation(rawRes = R.raw.play_ads, Modifier.clickable { onRewardRequest() })
    }
}
