package de.tu_chemnitz.restful.services

import de.tu_chemnitz.restful.data.Location
import de.tu_chemnitz.restful.data.Place
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class PlaceServiceIntegrationTest @Autowired constructor(val placeService: PlaceService) {

    @BeforeEach
    fun cleanUp() {
        placeService.deleteAll()
    }

    @Test
    fun `should add place to database`() {
        placeService.createMany(berlin)
        assertEquals(expected = berlin, actual = placeService[berlin.name])
    }

    @ParameterizedTest
    @ArgumentsSource(PlaceProvider::class)
    fun `should delete place from partner list`(places: Set<Place>) {
        val toBeRemoved = Place(
            name = "Umpalumpadorf",
            partners = mapOf(FRANKFURT to 1.0, BERLIN to 1.0, BRAUNSCHWEIG to 1.0),
            location = Location(0.0, 1.0)
        )

        placeService.createMany(places.addPartner(toBeRemoved.name) + toBeRemoved)
        placeService.deleteById(toBeRemoved.name)

        assertNull(placeService[toBeRemoved.name])
        places.forEach { place ->
            assertEquals(expected = place, actual = placeService[place.name])
        }
    }

    private fun Set<Place>.addPartner(name: String, distance: Double = 1.0) = this.map { place ->
        place.copy(partners = place.partners + (name to distance))
    }

    companion object PlaceProvider : ArgumentsProvider {
        private const val BRAUNSCHWEIG = "Braunschweig"
        private const val BERLIN = "Berlin"
        private const val FRANKFURT = "Frankfurt"

        private val berlin = Place(
            name = BERLIN,
            partners = mapOf(
                BRAUNSCHWEIG to 191.0,
                FRANKFURT to 419.0
            ),
            location = Location(0.0, 1.0)
        )

        private val braunschweig = Place(
            name = BRAUNSCHWEIG,
            partners = mapOf(
                BERLIN to 191.0,
                FRANKFURT to 270.57
            ),
            location = Location(0.0, 1.0)
        )

        private val frankfurt = Place(
            name = FRANKFURT,
            partners = mapOf(
                BERLIN to 419.0,
                BRAUNSCHWEIG to 270.57
            ),
            location = Location(0.0, 1.0)
        )

        override fun provideArguments(context: ExtensionContext?): Stream<Arguments> = Stream.of(
            Arguments.of(setOf(berlin, braunschweig, frankfurt))
        )
    }
}
