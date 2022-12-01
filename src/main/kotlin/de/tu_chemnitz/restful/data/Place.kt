package de.tu_chemnitz.restful.data

import org.springframework.data.annotation.Id

data class Location(
    val longitude: Double,
    val latitude: Double,
)

typealias Distance = Double

data class Place(
    val name: String,
    val location: Location,
    val partners: Map<String, Distance>,
) {
    fun toEntity() = PlaceEntity(
        name = name,
        location = location,
        partners = partners
    )
}

data class PlaceEntity(
    @Id val name: String,
    val location: Location,
    val partners: Map<String, Distance>,
)

data class PlaceDto(
    val partners: Set<PlaceDto>
)
