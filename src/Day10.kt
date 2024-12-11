fun main() {
    fun toIntMap(input: List<String>): Array<Array<Int>> {
        return Array(input.size) { i ->
            Array(input[i].length) { j ->
                input[i][j].digitToIntOrNull() ?: -1
            }
        }
    }

    fun searchTrails(
        input: Array<Array<Int>>,
        i: Int,
        j: Int,
        expectedNode: Int,
    ): List<Pair<Int, Int>> {
        if (i !in input.indices) return emptyList()
        if (j !in input[i].indices) return emptyList()
        val item = input[i][j]
        if (item != expectedNode) return emptyList()
        if (item == 9) {
            return listOf(i to j)
        }
        return emptyList<Pair<Int, Int>>() +
                searchTrails(input, i + 1, j, expectedNode + 1) +
                searchTrails(input, i - 1, j, expectedNode + 1) +
                searchTrails(input, i, j + 1, expectedNode + 1) +
                searchTrails(input, i, j - 1, expectedNode + 1)
    }

    fun part1(input: Array<Array<Int>>): Long {
        var sum = 0L
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] != 0) continue
                sum += searchTrails(input, i, j, 0).distinct().size
            }
        }
        return sum
    }

    fun part1(input: List<String>): Long {
        return part1(toIntMap(input))
    }

    fun part2(input: Array<Array<Int>>): Long {
        var sum = 0L
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] != 0) continue
                sum += searchTrails(input, i, j, 0).size
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        return part2(toIntMap(input))
    }

    val testInput = readInput("Day10_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
