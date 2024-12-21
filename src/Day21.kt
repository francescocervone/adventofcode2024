import java.util.LinkedList

private enum class Day21Actions(val char: Char) {
    Left('<'),
    Up('^'),
    Right('>'),
    Down('v'),
    Push('A')
}

fun main() {
    data class Location(val i: Int, val j: Int)

    data class Route(
        val from: Char,
        val to: Char,
    )

    fun isNumPadIllegalSpot(location: Location): Boolean {
        val i = location.i
        val j = location.j
        if (i !in 0..3) return true
        if (j !in 0..2) return true
        if (i to j == 3 to 0) return true
        return false
    }

    fun isDirPadIllegalSpot(location: Location): Boolean {
        val i = location.i
        val j = location.j
        if (i !in 0..1) return true
        if (j !in 0..2) return true
        if (i to j == 0 to 0) return true
        return false
    }

    fun findNumPadShortestPaths(): Map<Route, Set<List<Day21Actions>>> {
        val keys = Array(4) { Array(3) { ' ' } }
        keys[0][0] = '7'
        keys[0][1] = '8'
        keys[0][2] = '9'
        keys[1][0] = '4'
        keys[1][1] = '5'
        keys[1][2] = '6'
        keys[2][0] = '1'
        keys[2][1] = '2'
        keys[2][2] = '3'
        keys[3][1] = '0'
        keys[3][2] = 'A'
        val paths = mutableMapOf<Route, Set<List<Day21Actions>>>()
        for (i in 0..3) {
            for (j in 0..2) {
                if (isNumPadIllegalSpot(Location(i, j))) continue
                val from = keys[i][j]
                val queue = LinkedList<Pair<Location, List<Day21Actions>>>()
                queue.add(Location(i, j) to emptyList())
                val enqueued = mutableMapOf<Location, Set<List<Day21Actions>>>(Location(i, j) to setOf(emptyList()))
                while (queue.isNotEmpty()) {
                    val (location, path) = queue.removeFirst()
                    val to = keys[location.i][location.j]
                    val existingPaths = paths.getOrPut(Route(from, to)) { emptySet() }
                    paths[Route(from, to)] = existingPaths.toMutableSet().apply { add(path) }
                    listOf(
                        location.copy(j = location.j - 1) to path + Day21Actions.Left,
                        location.copy(j = location.j + 1) to path + Day21Actions.Right,
                        location.copy(i = location.i - 1) to path + Day21Actions.Up,
                        location.copy(i = location.i + 1) to path + Day21Actions.Down,
                    )
                        .asSequence()
                        .filter {
                            val size = (enqueued[it.first] ?: emptySet()).firstOrNull()?.size
                            if (size == null) true
                            else it.second.size <= size
                        }
                        .filter { it.second !in (enqueued[it.first] ?: emptySet()) }
                        .filter { !isNumPadIllegalSpot(it.first) }
                        .onEach {
                            enqueued[it.first] = (enqueued[it.first] ?: emptySet()).plus<List<Day21Actions>>(it.second)
                        }
                        .onEach { queue += it }
                        .toList()
                }
            }
        }
        return paths
    }

    fun findDirPadShortestPaths(): Map<Route, Set<List<Day21Actions>>> {
        val keys = Array(2) { Array(3) { ' ' } }
        keys[0][1] = '^'
        keys[0][2] = 'A'
        keys[1][0] = '<'
        keys[1][1] = 'v'
        keys[1][2] = '>'
        val paths = mutableMapOf<Route, Set<List<Day21Actions>>>()
        for (i in 0..2) {
            for (j in 0..3) {
                if (isDirPadIllegalSpot(Location(i, j))) continue
                val from = keys[i][j]
                val queue = LinkedList<Pair<Location, List<Day21Actions>>>()
                queue.add(Location(i, j) to emptyList())
                val enqueued = mutableMapOf<Location, Set<List<Day21Actions>>>(Location(i, j) to setOf(emptyList()))
                while (queue.isNotEmpty()) {
                    val (location, path) = queue.removeFirst()
                    val to = keys[location.i][location.j]
                    val existingPaths = paths.getOrPut(Route(from, to)) { emptySet() }
                    paths[Route(from, to)] = existingPaths.toMutableSet().apply { add(path) }
                    listOf(
                        location.copy(j = location.j - 1) to path + Day21Actions.Left,
                        location.copy(j = location.j + 1) to path + Day21Actions.Right,
                        location.copy(i = location.i - 1) to path + Day21Actions.Up,
                        location.copy(i = location.i + 1) to path + Day21Actions.Down,
                    ).asSequence()
                        .filter {
                            val size = (enqueued[it.first] ?: emptySet()).firstOrNull()?.size
                            if (size == null) true
                            else it.second.size <= size
                        }
                        .filter { it.second !in (enqueued[it.first] ?: emptySet()) }
                        .filter { !isDirPadIllegalSpot(it.first) }
                        .onEach {
                            enqueued[it.first] = (enqueued[it.first] ?: emptySet()).plus<List<Day21Actions>>(it.second)
                        }
                        .onEach { queue += it }
                        .toList()
                }
            }
        }
        return paths
    }

    fun findPaths(
        from: Char,
        sequence: String,
        paths: Map<Route, Set<List<Day21Actions>>>,
    ): List<List<Day21Actions>> {
        if (sequence.isEmpty()) {
            return listOf(emptyList())
        }
        val to = sequence.first()
        val routePaths = paths[Route(from, to)]!!
        return routePaths.flatMap { path ->
            val nextSequence = sequence.substring(1)
            findPaths(to, nextSequence, paths).map { subpath ->
                path + Day21Actions.Push + subpath
            }
        }
    }

    fun part1(input: List<String>): Long {
        val numPadShortestPaths = findNumPadShortestPaths()
        val dirPadShortestPaths = findDirPadShortestPaths()
        return input.sumOf { sequence ->
            val numPadSequences: List<String> = findPaths(
                from = 'A',
                sequence = sequence,
                paths = numPadShortestPaths,
            ).map { path -> path.map { it.char }.joinToString(separator = "") }
            val dirPadSequences1: List<String> = numPadSequences.flatMap { numPadSequence ->
                findPaths(
                    from = 'A',
                    sequence = numPadSequence,
                    paths = dirPadShortestPaths,
                ).map { path -> path.map { it.char }.joinToString(separator = "") }
            }
            val dirPadSequences2: List<String> = dirPadSequences1.flatMap { dirPadSequence1 ->
                findPaths(
                    from = 'A',
                    sequence = dirPadSequence1,
                    paths = dirPadShortestPaths,
                ).map { path -> path.map { it.char }.joinToString(separator = "") }
            }
            dirPadSequences2.minOf { it.length } * sequence.substringBefore('A').toInt()
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    val testInput = readInput("Day21_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day21")
    part1(input).println()
    part2(input).println()
}
