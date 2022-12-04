package de.tu_chemnitz.restful.data

data class Path(
    val placesVisited: List<String>,
    val distance: Distance
)

data class PathResponse(
    val placesVisited: List<String>,
    val distance: Distance
) {
    companion object {
        fun fromPath(path: Path) = PathResponse(placesVisited = path.placesVisited, distance = path.distance)
    }
}

data class Node(
    val name: String,
    val f: Distance,
    val g: Distance,
    val h: Distance,
    val parent: Node?
)
