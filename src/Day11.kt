fun main() {
    fun countStones(stone: String, iterations: Int, memo: MutableMap<Pair<String, Int>, Long>): Long {
        if (iterations == 0) return 0
        memo[stone to iterations]?.let { return it }
        return when {
            stone == "0" -> {
                countStones("1", iterations - 1, memo)
            }

            stone.length % 2 == 0 -> {
                val stone1 = stone.substring(0, stone.length / 2).toLong().toString()
                val stone2 = stone.substring(stone.length / 2, stone.length).toLong().toString()
                val count1 = countStones(stone1, iterations - 1, memo)
                val count2 = countStones(stone2, iterations - 1, memo)
                count1 + count2 + 1
            }

            else -> {
                countStones((stone.toLong() * 2024).toString(), iterations - 1, memo)
            }
        }.also { memo[stone to iterations] = it }
    }

    fun countStones(input: String, iterations: Int): Long {
        val stones = input.split(" ").toMutableList()
        return stones.fold(0L) { acc, stone ->
            acc + 1 + countStones(stone, iterations, mutableMapOf())
        }
    }

    fun part1(input: String): Long {
        return countStones(input, 25)
    }

    fun part2(input: String): Long {
        return countStones(input, 75)
    }

    val testInput = readInput("Day11_test").first()
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day11").first()
    part1(input).println()
    part2(input).println()
}
