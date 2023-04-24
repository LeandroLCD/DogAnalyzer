package com.leandrolcd.dogedexmvvm.ui.dogdetail


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.*
import com.leandrolcd.dogedexmvvm.LANGUAGE
import com.leandrolcd.dogedexmvvm.R
import com.leandrolcd.dogedexmvvm.isSpanish
import com.leandrolcd.dogedexmvvm.ui.authentication.utilities.LoadingScreen
import com.leandrolcd.dogedexmvvm.ui.model.Dog
import com.leandrolcd.dogedexmvvm.ui.model.DogRecognition
import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import com.leandrolcd.dogedexmvvm.ui.ui.theme.backGroudColor
import kotlin.math.floor

@ExperimentalCoilApi
@Composable
fun DogDetailScreen(
    navController: NavHostController,
    isRecognition: Boolean,
    dogList: List<DogRecognition>,
    viewModel: DogDetailViewModel = hiltViewModel()
) {

    viewModel.setNavHostController(navController = navController)
    val status = viewModel.uiStatus
    when (status.value) {
        is UiStatus.Error -> {
            Log.d("TAG", "DogError: ${(status.value as UiStatus.Error<*>).message}")
        }
        is UiStatus.Loaded -> {
            viewModel.getDogsById(dogList)
        }
        is UiStatus.Loading -> {
            LoadingScreen(Modifier.fillMaxSize())
        }
        is UiStatus.Success -> {
            DogContent(navController, isRecognition, dogList.first().id, viewModel)
        }
    }

}

@Composable
fun DogContent(
    navController: NavHostController,
    isRecognition: Boolean,
    dogId: String,
    viewModel: DogDetailViewModel
) {

/*
*
val dog = viewModel.dogStatus.value ?: Dog("0", index = 0)
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
         DogDetail(dog = dog, navController)


        FloatingActionButton(
            onClick = {
                if (isRecognition) {
                    viewModel.addDogToUser(dogId)
                } else {
                    navController.popBackStack()
                }


            },
            modifier = Modifier.align(Alignment.BottomCenter),
            backgroundColor = backGroudColor
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "")
        }


    }
    */


    DogScaffold(
        navController,
        isRecognition,
        dogId,
        viewModel
    )


}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DogScaffold(
    navController: NavHostController,
    isRecognition: Boolean,
    dogId: String,
    viewModel: DogDetailViewModel
) {
    var index by remember {
        mutableStateOf(0)
    }
    var isVisible by remember {
        mutableStateOf(false)
    }
    var dog by remember {
        mutableStateOf(viewModel.dogStatus.value?.first())
    }
    Scaffold(
        topBar = {
            MyTopAppBar(navController, isRecognition, dog?.confidence) {
                isVisible = true
            }
        },
        bottomBar = {
            MyBottomBar(index = index) {
                index = it
            }
        },
        floatingActionButton = {
            MyFab() {
                if (isRecognition) {
                    viewModel.addDogToUser(dogId)
                } else {
                    navController.popBackStack()
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            Box(Modifier.padding(innerPadding)) {

                DogDetail(dog = dog!!, index)
            }


        },
        modifier = Modifier.background(Color.White)
    )
    viewModel.dogStatus.value?.let { dogs ->
        DogDialog(isVisible = isVisible, dogList = dogs,
            onDismissRequest = {
                isVisible = false
            }, onSelectItems = {
                dog = it
                isVisible = false
            })
    }
}


@Composable
fun DogDialog(
    isVisible: Boolean = false,
    dogList: List<Dog>,
    onDismissRequest: () -> Unit,
    onSelectItems: (Dog) -> Unit
) {

    if (isVisible) {
        AlertDialog(onDismissRequest = { onDismissRequest() },
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Outlined.Pets,
                        contentDescription = "Imagen"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Top Score", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(content = {
                    items(dogList) {
                        ItemDogR(it, onSelectItems)
                    }
                })
            },
            confirmButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

}

@Composable
fun ItemDogR(dog: Dog, onSelectItems: (Dog) -> Unit) {
    TextButton(onClick = { onSelectItems(dog) }) {
        Row() {
            Text(text = dog.name)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${floor(dog.confidence)} %")

        }
    }

}
@Composable
fun MyFab(imageVector: ImageVector = Icons.Default.Check, onClicked: () -> Unit) {
    FloatingActionButton(
        onClick = { onClicked() },
        backgroundColor = backGroudColor,
        contentColor = Color.Black
    ) {
        Icon(imageVector = imageVector, contentDescription = null)
    }
}
@Composable
fun MyTopAppBar(
    navController: NavHostController,
    isRecognition: Boolean,
    confidence: Float?,
    onClicked: () -> Unit
) {
    Row(Modifier.background(Color.Gray)) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = "back",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (isRecognition && confidence != null) {
            TextButton(onClick = { onClicked() }) {
                TextTitle(
                    textSp = "Confianza $confidence %",
                    textEn = "Confidence $confidence %",
                    fontSize = 16.sp,
                    textColor = Color.White
                )
            }
        }

    }
}
@Composable
fun MyBottomBar(index: Int, onClickSelect: (Int) -> Unit) {
    val currentLocale = LocalConfiguration.current.locales[0]
    val language = currentLocale.language
    BottomAppBar(
        modifier = Modifier.background(Color.White),
        backgroundColor = backGroudColor,
        cutoutShape = CircleShape,
        contentColor = Color.Black,
        elevation = 8.dp
    ) {
        BottomNavigationItem(selected = index == 0, onClick = { onClickSelect(0) }, icon = {
            Icon(
                imageVector = Icons.Outlined.MedicalInformation,
                contentDescription = if (language.isSpanish()) {
                    "Caracteristicas"
                } else {
                    "Characteristics"
                }
            )
        }, label = {
            Text(
                text = if (language.isSpanish()) {
                    "Caracteristicas"
                } else {
                    "Characteristics"
                }
            )
        })
        BottomNavigationItem(selected = index == 1, onClick = { onClickSelect(1) }, icon = {
            Icon(
                imageVector = Icons.Outlined.QuestionMark,
                contentDescription = if (language.isSpanish()) {
                    "Curiosidades"
                } else {
                    "Curiosities"
                }
            )
        }, label = {
            Text(
                text = if (language.isSpanish()) {
                    "Curiosidades"
                } else {
                    "Curiosities"
                }
            )
        })
    }

}
@Composable
fun DogInformation(dog: Dog, modifier: Modifier, index: Int) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(36.dp, 36.dp, 0.dp, 0.dp),
        color = Color.White,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                text = dog.name,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            IconBarra(
                Modifier
                    .height(40.dp)
                    .width(200.dp)
            )
            TextTitle(
                textEn = "${dog.lifeExpectancy} year",
                textSp = "${dog.lifeExpectancy} años"
            )
            TextTitle(
                textEn = "Race: ${dog.race}",
                textSp = "Raza: ${dog.raceEs}"
            )
            if (index == 0) {
                DogCharacteristics(dog, Modifier)
            } else {
                DogCuriosities(textEn = dog.curiosities, textSp = dog.curiositiesEs, Modifier)
            }


        }

    }

}
@Composable
fun DogCuriosities(textEn: String, textSp: String, modifier: Modifier = Modifier) {

    val scroll = rememberScrollState()
    Surface(
        shape = RoundedCornerShape(36.dp, 36.dp, 36.dp, 36.dp),
        border = BorderStroke(1.dp, Color.Black),
        modifier = modifier.padding(16.dp, 16.dp, 16.dp, 48.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            TextTitle(
                textSp = "Curiosidades",
                textEn = "Curiosities",
                fontSize = 24.sp
            )

            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = if (LANGUAGE.isSpanish()) {
                        textSp
                    } else {
                        textEn
                    },
                    fontSize = 16.sp,
                    style = TextStyle(textAlign = TextAlign.Center)
                )
            }
            if (scroll.value < (scroll.maxValue - 10)) {
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    contentDescription = "",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Gray
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.ExpandLess,
                    contentDescription = "",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Gray
                )
            }


        }
    }
}
@Composable
fun DogCharacteristics(dog: Dog, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(36.dp, 36.dp, 36.dp, 36.dp),
        border = BorderStroke(1.dp, Color.Black),
        modifier = modifier.padding(16.dp, 16.dp, 16.dp, 48.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            TextTitle(
                textSp = "Caracteristicas",
                textEn = "Characteristics",
                fontSize = 24.sp,
                Modifier.padding(bottom = 8.dp)
            )
            Log.d("TAG", "DogCharacteristics: $dog")
            TextDescription(
                textSp = dog.temperamentEs,
                textEn = dog.temperament,
                textAlign = TextAlign.Center
            )
            ColumnDetail(
                dog = dog,
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(modifier = modifier.fillMaxHeight(1f))
        }


    }
}

@Composable
private fun TextTitle(
    textSp: String,
    textEn: String,
    fontSize: TextUnit = 16.sp,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    textColor: Color = Color.Black
) {
    Text(
        text = if (LANGUAGE.isSpanish()) {
            textSp
        } else {
            textEn
        },
        fontSize = fontSize,
        color = textColor,
        fontWeight = FontWeight.Medium,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Composable
private fun TextDescription(
    textSp: String,
    textEn: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = if (LANGUAGE.isSpanish()) {
            textSp
        } else {
            textEn
        },
        fontSize = 16.sp,
        color = Color.Black,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Composable
fun DogImage(imageUrl: String, modifier: Modifier) {
    SubcomposeAsyncImage(
        model = imageUrl,
        loading = {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(45.dp)
                        .width(45.dp)
                )
            }
        },
        contentDescription = "Dog image",
        modifier = modifier.fillMaxWidth()
    )
}
@Composable
fun DogDetail(dog: Dog, index: Int) {
    ConstraintLayout(
        Modifier
            .background(Color.Gray)
            .fillMaxSize()
    ) {
        val (header, body) = createRefs()
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
            DogInformation(dog = dog, modifier = Modifier.fillMaxSize(), index = index)
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
            DogImage(dog.imageUrl, Modifier)
        }


    }
}
@Composable
fun ColumnDetail(dog: Dog, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(Modifier.padding(top = 16.dp)) {
            TextTitle(textSp = "Sexo", textEn = "Sex", modifier = Modifier.weight(1f))
            TextTitle(textSp = "Peso", textEn = "Weigh", modifier = Modifier.weight(1f))
            TextTitle(textSp = "Altura", textEn = "Height", modifier = Modifier.weight(1f))
        }
        Row() {
            TextTitle(
                textSp = "Macho", textEn = "Male",
                modifier = Modifier.weight(1f)
            )
            TextDescription(
                textSp = dog.weightMaleEs, textEn = dog.weightMale,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            TextDescription(
                textSp = dog.heightMaleEs, textEn = dog.heightMale,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
        Row() {
            TextTitle(
                textSp = "Hembra", textEn = "Female",
                modifier = Modifier.weight(1f)
            )
            TextDescription(
                textSp = dog.weightFemaleEs, textEn = dog.weightFemale,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            TextDescription(
                textSp = dog.heightFemaleEs, textEn = dog.heightFemale,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

    }
}
@Composable
fun IconBarra(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.electrocardiography))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = true
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier
    )
}


