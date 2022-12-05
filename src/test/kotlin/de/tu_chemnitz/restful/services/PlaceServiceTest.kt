package de.tu_chemnitz.restful.services

import de.tu_chemnitz.restful.data.Location
import de.tu_chemnitz.restful.data.Path
import de.tu_chemnitz.restful.data.Place
import java.util.stream.Stream
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
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
class PlaceServiceTest @Autowired constructor(val placeService: PlaceService) {

    @BeforeEach
    fun cleanUp() {
        placeService.deleteAll()
    }

    @ParameterizedTest
    @ArgumentsSource(PlaceProvider::class)
    fun `aStar should work correctly`(places: Set<Place>) {
        placeService.createMany(places.toList())
        val heuristics = mapOf(
            SAARBRUECKEN to 222.0,
            KAISERSLAUTERN to 158.0,
            KARLSRUHE to 140.0,
            LUDWIGSHAFEN to 108.0,
            FRANKFURT to 96.0,
            HEILBRONN to 87.0,
            WUERZBURG to 0.0
        )

        val actual = placeService.getShortestDistance(
            start = saarbruecken.name,
            destination = wuerzburg.name,
            heuristics = heuristics
        )

        assertEquals(
            expected = Path(listOf(SAARBRUECKEN, KAISERSLAUTERN, FRANKFURT, WUERZBURG), distance = 289.0),
            actual = actual
        )
    }

    companion object PlaceProvider : ArgumentsProvider {
        private const val KAISERSLAUTERN = "Kaiserslautern"
        private const val SAARBRUECKEN = "Saarbruecken"
        private const val KARLSRUHE = "Karlsruhe"
        private const val FRANKFURT = "Frankfurt"
        private const val LUDWIGSHAFEN = "Ludwigshafen"
        private const val HEILBRONN = "Heilbronn"
        private const val WUERZBURG = "Wuerzburg"

        private val saarbruecken = Place(
            name = SAARBRUECKEN,
            location = Location(longitude = 13.404954, latitude = 52.520008),
            partners = mapOf(
                KAISERSLAUTERN to 70.0,
                KARLSRUHE to 145.0
            )
        )

        private val kaiserslautern = Place(
            name = KAISERSLAUTERN,
            location = Location(longitude = 10.516667, latitude = 52.266666),
            partners = mapOf(
                SAARBRUECKEN to 70.0,
                FRANKFURT to 103.0,
                LUDWIGSHAFEN to 53.0
            )
        )

        private val frankfurt = Place(
            name = FRANKFURT,
            location = Location(longitude = 8.682127, latitude = 50.110924),
            partners = mapOf(
                KAISERSLAUTERN to 103.0,
                WUERZBURG to 116.0
            )
        )

        private val wuerzburg = Place(
            name = WUERZBURG,
            location = Location(longitude = 8.682127, latitude = 50.110924),
            partners = mapOf(
                FRANKFURT to 116.0,
                LUDWIGSHAFEN to 183.0,
                HEILBRONN to 102.0
            )
        )

        private val ludwigshafen = Place(
            name = LUDWIGSHAFEN,
            location = Location(longitude = 8.682127, latitude = 50.110924),
            partners = mapOf(
                KAISERSLAUTERN to 53.0,
                WUERZBURG to 183.0
            )
        )

        private val karlsruhe = Place(
            name = KARLSRUHE,
            location = Location(longitude = 8.682127, latitude = 50.110924),
            partners = mapOf(
                SAARBRUECKEN to 145.0,
                HEILBRONN to 84.0
            )
        )

        val heilbronn = Place(
            name = HEILBRONN,
            location = Location(longitude = 8.682127, latitude = 50.110924),
            partners = mapOf(
                WUERZBURG to 102.0,
                KARLSRUHE to 84.0
            )
        )


        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.of(setOf(saarbruecken, kaiserslautern, frankfurt, ludwigshafen, karlsruhe, heilbronn, wuerzburg))
        )
    }
}