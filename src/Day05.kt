fun main() {
    fun generateCompareTo(rules: List<String>): (Int, Int) -> Int {
        val compareTo = mutableMapOf<Pair<Int, Int>, Int>()
        rules.forEach { rule ->
            val (v1, v2) = rule.split('|').map { it.toInt() }
            compareTo[v1 to v2] = -1
            compareTo[v2 to v1] = 1
        }
        return { v1, v2 ->
            compareTo[v1 to v2] ?: throw IllegalStateException("Ordering rule not found for $v1 and $v2")
        }
    }

    fun part1(input: List<String>): Int {
        val blank = input.indexOfFirst { it.isBlank() }
        val orderingRules = input.subList(0, blank)
        val updates = input.subList(blank + 1, input.size)
        val compareTo = generateCompareTo(orderingRules)
        return updates.fold(0) { sum, update ->
            val originalUpdate = update.split(',').map { it.toInt() }
            val sorted = originalUpdate.sortedWith { v1, v2 -> compareTo(v1, v2) }
            if (sorted == originalUpdate) {
                val middle = sorted[sorted.size / 2]
                sum + middle
            } else {
                sum
            }
        }
    }

    fun part2(input: List<String>): Int {
        val blank = input.indexOfFirst { it.isBlank() }
        val orderingRules = input.subList(0, blank)
        val updates = input.subList(blank + 1, input.size)
        val compareTo = generateCompareTo(orderingRules)
        return updates.fold(0) { sum, update ->
            val originalUpdate = update.split(',').map { it.toInt() }
            val sorted = originalUpdate.sortedWith { v1, v2 -> compareTo(v1, v2) }
            if (sorted != originalUpdate) {
                val middle = sorted[sorted.size / 2]
                sum + middle
            } else {
                sum
            }
        }
    }

    val testInput = readInput("Day05_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
