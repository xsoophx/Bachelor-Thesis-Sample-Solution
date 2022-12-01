package de.tu_chemnitz.restful.services

import de.tu_chemnitz.restful.data.Place
import de.tu_chemnitz.restful.repositories.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceService @Autowired constructor(val placeRepository: PlaceRepository){

    fun get(name: String) = placeRepository.findById(name)

    fun create(place: Place) = placeRepository.save(place.toEntity())
}