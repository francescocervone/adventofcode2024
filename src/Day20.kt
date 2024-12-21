import java.util.LinkedList
import kotlin.math.absoluteValue

fun main() {
    data class Location(val i: Int, val j: Int)

    fun findStart(input: List<String>): Location {
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] == 'S')
                    return Location(i, j)
            }
        }
        error("Start not found")
    }

    fun findEnd(input: List<String>): Location {
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] == 'E')
                    return Location(i, j)
            }
        }
        error("End not found")
    }

    fun findPath(input: List<String>): List<Location> {
        val start = findStart(input)
        val end = findEnd(input)
        var location = start
        val path = mutableListOf(location)
        while (location != end) {
            location = listOf(
                Location(location.i - 1, location.j),
                Location(location.i + 1, location.j),
                Location(location.i, location.j - 1),
                Location(location.i, location.j + 1),
            ).filter { it.i in input.indices }
                .filter { it.j in input[0].indices }
                .filter { it !in path }
                .first { input[it.i][it.j] in setOf('.', 'E') }
            path += location
        }
        return path
    }

    fun distance(path: List<Location>): Map<Location, Int> {
        return path.mapIndexed { index, location ->
            location to index
        }.associateBy { it.first }
            .mapValues { (_, v) -> v.second }
    }

    fun part1(input: List<String>): Long {
        val path = findPath(input)
        val distance = distance(path)
        var count = 0L
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] == '#') {
                    val up = Location(i - 1, j)
                    val down = Location(i + 1, j)
                    val left = Location(i, j - 1)
                    val right = Location(i, j + 1)
                    if (up in distance.keys && down in distance.keys) {
                        val saved = (distance[down]!! - distance[up]!!).absoluteValue - 2
                        if (saved >= 100) {
                            count++
                        }
                    }
                    if (left in distance.keys && right in distance.keys) {
                        val saved = (distance[left]!! - distance[right]!!).absoluteValue - 2
                        if (saved >= 100) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    fun findWalls(input: List<String>): Set<Location> {
        return buildSet {
            for (i in input.indices) {
                for (j in input[i].indices) {
                    if (input[i][j] == '#') add(Location(i, j))
                }
            }
        }
    }

    data class Cheat(
        val start: Location,
        val end: Location,
    )

    data class WallMemo(
        val startLocation: Location,
        val wallLocation: Location,
        val remainingCheatSteps: Int,
    )

    fun countCheatsBfs(
        start: Location,
        pathPoints: Set<Location>,
        walls: Set<Location>,
        distanceMap: Map<Location, Int>,
        iRange: IntRange,
        jRange: IntRange,
        cheats: MutableMap<Cheat, Int>,
    ) {
        data class Iteration(
            val currentLocation: Location,
            val remainingCheatSteps: Int,
            val startingCheatLocation: Location,
            val demolishedWalls: Set<Location>,
        )

        val visited = mutableSetOf<WallMemo>()
        val queue = LinkedList<Iteration>()
        queue.add(Iteration(start, 20, start, emptySet()))
        while (queue.isNotEmpty()) {
            val (currentLocation, remainingCheatSteps, startingCheatLocation, demolishedWalls) = queue.removeFirst()
            println(currentLocation)
            listOf(
                Location(currentLocation.i - 1, currentLocation.j),
                Location(currentLocation.i + 1, currentLocation.j),
                Location(currentLocation.i, currentLocation.j - 1),
                Location(currentLocation.i, currentLocation.j + 1),
            ).filter { it.i in iRange }
                .filter { it.j in jRange }
                .filter { it !in demolishedWalls }
                // .filter { it !in visited }
                // .onEach { visited += it }
                .forEach { nextLocation ->
                    when {
                        currentLocation in pathPoints && nextLocation in pathPoints -> {
                            val currentLocationDistance = distanceMap[currentLocation]!!
                            val nextLocationDistance = distanceMap[nextLocation]!!
                            // We're going backwards
                            if (nextLocationDistance <= currentLocationDistance) return@forEach
                            queue.add(
                                Iteration(
                                    currentLocation = nextLocation,
                                    remainingCheatSteps = remainingCheatSteps,
                                    startingCheatLocation = nextLocation,
                                    demolishedWalls = demolishedWalls
                                )
                            )
                        }

                        currentLocation in pathPoints && nextLocation in walls -> {
                            val nextLocationWallMemo = WallMemo(
                                startLocation = currentLocation,
                                wallLocation = nextLocation,
                                remainingCheatSteps = remainingCheatSteps - 1
                            )
//                            if (nextLocationWallMemo in wallMemo) return@forEach
//                             wallMemo += nextLocationWallMemo
                            val nextIteration = Iteration(
                                currentLocation = nextLocation,
                                remainingCheatSteps = remainingCheatSteps - 1,
                                startingCheatLocation = currentLocation,
                                demolishedWalls = setOf(nextLocation)
                            )
                            if (nextLocationWallMemo !in visited) {
                                queue.add(nextIteration)
                                visited += nextLocationWallMemo
                            }
                        }

                        currentLocation in walls && nextLocation in walls && remainingCheatSteps == 0 -> return@forEach

                        currentLocation in walls && nextLocation in walls -> {
                            val nextLocationWallMemo = WallMemo(
                                startLocation = startingCheatLocation,
                                wallLocation = nextLocation,
                                remainingCheatSteps = remainingCheatSteps - 1
                            )
//                            if (nextLocationWallMemo in wallMemo) return@forEach
//                            wallMemo += nextLocationWallMemo
                            val nextIteration = Iteration(
                                currentLocation = nextLocation,
                                remainingCheatSteps = remainingCheatSteps - 1,
                                startingCheatLocation = startingCheatLocation,
                                demolishedWalls = demolishedWalls + nextLocation
                            )
                            if (nextLocationWallMemo !in visited) {
                                queue.add(nextIteration)
                                visited += nextLocationWallMemo
                            }
                        }

                        currentLocation in walls && nextLocation in pathPoints -> {
                            val startingCheatLocationDistance = distanceMap[startingCheatLocation]!!
                            val nextLocationDistance = distanceMap[nextLocation]!!
                            // We're going backward
                            if (nextLocationDistance <= startingCheatLocationDistance) {
                                return@forEach
                            }
                            val cheatSteps = (20 - remainingCheatSteps) + 1
                            val savedSteps = nextLocationDistance - startingCheatLocationDistance - cheatSteps
                            val cheat = Cheat(startingCheatLocation, nextLocation)
                            if (savedSteps < 50) return@forEach
                            cheats[cheat] = maxOf(savedSteps, cheats.getOrPut(cheat) { savedSteps })
//                            if (startingCheatLocation == Location(3, 1) && nextLocation == Location(7, 3)) {
//                                println()
//                                println("savedSteps: $savedSteps")
//                                println("remainingCheatSteps: $remainingCheatSteps")
//                                println("cheatSteps: $cheatSteps")
//                                println("nextLocationDistance: $nextLocationDistance")
//                                println("startingCheatLocationDistance: $startingCheatLocationDistance")
//                            }
                            return@forEach
                        }

                        else -> error("Unknown status for $currentLocation && $nextLocation")
                    }
                }
        }
    }

    fun part2(input: List<String>): Long {
        val path = findPath(input)
        val distance = distance(path)
        val walls = findWalls(input)
        val startLocation = path.first()
        val cheats = mutableMapOf<Cheat, Int>()
        countCheatsBfs(
            start = startLocation,
            pathPoints = path.toSet(),
            walls = walls,
            distanceMap = distance,
            iRange = input.indices,
            jRange = input[0].indices,
            cheats = cheats,
        )
        cheats
            .toList()
            .groupBy { it.second }
            .mapValues { (_, values) -> values.map { it.first } }
            .toList()
            .sortedBy { it.first }
            .forEach { (savedSteps, cheats) ->
                println("${cheats.size} of $savedSteps")
            }
        return cheats.size.toLong()
    }

    val testInput = readInput("Day20_test")
    // part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day20")
//    part1(input).println()
    // part2(input).println()
}
