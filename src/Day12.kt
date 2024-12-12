private data class PlantLocation(
    val row: Int,
    val column: Int,
) {
    override fun toString(): String {
        return "[$row, $column]"
    }
}

private data class PlantRegion(
    val perimeter: Int,
    val area: Int,
    val items: Set<PlantLocation>,
    val leftBoundaries: Set<PlantLocation>,
    val rightBoundaries: Set<PlantLocation>,
    val topBoundaries: Set<PlantLocation>,
    val bottomBoundaries: Set<PlantLocation>,
)

private sealed class RegionResult {
    data class Success(val region: PlantRegion) : RegionResult()
    data class Boundary(val location: PlantLocation) : RegionResult()
    data object AlreadyVisited : RegionResult()
}

fun main() {

    fun calculateRegion(
        garden: List<String>,
        i: Int,
        j: Int,
        iRange: IntRange,
        jRange: IntRange,
        plant: Char,
        visited: MutableSet<Pair<Int, Int>>,
    ): RegionResult {
        if (i !in iRange) return RegionResult.Boundary(PlantLocation(i, j))
        if (j !in jRange) return RegionResult.Boundary(PlantLocation(i, j))
        if (garden[i][j] != plant) return RegionResult.Boundary(PlantLocation(i, j))
        if (i to j in visited) return RegionResult.AlreadyVisited
        visited.add(i to j)
        val left = calculateRegion(garden, i, j - 1, iRange, jRange, plant, visited)
        val right = calculateRegion(garden, i, j + 1, iRange, jRange, plant, visited)
        val up = calculateRegion(garden, i - 1, j, iRange, jRange, plant, visited)
        val down = calculateRegion(garden, i + 1, j, iRange, jRange, plant, visited)
        val allSides = listOf(up, down, left, right)
        val perimeter = allSides.sumOf { result ->
            when (result) {
                RegionResult.AlreadyVisited -> 0
                is RegionResult.Boundary -> 1
                is RegionResult.Success -> result.region.perimeter
            }
        }
        val area = allSides.sumOf { result ->
            when (result) {
                RegionResult.AlreadyVisited -> 0
                is RegionResult.Boundary -> 0
                is RegionResult.Success -> result.region.area
            }
        }
        val items = allSides.filterIsInstance<RegionResult.Success>().flatMap { it.region.items }
        return RegionResult.Success(
            PlantRegion(
                perimeter = perimeter,
                area = 1 + area,
                items = setOf(PlantLocation(i, j)) + items,
                leftBoundaries = allSides.fold(emptySet()) { acc, result ->
                    when (result) {
                        RegionResult.AlreadyVisited -> acc
                        is RegionResult.Boundary -> if (result == left) acc + result.location else acc
                        is RegionResult.Success -> acc + result.region.leftBoundaries
                    }
                },
                rightBoundaries = allSides.fold(emptySet()) { acc, result ->
                    when (result) {
                        RegionResult.AlreadyVisited -> acc
                        is RegionResult.Boundary -> if (result == right) acc + result.location else acc
                        is RegionResult.Success -> acc + result.region.rightBoundaries
                    }
                },
                topBoundaries = allSides.fold(emptySet()) { acc, result ->
                    when (result) {
                        RegionResult.AlreadyVisited -> acc
                        is RegionResult.Boundary -> if (result == up) acc + result.location else acc
                        is RegionResult.Success -> acc + result.region.topBoundaries
                    }
                },
                bottomBoundaries = allSides.fold(emptySet()) { acc, result ->
                    when (result) {
                        RegionResult.AlreadyVisited -> acc
                        is RegionResult.Boundary -> if (result == down) acc + result.location else acc
                        is RegionResult.Success -> acc + result.region.bottomBoundaries
                    }
                },
            )
        )
    }

    fun calculateRegions(input: List<String>): List<PlantRegion> {
        val iRange = input.indices
        val jRange = input[0].indices
        val regions = mutableListOf<PlantRegion>()
        val visitedPlants = mutableSetOf<Pair<Int, Int>>()
        input.forEachIndexed { i, row ->
            row.forEachIndexed { j, plant ->
                val result = calculateRegion(
                    garden = input,
                    i = i,
                    j = j,
                    iRange = iRange,
                    jRange = jRange,
                    plant = plant,
                    visited = visitedPlants
                )
                if (result is RegionResult.Success) {
                    regions += result.region
                }
            }
        }
        return regions
    }

    fun part1(input: List<String>): Long {
        val regions = calculateRegions(input)
        return regions.fold(0) { acc, region ->
            acc + (region.perimeter * region.area)
        }
    }

    fun PlantRegion.sides(): Int {
        val leftSides = leftBoundaries
            // 0 -> [(1, 0), (2, 0)]
            // 1 -> [(5, 1), (3, 1)]
            .groupBy { it.column }
            // 0 -> [1, 2]
            // 1 -> [5, 3]
            .mapValues { (_, locations) -> locations.map { it.row } }
            // [ [1, 2], [5, 3] ]
            .values
            // [ [1, 2], [3, 5] ]
            .map { it.sorted() }
            .fold(0) { acc, column ->
                var currentRow = column[0]
                var i = 1
                var sidesCount = 1
                while (i < column.size) {
                    if (column[i] != currentRow + 1) {
                        sidesCount++
                    }
                    currentRow = column[i]
                    i++
                }
                acc + sidesCount
            }
        val rightSides = rightBoundaries
            .groupBy { it.column }
            .mapValues { (_, locations) -> locations.map { it.row } }
            .values
            .map { it.sorted() }
            .fold(0) { acc, column ->
                var currentRow = column[0]
                var i = 1
                var sidesCount = 1
                while (i < column.size) {
                    if (column[i] != currentRow + 1) {
                        sidesCount++
                    }
                    currentRow = column[i]
                    i++
                }
                acc + sidesCount
            }
        val topSides = topBoundaries
            .groupBy { it.row }
            .mapValues { (_, locations) -> locations.map { it.column } }
            .values
            .map { it.sorted() }
            .fold(0) { acc, row ->
                var currentColumn = row[0]
                var i = 1
                var sidesCount = 1
                while (i < row.size) {
                    if (row[i] != currentColumn + 1) {
                        sidesCount++
                    }
                    currentColumn = row[i]
                    i++
                }
                acc + sidesCount
            }
        val bottomSides = bottomBoundaries
            .groupBy { it.row }
            .mapValues { (_, locations) -> locations.map { it.column } }
            .values
            .map { it.sorted() }
            .fold(0) { acc, row ->
                var currentColumn = row[0]
                var i = 1
                var sidesCount = 1
                while (i < row.size) {
                    if (row[i] != currentColumn + 1) {
                        sidesCount++
                    }
                    currentColumn = row[i]
                    i++
                }
                acc + sidesCount
            }
        return leftSides + rightSides + topSides + bottomSides
    }

    fun part2(input: List<String>): Long {
        val regions = calculateRegions(input)
        return regions.filter { it.area > 0 }.fold(0) { acc, region ->
            acc + (region.sides() * region.area)
        }
    }

    val testInput = readInput("Day12_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
