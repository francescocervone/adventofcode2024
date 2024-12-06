data class GuardLocation(val row: Int, val column: Int) {
    operator fun plus(other: GuardLocation): GuardLocation {
        return GuardLocation(row + other.row, column + other.column)
    }
}

enum class GuardDirection(val coordIncrement: GuardLocation) {
    Left(GuardLocation(0, -1)),
    Right(GuardLocation(0, 1)),
    Up(GuardLocation(-1, 0)),
    Down(GuardLocation(1, 0));

    fun turnRight(): GuardDirection {
        return when (this) {
            Left -> Up
            Right -> Down
            Up -> Right
            Down -> Left
        }
    }
}

fun main() {
    data class Guard(
        val location: GuardLocation,
        val direction: GuardDirection,
    )

    fun getGuard(input: Array<Array<Char>>): Guard {
        for (row in input.indices) {
            for (column in input[row].indices) {
                val location = GuardLocation(row, column)
                val guard = when (input[row][column]) {
                    '^' -> Guard(location, GuardDirection.Up)
                    '>' -> Guard(location, GuardDirection.Right)
                    'v' -> Guard(location, GuardDirection.Down)
                    '<' -> Guard(location, GuardDirection.Left)
                    else -> null
                }
                if (guard != null) return guard
            }
        }
        throw IllegalStateException("Guard not found")
    }

    fun getMap(input: List<String>) = Array(input.size) { i ->
        Array(input[i].length) { j -> input[i][j] }
    }

    fun Char.turnRight() = when (this) {
        '^' -> '>'
        '>' -> 'v'
        'v' -> '<'
        '<' -> '^'
        else -> throw IllegalStateException("Unexpected char $this")
    }

    fun print(map: Array<Array<Char>>) {
        for (i in map.indices) {
            for (j in map[i].indices) {
                print(map[i][j])
                print(" ")
            }
            println()
        }
    }

    fun makeGuardWalk(map: Array<Array<Char>>) {
        var guard = getGuard(map)
        val rowsRange = map.indices
        val columnsRange = map[0].indices
        while (true) {
            val location = guard.location
            val direction = guard.direction
            val nextLocation = location + direction.coordIncrement
            if (nextLocation.row !in rowsRange) {
                map[location.row][location.column] = 'X'
                return
            }
            if (nextLocation.column !in columnsRange) {
                map[location.row][location.column] = 'X'
                return
            }
            guard = when (val nextValue = map[nextLocation.row][nextLocation.column]) {
                '.' -> {
                    map[nextLocation.row][nextLocation.column] = map[location.row][location.column]
                    map[location.row][location.column] = 'X'
                    guard.copy(location = nextLocation)
                }

                'X' -> {
                    map[nextLocation.row][nextLocation.column] = map[location.row][location.column]
                    map[location.row][location.column] = 'X'
                    guard.copy(location = nextLocation)
                }

                '#' -> {
                    map[location.row][location.column] = map[location.row][location.column].turnRight()
                    guard.copy(direction = direction.turnRight())
                }

                else -> throw IllegalStateException("Unexpected char $nextValue")
            }

        }
    }


    fun part1(input: List<String>): Int {
        val map = getMap(input)
        makeGuardWalk(map)
        var count = 0
        for (i in map.indices) {
            for (j in map[i].indices) {
                if (map[i][j] == 'X') count++
            }
        }
        return count
    }

    fun isGuardStuckInLoop(map: Array<Array<Char>>): Boolean {
        var guard = getGuard(map)
        val turningPoints = mutableSetOf<GuardLocation>()
        val pathLocations = mutableSetOf<Guard>()
        val rowsRange = map.indices
        val columnsRange = map[0].indices
        while (true) {
            if (guard in pathLocations) return true
            pathLocations += guard
            val location = guard.location
            val direction = guard.direction
            val nextLocation = location + direction.coordIncrement
            if (nextLocation.row !in rowsRange) {
                return false
            }
            if (nextLocation.column !in columnsRange) {
                return false
            }
            guard = when (val nextValue = map[nextLocation.row][nextLocation.column]) {
                '.' -> {
                    map[nextLocation.row][nextLocation.column] = map[location.row][location.column]
                    map[location.row][location.column] = if (location in turningPoints) {
                        '+'
                    } else {
                        when (direction) {
                            GuardDirection.Left -> '-'
                            GuardDirection.Right -> '-'
                            GuardDirection.Up -> '|'
                            GuardDirection.Down -> '|'
                        }
                    }
                    guard.copy(location = nextLocation)
                }

                '#' -> {
                    turningPoints += location
                    map[location.row][location.column] = map[location.row][location.column].turnRight()
                    guard.copy(direction = direction.turnRight())
                }

                'O' -> {
                    turningPoints += location
                    map[location.row][location.column] = map[location.row][location.column].turnRight()
                    guard.copy(direction = direction.turnRight())
                }

                '|' -> {
                    when (direction) {
                        GuardDirection.Up,
                        GuardDirection.Down,
                            -> {
                            map[nextLocation.row][nextLocation.column] = map[location.row][location.column]
                            map[location.row][location.column] = if (location in turningPoints) {
                                '+'
                            } else {
                                '|'
                            }
                            guard.copy(location = nextLocation)
                        }

                        GuardDirection.Left,
                        GuardDirection.Right,
                            -> {
                            map[nextLocation.row][nextLocation.column] = map[location.row][location.column]
                            map[location.row][location.column] = if (location in turningPoints) {
                                '+'
                            } else {
                                '-'
                            }
                            turningPoints += nextLocation
                            guard.copy(location = nextLocation)
                        }
                    }
                }

                '-' -> {
                    when (direction) {
                        GuardDirection.Left,
                        GuardDirection.Right,
                            -> {
                            map[nextLocation.row][nextLocation.column] = map[location.row][location.column]
                            map[location.row][location.column] = if (location in turningPoints) {
                                '+'
                            } else {
                                '-'
                            }
                            guard.copy(location = nextLocation)
                        }

                        GuardDirection.Up,
                        GuardDirection.Down,
                            -> {
                            map[nextLocation.row][nextLocation.column] = map[location.row][location.column]
                            map[location.row][location.column] = if (location in turningPoints) {
                                '+'
                            } else {
                                '|'
                            }
                            turningPoints += nextLocation
                            guard.copy(location = nextLocation)
                        }
                    }
                }

                '+' -> {
                    map[nextLocation.row][nextLocation.column] = map[location.row][location.column]
                    turningPoints += nextLocation
                    map[location.row][location.column] = if (location in turningPoints) {
                        '+'
                    } else {
                        when (direction) {
                            GuardDirection.Left -> '-'
                            GuardDirection.Right -> '-'
                            GuardDirection.Up -> '|'
                            GuardDirection.Down -> '|'
                        }
                    }
                    guard.copy(location = nextLocation)
                }


                else -> throw IllegalStateException("Unexpected char $nextValue")
            }
        }
    }

    fun part2(input: List<String>): Int {
        val validLocations = mutableSetOf<Pair<Int, Int>>()
        var loops = 0
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] == '.') {
                    val map = getMap(input)
                    map[i][j] = 'O'
                    if (isGuardStuckInLoop(map)) {
                        validLocations += i to j
                        loops++
                    }
                }
            }
        }
        return loops
    }

    val testInput = readInput("Day06_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
