package de.tu_chemnitz.restful.controllers

import de.tu_chemnitz.restful.data.Place
import de.tu_chemnitz.restful.services.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("places")
class PlaceController @Autowired constructor(val placeService: PlaceService) {
    //TODO: add response entities

    @GetMapping("/{name}")
    fun getPlace(@PathVariable("name") name: String) = placeService[name].get().toDto()

    @PostMapping(produces = ["application/json"])
    fun createPlaces(@RequestBody places: List<Place>) = placeService.createMany(places)

    @DeleteMapping("/{name}")
    fun deletePlace(@PathVariable("name") name: String) = placeService.deleteById(name)
}
