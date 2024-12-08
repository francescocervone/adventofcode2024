private data class Location(val i: Int, val j: Int)

fun main() {
    fun getFrequencies(input: List<String>): Map<Char, List<Location>> {
        val frequencies = mutableMapOf<Char, MutableList<Location>>().withDefault { mutableListOf() }
        for (i in input.indices) {
            for (j in input[i].indices) {
                val frequency = input[i][j]
                if (frequency != '.') {
                    val locations = frequencies.getValue(frequency)
                    locations.add(Location(i, j))
                    frequencies[frequency] = locations
                }
            }
        }
        return frequencies
    }

    fun inRange(location: Location, iRange: IntRange, jRange: IntRange): Boolean {
        if (location.i !in iRange) return false
        if (location.j !in jRange) return false
        return true
    }


    fun part1(input: List<String>): Long {
        val frequencies = getFrequencies(input)
        val antinodes = mutableSetOf<Location>()
        val iRange = input[0].indices
        val jRange = input.indices
        frequencies.forEach { (_, locations) ->
            for (i in locations.indices) {
                for (j in (i + 1)..locations.lastIndex) {
                    val location1 = locations[i]
                    val location2 = locations[j]
                    val distance = Location(
                        location2.i - location1.i,
                        location2.j - location1.j,
                    )
                    val antinode1 = Location(
                        location1.i - distance.i,
                        location1.j - distance.j
                    )
                    if (inRange(antinode1, iRange, jRange)) {
                        antinodes += antinode1
                    }
                    val antinode2 = Location(
                        location2.i + distance.i,
                        location2.j + distance.j
                    )
                    if (inRange(antinode2, iRange, jRange)) {
                        antinodes += antinode2
                    }
                }
            }
        }
        return antinodes.size.toLong()
    }

    fun backwardAntinodes(
        location: Location,
        distance: Location,
        iRange: IntRange,
        jRange: IntRange,
    ): Set<Location> {
        val antinode = Location(
            location.i - distance.i,
            location.j - distance.j
        )
        return if (inRange(antinode, iRange, jRange)) {
            backwardAntinodes(antinode, distance, iRange, jRange) + antinode
        } else {
            emptySet()
        }
    }

    fun forwardAntinodes(
        location: Location,
        distance: Location,
        iRange: IntRange,
        jRange: IntRange,
    ): Set<Location> {
        val antinode = Location(
            location.i + distance.i,
            location.j + distance.j
        )
        return if (inRange(antinode, iRange, jRange)) {
            forwardAntinodes(antinode, distance, iRange, jRange) + antinode
        } else {
            emptySet()
        }
    }

    fun part2(input: List<String>): Long {
        val frequencies = getFrequencies(input)
        val antinodes = mutableSetOf<Location>()
        val iRange = input[0].indices
        val jRange = input.indices
        frequencies.forEach { (_, locations) ->
            for (i in locations.indices) {
                for (j in (i + 1)..locations.lastIndex) {
                    val location1 = locations[i]
                    val location2 = locations[j]
                    val distance = Location(
                        location2.i - location1.i,
                        location2.j - location1.j,
                    )
                    antinodes.add(location1)
                    antinodes.add(location2)
                    antinodes.addAll(backwardAntinodes(location1, distance, iRange, jRange))
                    antinodes.addAll(forwardAntinodes(location2, distance, iRange, jRange))
                }
            }
        }
        return antinodes.size.toLong()
    }

    val testInput = readInput("Day08_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
