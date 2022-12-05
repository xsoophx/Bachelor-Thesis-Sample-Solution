package de.tu_chemnitz.restful.controllers

import com.ninjasquad.springmockk.MockkBean
import de.tu_chemnitz.restful.data.Location
import de.tu_chemnitz.restful.data.Place
import de.tu_chemnitz.restful.data.PlaceResponse
import de.tu_chemnitz.restful.services.PlaceService
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.util.stream.Stream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND
import javax.servlet.http.HttpServletResponse.SC_OK
import kotlin.test.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(PlaceController::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlaceControllerTest @Autowired constructor(private val controller: PlaceController) {

    @MockkBean
    private lateinit var service: PlaceService

    @BeforeEach
    fun resetMocks() = clearMocks(service)

    @AfterEach
    fun confirm() = confirmVerified(service)

    @Test
    fun `should create place`() {
        every { service.createMany(listOf(place)) } returns listOf(place.toEntity())
        val response = controller.createPlaces(listOf(place))

        assertEquals(expected = HttpStatus.OK, actual = response.statusCode)
        assertEquals(expected = listOf(PlaceResponse.fromPlace(place)), actual = response.body)

        verify(exactly = 1) { service.createMany(listOf(place)) }
    }

    @ParameterizedTest
    @ArgumentsSource(Provider::class)
    fun `should delete user`(deletionSuccessful: Boolean, statusCode: Int) {
        val servletResponse = mockk<HttpServletResponse>()
        every { service.deleteById(place.name) } returns deletionSuccessful
        every { servletResponse.status = statusCode } just Runs

        controller.deletePlace(place.name, servletResponse)

        verify(exactly = 1) { service.deleteById(place.name) }
        verify(exactly = 1) { servletResponse.status = statusCode }
    }

    companion object Provider : ArgumentsProvider {
        private val place = Place(
            name = "Umpalumpadorf",
            location = Location(longitude = 12.916667, latitude = 50.833332),
            partners = mapOf()
        )

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> = Stream.of(
            Arguments.of(true, SC_OK),
            Arguments.of(false, SC_NOT_FOUND),
        )
    }
}