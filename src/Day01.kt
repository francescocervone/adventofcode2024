import kotlin.math.absoluteValue

fun main() {
    fun part1(input: List<String>): Int {
        val leftList = mutableListOf<Int>()
        val rightList = mutableListOf<Int>()
        input.forEach { line ->
            val (left, right) = line.split("   ").map { it.toInt() }
            leftList += left
            rightList += right
        }
        leftList.sort()
        rightList.sort()
        return leftList.foldIndexed(0) { i, totalDistance, _ ->
            val left = leftList[i]
            val right = rightList[i]
            val distance = (right - left).absoluteValue
            totalDistance + distance
        }
    }

    fun part2(input: List<String>): Int {
        val leftList = mutableListOf<Int>()
        val occurrences = mutableMapOf<Int, Int>().withDefault { 0 }
        input.forEach { line ->
            val (left, right) = line.split("   ").map { it.toInt() }
            leftList += left
            occurrences[right] = occurrences.getValue(right) + 1
        }
        return leftList.fold(0) { score, value ->
            score + (occurrences.getValue(value) * value)
        }
    }

    val testInput = readInput("Day01_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
