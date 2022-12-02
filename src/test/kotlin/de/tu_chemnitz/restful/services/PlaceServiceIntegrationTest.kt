package de.tu_chemnitz.restful.services

import de.tu_chemnitz.restful.data.Location
import de.tu_chemnitz.restful.data.Place
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
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
        val place = Place(
            name = "Berlin",
            location = Location(longitude = 13.404954, latitude = 52.520008),
            partners = mapOf(
                "Braunschweig" to 191.0,
                "Frankfurt" to 419.0
            )
        )
        placeService.createMany(listOf(place))
        assertEquals(expected = place.toEntity(), actual = placeService[place.name].get())
    }
}
