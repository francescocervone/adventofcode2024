fun main() {
    fun isSafe(levels: List<Int>): Boolean {
        val size = levels.size
        var direction = 0
        for (i in 0..<(size - 1)) {
            val safety = safety(levels[i], levels[i + 1], direction)
            if (!safety.isSafe) {
                return false
            } else {
                direction = safety.direction
            }
        }
        return true
    }

    fun part1(input: List<String>): Int {
        return input.fold(0) { safeReportsCount, report ->
            val levels = report.split(" ").map { it.toInt() }
            if (isSafe(levels)) {
                safeReportsCount + 1
            } else {
                safeReportsCount
            }
        }
    }

    fun part2(input: List<String>): Int {
        return input.fold(0) { safeReportsCount, report ->
            val levels = report.split(" ").map { it.toInt() }
            if (isSafe(levels)) {
                safeReportsCount + 1
            } else {
                repeat(levels.size) { i ->
                    val levelsWithoutI = levels.filterIndexed { index, _ -> index != i }
                    if (isSafe(levelsWithoutI)) {
                        return@fold safeReportsCount + 1
                    }
                }
                safeReportsCount
            }
        }
    }

    val testInput = readInput("Day02_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

private data class Result(val isSafe: Boolean, val direction: Int)

private fun safety(v1: Int, v2: Int, direction: Int): Result {
    val delta = v2 - v1
    if (delta == 0) return Result(false, direction)
    if (delta > 3) return Result(false, direction)
    if (delta < -3) return Result(false, direction)
    return if (direction == -1) {
        if (delta > 0) Result(false, direction)
        else Result(true, direction)
    } else if (direction == 1) {
        if (delta < 0) Result(false, direction)
        else Result(true, direction)
    } else if (direction == 0) {
        if (delta > 0) Result(true, 1)
        else Result(true, -1)
    } else {
        throw IllegalStateException()
    }
}