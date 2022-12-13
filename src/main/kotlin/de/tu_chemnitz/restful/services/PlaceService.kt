package de.tu_chemnitz.restful.services

import de.tu_chemnitz.restful.data.Distance
import de.tu_chemnitz.restful.data.Node
import de.tu_chemnitz.restful.data.Path
import de.tu_chemnitz.restful.data.Place
import de.tu_chemnitz.restful.data.PlaceEntity
import de.tu_chemnitz.restful.repositories.PlaceRepository
import java.util.PriorityQueue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceService @Autowired constructor(val placeRepository: PlaceRepository) {

    operator fun get(name: String): Place? = placeRepository.findById(name).orElse(null)?.let {
        Place.fromEntity(it)
    }

    fun getMany(offset: Int, limit: Int): List<Place> =
        placeRepository.findAll().map(Place.Companion::fromEntity)

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
            partners = mapOf(partner to distance)
        )
    )

    // needs some refactoring again
    private fun aStar(start: String, destination: String, heuristics: Map<Place, Distance>): Path {
        val simplifiedHeuristics = heuristics.map { it.key.name to it.value }.toMap()
        val open = PriorityQueue<Node>(compareBy { it.f }).also {
            it.add(Node(name = start, f = 0.0, g = 0.0, h = 0.0, parent = null))
        }
        val closed = mutableSetOf<Node>()

        while (open.isNotEmpty()) {
            val currentNode = open.poll()
            if (currentNode.name == destination) {
                closed += currentNode
                break
            }

            val heuristic = checkNotNull(heuristics.keys.find { it.name == currentNode.name }) {
                "There should be a heuristic for ${currentNode.name}"
            }
            val newNodes = heuristic.partners.asSequence().filter { partner ->
                partner.key !in closed.map { it.name }
            }.map { (place, distance) ->
                val h = simplifiedHeuristics.getValue(place)
                Node(
                    name = place,
                    g = currentNode.g + distance,
                    f = currentNode.g + distance + h,
                    h = h,
                    parent = currentNode
                )
            }

            // needs optimization
            newNodes.filter { newNode -> open.containsHigherFValueOf(newNode) || open.none { it.name == newNode.name } }
                .forEach { node ->
                    open.removeAll { it.name == node.name }
                    open += node
                }

            open -= currentNode
            closed += currentNode
        }

        val path = getResultPath(closed, destination)
        return Path(placesVisited = path.first, distance = path.second)
    }

    private fun getResultPath(closed: Set<Node>, destination: String): Pair<List<String>, Distance> {
        val destinationNode = closed.first { it.name == destination }
        val path = generateSequence(destinationNode) { it.parent }
            .toList()
            .asReversed()

        return path.map { it.name } to destinationNode.g
    }

    private fun PriorityQueue<Node>.containsHigherFValueOf(node: Node): Boolean =
        find { it.name == node.name }?.let { it.f > node.f } ?: false
}