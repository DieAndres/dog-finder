import android.app.Application
import io.mockk.mockk
import io.mockk.every
import com.example.dogfinder.data.local.AppDatabase
import com.example.dogfinder.data.local.DogDao
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.dogfinder.data.api.RetrofitInstance
import com.example.dogfinder.models.BreedResponse
import com.example.dogfinder.viewmodel.DogViewModel
import io.mockk.coEvery
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DogViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    // este test simula una peticion a la api para demotrar que luego de recibirla ya no muestra la pantalla de cargando
    @Test
    fun `estado inicial de isLoading debe ser falso despues de iniciar`() = runTest {
        // "Engañamos" a Retrofit (Internet)
        mockkObject(RetrofitInstance)
        coEvery { RetrofitInstance.api.getBreeds() } returns BreedResponse(emptyMap(), "success")

        mockkObject(AppDatabase)
        val mockDb = mockk<AppDatabase>(relaxed = true)
        val mockDao = mockk<DogDao>(relaxed = true)
        every { AppDatabase.getDatabase(any()) } returns mockDb
        every { mockDb.dogDao() } returns mockDao

        val application = mockk<Application>(relaxed = true)
        val viewModel = DogViewModel(application)

        advanceUntilIdle()
        assertEquals(false, viewModel.isLoading.value)

        unmockkObject(RetrofitInstance)
        unmockkObject(AppDatabase)
    }
}