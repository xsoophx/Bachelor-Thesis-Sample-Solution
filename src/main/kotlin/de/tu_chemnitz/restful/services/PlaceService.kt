package de.tu_chemnitz.restful.services

import de.tu_chemnitz.restful.data.Distance
import de.tu_chemnitz.restful.data.Location
import de.tu_chemnitz.restful.data.Place
import de.tu_chemnitz.restful.data.PlaceEntity
import de.tu_chemnitz.restful.repositories.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceService @Autowired constructor(val placeRepository: PlaceRepository) {

    operator fun get(name: String) = placeRepository.findById(name)

    fun createMany(places: List<Place>): Iterable<PlaceEntity> = placeRepository.saveAll(places.map { place ->
        place.createPartners()
        create(place)
    })

    fun deleteAll() = placeRepository.deleteAll()

    fun deleteById(name: String) {
        findAllByPartners(name).forEach { placeEntity ->
            create(placeEntity.copy(partners = placeEntity.partners - (name)))
        }
        placeRepository.deleteById(name)
    }

    private fun findAllByPartners(name: String) = placeRepository.findAllByPartner(name)

    private fun create(place: Place) = placeRepository.save(place.toEntity())

    private fun create(placeEntity: PlaceEntity) = placeRepository.save(placeEntity)

    private fun Place.createPartners() {
        partners.forEach { (partner, distance) ->
            this@PlaceService[partner].orElse(null)?.let { oldEntity ->
                create(oldEntity.copy(partners = oldEntity.partners + (partner to distance)))
            } ?: run { create(partner, name, distance) }
        }
    }

    private fun create(name: String, partner: String, distance: Distance) = placeRepository.save(
        PlaceEntity(
            name = name,
            location = Location(0.0, 0.0),
            partners = mapOf(partner to distance)
        )
    )
}