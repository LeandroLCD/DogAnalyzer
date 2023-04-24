package com.leandrolcd.dogedexmvvm.ui.doglist

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraIndoor
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.*
import com.leandrolcd.dogedexmvvm.R
import com.leandrolcd.dogedexmvvm.ui.authentication.utilities.LoadingScreen
import com.leandrolcd.dogedexmvvm.ui.camera.CameraCompose
import com.leandrolcd.dogedexmvvm.ui.model.Dog
import com.leandrolcd.dogedexmvvm.ui.model.DogRecognition
import com.leandrolcd.dogedexmvvm.ui.model.Routes
import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import com.leandrolcd.dogedexmvvm.ui.ui.theme.backGroudColor
import com.leandrolcd.dogedexmvvm.ui.ui.theme.primaryColor

@RequiresApi(Build.VERSION_CODES.R)
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
fun DogListScreen(
    navHostController: NavHostController,
    viewModel: DogListViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val uiState by produceState<UiStatus<List<Dog>>>(
        initialValue = UiStatus.Loading(),
        key1 = lifecycle,
        key2 = viewModel) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED){
            viewModel.uiStatus.collect{value = it}
        }
    }

    when (uiState) {
        is UiStatus.Error -> { TODO()}
        is UiStatus.Loaded -> {

        }
        is UiStatus.Loading -> {
            LoadingScreen()
        }
        is UiStatus.Success -> {
            DogListContent(navHostController, dogList = (uiState as UiStatus.Success<List<Dog>>).data, viewModel = viewModel ) {
                navHostController.navigate(
                    Routes.ScreenDogDetail.routeName(false, listOf(DogRecognition(id=it.mlId,100f)) )
                )
            }
        }
    }

}
@RequiresApi(Build.VERSION_CODES.R)
@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun DogListContent(navHostController: NavHostController, viewModel: DogListViewModel,
    dogList: List<Dog>,
    onItemSelected: (Dog) -> Unit,

    ) {
    var index by remember{
        mutableStateOf(0)
    }
    Scaffold(topBar = {
        MyTopBar()
    },
    bottomBar = {
        MyBottomBar(index){
            index = it
        }
    }) {

        if(index == 0){
            DogCollection(dogList, onItemSelected)
        }else{
            CameraCompose() {
                navHostController.navigate(Routes.ScreenDogDetail.routeName(
                    true,

                    viewModel.dogRecognition.value
                ))
            }
        }

    }
}

@ExperimentalMaterialApi
@Composable
fun DogCollection(dogList: List<Dog>, onItemSelected: (Dog) -> Unit) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxSize(),
            content = {
                items(dogList) {
                    ItemDog(it, onItemSelected)
                }
            },
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp))

}
@ExperimentalMaterialApi
@Composable
fun ItemDog(dog: Dog, onItemSelected: (Dog) -> Unit) {
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
            SubcomposeAsyncImage(contentDescription = "Dog Image",
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

            Text(text = "# ${dog.index}", Modifier.padding(horizontal = 8.dp))
            DogAnimation()

        }
    }

}
@Composable
fun DogAnimation(){
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
fun MyTopBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.title_top_bar)) },
        backgroundColor = primaryColor,
        contentColor = Color.Black,
        elevation = 8.dp

    )
}
@Composable
fun MyBottomBar(index:Int, onClickSelect:(Int)->Unit) {
    BottomAppBar(backgroundColor = backGroudColor,
        contentColor = Color.Black,
        elevation = 8.dp) {
        BottomNavigationItem(selected = index == 0, onClick = { onClickSelect(0) }, icon = {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Dog"
            )
        }, label = {
            Text(
                text = "Home"
            )
        })
        BottomNavigationItem(selected = index == 1, onClick = { onClickSelect(1) }, icon = {
            Icon(
                imageVector = Icons.Default.CameraIndoor,
                contentDescription = "Camera"
            )
        }, label = {
            Text(
                text = "Camera"
            )
        })
    }
}
