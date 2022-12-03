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

    companion object {
        fun fromEntity(placeEntity: PlaceEntity) =
            Place(name = placeEntity.name, location = placeEntity.location, partners = placeEntity.partners)
    }
}

data class PlaceEntity(
    @Id val name: String,
    val location: Location,
    val partners: Map<String, Distance>,
) {
    fun toResponse() = PlaceResponse(partners = partners)
}

data class PlaceResponse(
    val partners: Map<String, Distance>
) {
    companion object {
        fun fromPlace(place: Place) =
            PlaceResponse(partners = place.partners)

        fun fromEntity(placeEntity: PlaceEntity) = PlaceResponse(partners = placeEntity.partners)
    }
}
