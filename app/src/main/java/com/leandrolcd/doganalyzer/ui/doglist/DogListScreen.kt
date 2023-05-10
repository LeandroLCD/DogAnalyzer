package com.leandrolcd.doganalyzer.ui.doglist

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.*
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.admob.BannerAdView
import com.leandrolcd.doganalyzer.ui.authentication.utilities.ErrorLoginScreen
import com.leandrolcd.doganalyzer.ui.authentication.utilities.LoadingScreen
import com.leandrolcd.doganalyzer.ui.camera.ButtonCamera
import com.leandrolcd.doganalyzer.ui.camera.CameraCompose
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.ui.theme.primaryColor
import com.leandrolcd.doganalyzer.ui.ui.theme.textColor
import kotlinx.coroutines.ExperimentalCoroutinesApi

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

    when (uiState) {
        is UiStatus.Error -> {
            ErrorLoginScreen(message = (uiState as UiStatus.Error<List<Dog>>).message) {
                activity.finish()

            }
        }
        is UiStatus.Loaded -> {

        }
        is UiStatus.Loading -> {
            LoadingScreen()
        }
        is UiStatus.Success -> {
            DogListContent(
                navHostController,
                dogList = (uiState as UiStatus.Success<List<Dog>>).data,
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
    }

}

@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.R)
@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun DogListContent(
    navHostController: NavHostController, viewModel: DogListViewModel,
    dogList: List<Dog>,
    onItemSelected: (Dog) -> Unit,

    ) {
    var index by remember {
        mutableStateOf(0)
    }
    var isBanner by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MyTopBar { viewModel.logout() }
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
                ButtonCamera(dog.confidence > 75) {
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
        var areaSize by remember { mutableStateOf(Size.Zero) }
        if (index == 0) {

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .onGloballyPositioned { layoutCoordinates ->
                        areaSize = layoutCoordinates.size.toSize()
                    }) {
                DogCollection(
                    dogList,
                    modifier = Modifier.padding(bottom = if (isBanner) 50.dp else 0.dp)
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
                    BannerAdView() {
                        isBanner = true
                    }

                }


            }

        } else {
            CameraCompose()

        }


    }
}

@ExperimentalMaterialApi
@Composable
fun DogCollection(dogList: List<Dog>, modifier: Modifier, onItemSelected: (Dog) -> Unit) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .background(Color.Transparent),
        content = {
            items(dogList) {
                ItemDog(it, onItemSelected)
            }
        },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )

}

@ExperimentalMaterialApi
@Composable
fun ItemDog(dog: Dog, onItemSelected: (Dog) -> Unit) = if (dog.inCollection) {
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
        SubcomposeAsyncImage(contentDescription = stringResource(R.string.dog_image),
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
        elevation = 16.dp
    ) {

        Text(text = "${dog.index}", Modifier.padding(horizontal = 8.dp))
        DogAnimation()

    }
}

@Composable
fun DogAnimation() {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.error_dog))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun MyTopBar(onClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.Title)) },
        backgroundColor = primaryColor,
        contentColor = Color.White,
        elevation = 8.dp,
        actions = {
            IconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = stringResource(R.string.logout)
                )
            }
        }

    )
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
