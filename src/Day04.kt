fun main() {
    fun search(
        input: List<String>,
        i: Int,
        j: Int,
        iDirection: Int,
        jDirection: Int,
        iRange: IntRange,
        jRange: IntRange,
        stringToFind: String,
    ): Boolean {
        if (i !in iRange) return false
        if (j !in jRange) return false
        if ((i + iDirection * (stringToFind.length - 1)) !in iRange) return false
        if ((j + jDirection * (stringToFind.length - 1)) !in jRange) return false
        val string = buildString {
            repeat(stringToFind.length) { index ->
                append(input[i + index * iDirection][j + index * jDirection])
            }
        }
        return string == stringToFind
    }

    fun search(input: List<String>, i: Int, j: Int): Int {
        if (input[i][j] != 'X') return 0
        return listOf(
            search(input, i, j, 0, 1, input.indices, input[i].indices, "XMAS"),
            search(input, i, j, 0, -1, input.indices, input[i].indices, "XMAS"),
            search(input, i, j, 1, 0, input.indices, input[i].indices, "XMAS"),
            search(input, i, j, -1, 0, input.indices, input[i].indices, "XMAS"),
            search(input, i, j, 1, 1, input.indices, input[i].indices, "XMAS"),
            search(input, i, j, 1, -1, input.indices, input[i].indices, "XMAS"),
            search(input, i, j, -1, 1, input.indices, input[i].indices, "XMAS"),
            search(input, i, j, -1, -1, input.indices, input[i].indices, "XMAS")
        ).count { it }

    }

    fun part1(input: List<String>): Int {
        var count = 0
        for (i in input.indices) {
            for (j in input[i].indices) {
                count += search(input, i, j)
            }
        }
        return count
    }

    fun search2(input: List<String>, i: Int, j: Int): Boolean {
        if (input[i][j] != 'M' && input[i][j] != 'S') return false
        val leftToRight = search(input, i, j, 1, 1, input.indices, input[i].indices, "MAS") ||
                search(input, i, j, 1, 1, input.indices, input[i].indices, "SAM")
        val rightToLeft = search(input, i + 2, j, -1, 1, input.indices, input[i].indices, "MAS") ||
                search(input, i + 2, j, -1, 1, input.indices, input[i].indices, "SAM")
        return leftToRight && rightToLeft
    }

    fun part2(input: List<String>): Int {
        var count = 0
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (search2(input, i, j)) {
                    count++
                }
            }
        }
        return count
    }

    val testInput = readInput("Day04_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
