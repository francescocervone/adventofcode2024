fun main() {
    fun isPossible(
        design: String,
        patterns: Set<String>,
        memo: MutableMap<String, Boolean>,
    ): Boolean {
        memo[design]?.let { return it }
        if (design.isEmpty()) return true
        patterns.forEach { pattern ->
            if (design.startsWith(pattern)) {
                if (isPossible(design.substring(pattern.length), patterns, memo)) {
                    memo[design] = true
                    return true
                }
            }
        }
        memo[design] = false
        return false
    }

    fun part1(input: List<String>): Long {
        val patterns = input.first().split(", ").toSet()
        val designs = input.subList(2, input.size)
        val memo = mutableMapOf<String, Boolean>()
        return designs.count { design ->
            isPossible(design, patterns, memo)
        }.toLong()
    }

    fun possibilities(
        design: String,
        patterns: Set<String>,
        memo: MutableMap<String, Long>,
    ): Long {
        memo[design]?.let { return it }
        if (design.isEmpty()) return 1
        return patterns.sumOf { pattern ->
            if (design.startsWith(pattern)) {
                possibilities(design.substring(pattern.length), patterns, memo)
            } else {
                0
            }
        }.also { memo[design] = it }
    }

    fun part2(input: List<String>): Long {
        val patterns = input.first().split(", ").toSet()
        val designs = input.subList(2, input.size)
        val memo = mutableMapOf<String, Long>()
        return designs.sumOf { design ->
            possibilities(design, patterns, memo)
        }
    }

    val testInput = readInput("Day19_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
