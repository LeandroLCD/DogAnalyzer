package com.leandrolcd.doganalyzer.ui.dogdetail


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.leandrolcd.doganalyzer.*
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.admob.BannerAdView
import com.leandrolcd.doganalyzer.ui.authentication.utilities.ErrorLoginScreen
import com.leandrolcd.doganalyzer.ui.authentication.utilities.LoadingScreen
import com.leandrolcd.doganalyzer.ui.doglist.ButtonDialog
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import com.leandrolcd.doganalyzer.ui.ui.theme.*
import com.leandrolcd.doganalyzer.ui.utilits.*
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
    when (val status = viewModel.uiStatus.value) {
        is UiStatus.Error -> {
            ErrorLoginScreen(message = (status as UiStatus.Error<*>).message) {
                navController.popBackStack()
            }
        }
        is UiStatus.Loaded -> {
            viewModel.getDogsById(dogList)
            val context = LocalContext.current as Activity
            interstitialShow(isRecognition, context) {
                viewModel.interstitialShow(context)
            }
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
    val activity = LocalContext.current as Activity
    Scaffold(
        topBar = {
            MyTopAppBar(navController, isRecognition, dog!!.confidence.toInt()) {
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
                    viewModel.addDogToUser(dogId, 2)
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
                val click = activity.setDetailClick()
                if (click % 5 == 0) {
                    viewModel.interstitialShow(activity)
                }
                isVisible = false
            }, onSelectItems = {
                dog = it
                val click = activity.setDetailClick()
                if (click % 5 == 0) {
                    viewModel.interstitialShow(activity)
                }

                isVisible = false
            })
    }
}

fun interstitialShow(
    isRecognition: Boolean,
    context: Context,
    Show: () -> Unit
) {

    if (isRecognition) {
        val click = context.setRecognitionClick()
        if (click % 2 == 0) {
            Show()
        }
    } else {
        val click = context.setDetailClick()
        if (click % 5 == 0) {
            Show()
        }
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
                TitleDialog(text = stringResource(R.string.top_score))
            },
            text = {
                LazyColumn(content = {
                    items(dogList) {
                        ItemDogR(it, onSelectItems)
                    }
                })
            },
            confirmButton = {
                ButtonDialog(text = stringResource(id = R.string.cancel)) {
                    onDismissRequest()
                }
            }
        )
    }

}

@Composable
fun TitleDialog(text: String) {

    val color = Purple500
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Pets,
            contentDescription = stringResource(id = R.string.dog_image),
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun ItemDogR(dog: Dog, onSelectItems: (Dog) -> Unit) {
    val color = if(isSystemInDarkTheme()) {
        Color.White
    }else{
        Color.Black
    }
    TextButton(onClick = { onSelectItems(dog) }) {
        Row(Modifier) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .weight(1f)
            ) {
                Text(text = dog.name,
                    color = color,
                    maxLines = 1,
                    softWrap = true)
            }
            Text(
                text = "${floor(dog.confidence).toInt()} %",
                color = color,
                modifier = Modifier
                    .width(70.dp)
                    .padding(end = 8.dp),
                textAlign = TextAlign.End
            )

        }
    }

}

@Composable
fun MyFab(imageVector: ImageVector = Icons.Default.Check, onClicked: () -> Unit) {
    FloatingActionButton(
        onClick = { onClicked() },
        backgroundColor = primaryColor,
        contentColor = Color.Black
    ) {
        Icon(imageVector = imageVector, contentDescription = null)
    }
}

@Composable
fun MyTopAppBar(
    navController: NavHostController,
    isRecognition: Boolean,
    confidence: Int,
    onClicked: () -> Unit
) {
    Row(
        Modifier.background(Color.Gray),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = stringResource(R.string.back),
                tint = primaryColor
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isRecognition && confidence > 50) {
            TextButton(
                onClick = { onClicked() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 0.dp
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextTitle(
                        textSp = "Probabilidad $confidence %",
                        textEn = "Probability $confidence %",
                        fontSize = 16.sp,
                        color = primaryColor
                    )
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = stringResource(R.string.show_top_score),
                        tint = primaryColor,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

    }
}

@Composable
fun MyBottomBar(index: Int, onClickSelect: (Int) -> Unit) {

    val language = LANGUAGE
    BottomAppBar(
        modifier = Modifier.background(Color.White),
        backgroundColor = primaryColor,
        cutoutShape = CircleShape,
        contentColor = Color.White,
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
        }, unselectedContentColor = textColor)
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
        }, unselectedContentColor = textColor)
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
                fontSize = if (dog.name.length < 16) 32.sp else 22.sp,
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
        elevation = 8.dp,
        color = colorGray

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {


            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextTitle(
                    textSp = "Curiosidades",
                    textEn = "Curiosities",
                    fontSize = 24.sp
                )
                Text(
                    text = if (LANGUAGE.isSpanish()) {
                        textSp
                    } else {
                        textEn
                    },
                    fontSize = 16.sp,
                    color = textColor,
                    style = TextStyle(textAlign = TextAlign.Center)
                )
            }
            if (scroll.value < (scroll.maxValue - 10)) {
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    contentDescription = "Scroll",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Gray
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.ExpandLess,
                    contentDescription = "Scroll",
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
        elevation = 8.dp,
        color = colorGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            TextTitle(
                textSp = "Caracteristicas",
                textEn = "Characteristics",
                fontSize = 24.sp,
                Modifier.padding(bottom = 8.dp)
            )

            TextDescription(
                textSp = dog.temperamentEs,
                textEn = dog.temperament,
                textAlign = TextAlign.Center
            )
            ColumnDetail(
                dog = dog,
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = modifier.fillMaxHeight(1f))
        }


    }
}

@Composable
fun TextTitle(
    textSp: String,
    textEn: String,
    fontSize: TextUnit = 16.sp,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    color: Color = textColor
) {
    Text(
        text = if (LANGUAGE.isSpanish()) {
            textSp
        } else {
            textEn
        },
        fontSize = fontSize,
        color = color,
        fontWeight = FontWeight.Medium,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Composable
fun TextDescription(
    textSp: String,
    textEn: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    fontSize: TextUnit = 16.sp,
    color: Color = textColor
) {
    Text(
        text = if (LANGUAGE.isSpanish()) {
            textSp
        } else {
            textEn
        },
        fontSize = fontSize,
        color = color,
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
        contentDescription = stringResource(id = R.string.dog_image),
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun DogDetail(dog: Dog, index: Int) {
    ConstraintLayout(
        Modifier
            .background(Color.Gray)
            .fillMaxSize()
    ) {
        val (header, body, banner) = createRefs()
        val topGuide = createGuidelineFromTop(0.35f)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Transparent)
                .constrainAs(banner) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalArrangement = Arrangement.Center,

            ) {
            BannerAdView() {}

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


