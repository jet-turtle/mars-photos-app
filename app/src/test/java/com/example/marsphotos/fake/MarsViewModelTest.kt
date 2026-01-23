package com.example.marsphotos.fake

import com.example.marsphotos.rules.TestDispatcherRule
import com.example.marsphotos.ui.screens.MarsUiState
import com.example.marsphotos.ui.screens.MarsViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MarsViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun marsViewModel_getMarsPhotos_verifyMarsUiStateSuccess() =
        runTest {
            val marsViewModel = MarsViewModel(
                marsPhotosRepository = FakeNetworkMarsPhotosRepository()
            )

            advanceUntilIdle()

            assertEquals(
                MarsUiState.Success(FakeDataSource.fakeMarsPhotosList),
                marsViewModel.marsUiState
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun marsViewModel_getMarsPhotos_verifyMarsUiStateError() = runTest {
        val marsViewModel = MarsViewModel(
            marsPhotosRepository = NetworkErrorMarsPhotosRepository()
        )

        advanceUntilIdle()

        assertEquals(
            MarsUiState.Error,
            marsViewModel.marsUiState
        )
    }
}
