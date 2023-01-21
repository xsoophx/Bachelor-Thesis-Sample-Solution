package de.tu_chemnitz.restful.data

import org.springframework.data.annotation.Id

typealias Distance = Double
typealias Partners = Map<String, Distance>

data class Place(
    val name: String,
    val partners: Map<String, Distance>,
    val location: Location?
) {
    fun toEntity() = PlaceEntity(
        name = name,
        partners = partners,
        location = location?.let { LocationEntity(it.x, it.y) }
    )

    companion object {
        fun fromEntity(placeEntity: PlaceEntity) =
            Place(
                name = placeEntity.name,
                partners = placeEntity.partners,
                location = placeEntity.location?.let { Location(it.x, it.y) }
            )
    }
}

data class PlaceEntity(
    @Id val name: String,
    val partners: Map<String, Distance>,
    val location: LocationEntity?
) {
    fun toResponse() = PlaceResponse(name = name, partners = partners)
}

data class Location(
    val x: Double,
    val y: Double
)

data class LocationEntity(
    val x: Double,
    val y: Double
)

data class PlaceResponse(
    val name: String,
    val partners: Partners
) {
    companion object {
        fun fromPlace(place: Place) = PlaceResponse(name = place.name, partners = place.partners)
        fun fromEntity(placeEntity: PlaceEntity) =
            PlaceResponse(name = placeEntity.name, partners = placeEntity.partners)
    }
}
