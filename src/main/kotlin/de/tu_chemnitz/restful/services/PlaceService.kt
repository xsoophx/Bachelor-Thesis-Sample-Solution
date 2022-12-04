package de.tu_chemnitz.restful.services

import de.tu_chemnitz.restful.data.Distance
import de.tu_chemnitz.restful.data.Location
import de.tu_chemnitz.restful.data.Node
import de.tu_chemnitz.restful.data.Path
import de.tu_chemnitz.restful.data.Place
import de.tu_chemnitz.restful.data.PlaceEntity
import de.tu_chemnitz.restful.repositories.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceService @Autowired constructor(val placeRepository: PlaceRepository) {

    operator fun get(name: String): Place? = placeRepository.findById(name).orElse(null)?.let {
        Place.fromEntity(it)
    }

    fun createMany(places: List<Place>): Iterable<PlaceEntity> = placeRepository.saveAll(places.map { place ->
        place.createPartners()
        save(place)
    })

    fun createMany(place: Place) = createMany(listOf(place))

    fun deleteAll() = placeRepository.deleteAll()

    fun deleteById(name: String): Boolean {
        return if (placeRepository.existsById(name)) {
            findAllByPartners(name).forEach { placeEntity ->
                save(placeEntity.copy(partners = placeEntity.partners - (name)))
            }
            placeRepository.deleteById(name)
            true
        } else false
    }

    fun getShortestDistance(start: String, destination: String, heuristics: Map<String, Distance>): Path? {
        if (this[start] == null || this[destination] == null) return null

        val places = findAll()
        if (places.any { it.name !in heuristics.keys }) return null

        return aStar(
            start,
            destination,
            places.map { Place.fromEntity(it) }.associateWith { place -> heuristics.getValue(place.name) })
    }

    private fun findAll() = placeRepository.findAll()

    private fun findAllByPartners(name: String) = placeRepository.findByPartnersExists(name)

    private fun save(place: Place) = placeRepository.save(place.toEntity())

    private fun save(placeEntity: PlaceEntity) = placeRepository.save(placeEntity)

    private fun Place.createPartners() {
        partners.forEach { (partner, distance) ->
            this@PlaceService[partner]?.let { oldEntity ->
                save(oldEntity.copy(partners = oldEntity.partners + (partner to distance)))
            } ?: run { save(partner, name, distance) }
        }
    }

    private fun save(name: String, partner: String, distance: Distance) = placeRepository.save(
        PlaceEntity(
            name = name,
            location = Location(0.0, 0.0),
            partners = mapOf(partner to distance)
        )
    )

    // very ugly, needs a lot of refactoring
    private fun aStar(start: String, destination: String, heuristics: Map<Place, Distance>): Path {
        val simplifiedHeuristics = heuristics.map { it.key.name to it.value }.toMap()
        var open = listOf(Node(name = start, f = 0.0, g = 0.0, h = 0.0, parent = null))
        var closed = listOf<Node>()

        while (open.isNotEmpty()) {
            val currentNode = open.minBy { it.g + it.h }
            if (currentNode.name == destination) {
                closed = closed + currentNode
                break
            }

            val newNodes = heuristics.keys.find { it.name == currentNode.name }!!.partners.map { (place, distance) ->
                val h = simplifiedHeuristics.getValue(place)
                Node(
                    name = place,
                    g = (currentNode.parent?.g ?: 0.0) + distance,
                    f = currentNode.g + distance + h,
                    h = h,
                    parent = currentNode
                )
            }

            open = open - currentNode + newNodes
            closed = closed + currentNode
        }

        val path = getResultPath(closed)
        return Path(placesVisited = path.first, distance = path.second)
    }

    private fun getResultPath(closed: List<Node>): Pair<List<String>, Distance> {
        val path = mutableListOf<Node>()
        var currentNode: Node? = closed.last()

        while (currentNode != null) {
            path.add(0, currentNode)
            currentNode = currentNode.parent
        }
        return path.map { it.name } to path.sumOf { it.g }
    }
}