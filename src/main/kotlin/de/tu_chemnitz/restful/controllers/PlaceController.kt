package de.tu_chemnitz.restful.controllers

import de.tu_chemnitz.restful.data.Distance
import de.tu_chemnitz.restful.data.PathResponse
import de.tu_chemnitz.restful.data.Place
import de.tu_chemnitz.restful.data.PlaceResponse
import de.tu_chemnitz.restful.services.PlaceService
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND
import javax.servlet.http.HttpServletResponse.SC_OK
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("places")
class PlaceController @Autowired constructor(val placeService: PlaceService) {

    @GetMapping("/{name}", produces = ["application/json"])
    @ResponseBody
    fun getPlace(@PathVariable("name") name: String): ResponseEntity<PlaceResponse> =
        placeService[name]
            ?.let { ResponseEntity.ok(PlaceResponse.fromPlace(it)) }
            ?: ResponseEntity.notFound().build()

    // optionally some paging (+sorting) can be added
    @GetMapping(produces = ["application/json"])
    fun getPlaces(
        @RequestParam(required = false, defaultValue = DEFAULT_OFFSET) offset: Int,
        @RequestParam(required = false, defaultValue = DEFAULT_LIMIT) limit: Int
    ): List<PlaceResponse> = placeService.getMany(offset, limit).map(PlaceResponse.Companion::fromPlace)

    @PostMapping(produces = ["application/json"])
    fun createPlaces(@RequestBody places: List<Place>): ResponseEntity<List<PlaceResponse>> {
        val entity = placeService.createMany(places)
        return ResponseEntity.ok().body(entity.map(PlaceResponse.Companion::fromEntity))
    }

    @PostMapping("/distance/heuristic", produces = ["application/json"])
    @ResponseBody
    fun getDistanceByHeuristic(
        @RequestParam("start") start: String,
        @RequestParam("destination") destination: String,
        @RequestBody heuristics: Map<String, Distance>
    ): ResponseEntity<PathResponse> {
        val entity = placeService.getShortestDistance(start, destination, heuristics)
        return entity?.let { ResponseEntity.ok(PathResponse.fromPath(it)) }
            ?: ResponseEntity.badRequest().build()
    }

    @PostMapping("/distance", produces = ["application/json"])
    @ResponseBody
    fun getDistance(
        @RequestParam("start") start: String,
        @RequestParam("destination") destination: String
    ): ResponseEntity<PathResponse> {
        val entity = placeService.getShortestDistance(start, destination)
        return ResponseEntity.ok(PathResponse.fromPath(entity))
    }


    @DeleteMapping("/{name}")
    fun deletePlace(@PathVariable("name") name: String, response: HttpServletResponse) {
        if (placeService.deleteById(name))
            response.status = SC_OK
        else
            response.status = SC_NOT_FOUND
    }

    companion object {
        private const val DEFAULT_OFFSET = "0"
        private const val DEFAULT_LIMIT = "10"
    }
}
