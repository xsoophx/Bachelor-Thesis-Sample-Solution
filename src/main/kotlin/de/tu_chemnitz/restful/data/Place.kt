package de.tu_chemnitz.restful.data

import org.springframework.data.annotation.Id

typealias Distance = Double
typealias Partners = Map<String, Distance>

data class Place(
    val name: String,
    val partners: Map<String, Distance>,
) {
    fun toEntity() = PlaceEntity(
        name = name,
        partners = partners
    )

    companion object {
        fun fromEntity(placeEntity: PlaceEntity) =
            Place(name = placeEntity.name, partners = placeEntity.partners)
    }
}

data class PlaceEntity(
    @Id val name: String,
    val partners: Map<String, Distance>,
) {
    fun toResponse() = PlaceResponse(name = name, partners = partners)
}

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
