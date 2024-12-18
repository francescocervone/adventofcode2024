import java.util.LinkedList

fun main() {
    data class Point(val x: Int, val y: Int)
    data class Iteration(
        val location: Point,
        val steps: Int,
    )

    fun bfs(fallenBytes: Set<Point>, size: Int): Int {
        val goal = Point(size - 1, size - 1)
        val queue = LinkedList<Iteration>()
        queue.add(Iteration(Point(0, 0), 0))
        val visitedPoints = mutableSetOf<Point>()
        while (queue.isNotEmpty()) {
            val (location, steps) = queue.removeFirst()
            if (location == goal) {
                return steps
            }

            val nextNodes = sequenceOf(
                location.copy(x = location.x - 1),
                location.copy(x = location.x + 1),
                location.copy(y = location.y - 1),
                location.copy(y = location.y + 1)
            ).filter { it.x >= 0 }
                .filter { it.x < size }
                .filter { it.y >= 0 }
                .filter { it.y < size }
                .filter { it !in visitedPoints }
                .filter { it !in fallenBytes }
            queue.addAll(nextNodes.map { Iteration(it, steps + 1) })
            visitedPoints += nextNodes
        }
        return -1
    }

    fun bytes(input: List<String>): List<Point> {
        return input.map { byte ->
            val (x, y) =byte.split(",").map { it.toInt() }
            Point(x, y)
        }
    }

    fun part1(input: List<String>, size: Int, elapsedNanos: Int): Long {
        val bytes = bytes(input)
        return bfs(bytes.subList(0, elapsedNanos).toSet(), size).toLong()
    }

    fun part2(input: List<String>, size: Int, elapsedNanos: Int): String {
        val bytes = bytes(input)
        val fallenBytes = bytes.subList(0, elapsedNanos).toMutableSet()
        bytes.subList(elapsedNanos, bytes.size).forEach { byte ->
            fallenBytes += byte
            val steps = bfs(fallenBytes, size)
            if (steps == -1) {
                return "${byte.x},${byte.y}"
            }
        }
        return ""
    }

    val testInput = readInput("Day18_test")
    part1(testInput, 7, 12).println()
    part2(testInput, 7, 12).println()

    val input = readInput("Day18")
    part1(input, 71, 1024).println()
    part2(input, 71, 1024).println()
}
