fun main() {
    fun Long.mix(secret: Long): Long = this xor secret

    fun Long.prune(): Long = this.mod(16777216L)

    fun nextSecret(secret: Long): Long {
        val s1 = (secret * 64).mix(secret).prune()
        val s2 = (s1 / 32).mix(s1).prune()
        return (s2 * 2048).mix(s2).prune()
    }

    fun secret(secret: Long, iterations: Int): Long {
        var current = secret
        repeat(iterations) {
            current = nextSecret(current)
        }
        return current
    }

    fun part1(input: List<String>): Long {
        return input.map { it.toLong() }
            .map { secret -> secret(secret, 2000) }
            .sum()
    }

    fun secretsSequence(secret: Long, iterations: Int): List<Long> {
        val secrets = mutableListOf(secret)
        var current = secret
        repeat(iterations) {
            current = nextSecret(current)
            secrets += current
        }
        return secrets
    }

    data class SequenceItem(
        val bananas: Int,
        val change: Int,
    )

    data class ChangeSequence(val sequence: List<Int>)

    class ChangeSequences {
        private val map: MutableMap<ChangeSequence, Int> = mutableMapOf()

        fun bananas(sequence: ChangeSequence): Int? = map[sequence]

        fun recordSequence(sequence: ChangeSequence, bananas: Int) {
            val existingBananas = map[sequence]
            if (existingBananas == null) {
                map[sequence] = bananas
            }
        }

        fun sequences(): Set<ChangeSequence> = map.keys
    }

    fun part2(input: List<String>): Long {
        val priceSequences = input.map { it.toLong() }
            .map { secret -> secretsSequence(secret, 2000) }
            .map { sequence -> sequence.map { secret -> secret.toString().last().digitToInt() } }
            .map { sequence ->
                sequence.fold(emptyList<Pair<Int, Int?>>()) { list, bananas ->
                    val last = list.lastOrNull()
                    if (last == null) {
                        list + (bananas to null)
                    } else {
                        list + (bananas to (bananas - last.first))
                    }
                }.mapNotNull { item ->
                    item.second?.let { SequenceItem(item.first, it) }
                }
            }
        val changeSequences = priceSequences.map { priceSequence ->
            val changeSequences = ChangeSequences()
            for (i in 0..<priceSequence.size - 4) {
                val subsequence = priceSequence.subList(i, i + 4)
                val bananas = subsequence.last().bananas
                val changeSequence = ChangeSequence(subsequence.map { it.change })
                changeSequences.recordSequence(changeSequence, bananas)
            }
            changeSequences
        }
        val allChangeSequences =
            changeSequences.fold(emptySet<ChangeSequence>()) { set, sequence -> set + sequence.sequences() }
        return allChangeSequences.maxOf { changeSequence ->
            changeSequences.sumOf { sequences ->
                sequences.bananas(changeSequence) ?: 0
            }
        }.toLong()
    }

    val testInput = readInput("Day22_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}
