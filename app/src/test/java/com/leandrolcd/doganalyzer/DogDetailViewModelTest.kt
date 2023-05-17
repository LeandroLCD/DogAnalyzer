package com.leandrolcd.doganalyzer

import androidx.navigation.NavHostController
import com.leandrolcd.doganalyzer.data.repositoty.IFireStoreRepository
import com.leandrolcd.doganalyzer.ui.admob.InterstitialAdMod
import com.leandrolcd.doganalyzer.ui.dogdetail.DogDetailViewModel
import com.leandrolcd.doganalyzer.ui.model.Dog
import com.leandrolcd.doganalyzer.ui.model.DogRecognition
import com.leandrolcd.doganalyzer.ui.model.Routes
import com.leandrolcd.doganalyzer.ui.model.UiStatus
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class DogDetailViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var repository: IFireStoreRepository

    @MockK
    lateinit var interstitialAdMod: InterstitialAdMod

    @MockK
    lateinit var navHostController: NavHostController


    @Test
    fun constructor() {
        MockKAnnotations.init(this)
        val viewModel = DogDetailViewModel(repository, interstitialAdMod)
        viewModel.setNavHostController(navHostController)
        assertNotNull(viewModel)
    }

    @Test
    fun whenGetDogsById_isCalled_thenUiStatusShouldBeLoading() {
        MockKAnnotations.init(this)
        val viewModel = DogDetailViewModel(repository, interstitialAdMod)
        viewModel.getDogsById(emptyList())

        assert(viewModel.uiStatus.value is UiStatus.Loading)
    }

    @Test
    fun givenGetDogsById_whenRepositoryReturnsSuccess_thenDogStatusShouldBeSortedByDescendingConfidence() {
        MockKAnnotations.init(this)
        class Repo():IFireStoreRepository{
            override suspend fun addDogToUser(dogId: String): UiStatus<Boolean> {
                TODO("Not yet implemented")
            }

            override suspend fun getDogCollection(): List<Dog> {
                TODO("Not yet implemented")
            }

            override suspend fun getDogById(id: String): UiStatus<Dog> {
                TODO("Not yet implemented")
            }

            override fun clearCache() {
                TODO("Not yet implemented")
            }

            override suspend fun getDogsByIds(list: List<DogRecognition>): UiStatus<List<Dog>> {
                return UiStatus.Success(
                    listOf(
                        Dog(
                            "2", "", "", "", "",
                            "", "", "", 2
                        ),
                        Dog(
                            "1", "Poodle", "", "", "", "",
                            "", "", 1
                        )
                    )
                )
            }

            override suspend fun synchronizeNow() {
                TODO("Not yet implemented")
            }

        }
        val viewModel = DogDetailViewModel(Repo(), interstitialAdMod)

        val dogs = listOf(DogRecognition("1", 0.6f), DogRecognition("2", 0.8f))
        val success = UiStatus.Success(
            listOf(
                Dog(
                    "2", "", "", "", "",
                    "", "", "", 2
                ),
                Dog(
                    "1", "Poodle", "", "", "", "",
                    "", "", 1
                )
            )
        )

        runBlocking {
            viewModel.getDogsById(dogs)


            assertEquals(
                success.data.sortedByDescending { it.confidence },
                viewModel.dogStatus.value
            )
    }
}
    @Test
    fun givenAddDogToUser_whenRepositoryReturnsSuccess_thenNavHostControllerShouldPopBackStack() {
        MockKAnnotations.init(this)
        val viewModel = DogDetailViewModel(repository, interstitialAdMod)

        val success = UiStatus.Success(true)

        coEvery { repository.addDogToUser("1") } returns success
        every { navHostController.popBackStack(Routes.ScreenDogList.route, false) } returns false

        viewModel.addDogToUser("1")
        assert(viewModel.uiStatus.value is UiStatus.Success)
    }

    @Test
    fun givenAddDogToUser_whenRepositoryReturnsError_thenUiStatusShouldBeError() {
        MockKAnnotations.init(this)
        val viewModel = DogDetailViewModel(repository, interstitialAdMod)

        val message = "Error message"
        val error = UiStatus.Error<Boolean>(message)

        coEvery { repository.addDogToUser("5") } returns error // envolver la llamada en coEvery

        runBlocking { // usar runBlocking para ejecutar la prueba en una coroutina
           val e = repository.addDogToUser("5")

            viewModel.addDogToUser("1")

            assertEquals(UiStatus.Error<List<Dog>>(message), viewModel.uiStatus.value)
        }

    }


}
