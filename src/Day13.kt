fun main() {
    data class Point(val x: Long, val y: Long)

    data class Config(
        val a: Point,
        val b: Point,
        val prize: Point,
    )

    fun config(input: List<String>): List<Config> {
        val configs = mutableListOf<Config>()
        var i = 0
        while (i < input.size) {
            val a = input[i].replace("Button A: ", "")
                .replace("X+", "")
                .replace("Y+", "")
                .split(", ")
                .let { Point(it[0].toLong(), it[1].toLong()) }
            val b = input[i + 1].replace("Button B: ", "")
                .replace("X+", "")
                .replace("Y+", "")
                .split(", ")
                .let { Point(it[0].toLong(), it[1].toLong()) }
            val prize = input[i + 2].replace("Prize: ", "")
                .replace("X=", "")
                .replace("Y=", "")
                .split(", ")
                .let { Point(it[0].toLong(), it[1].toLong()) }
            configs += Config(a, b, prize)
            i += 4
        }
        return configs
    }

    fun Config.location(aSteps: Long, bSteps: Long): Point {
        return Point(
            a.x * aSteps + b.x * bSteps,
            a.y * aSteps + b.y * bSteps,
        )
    }

    fun calculateRequiredSteps(config: Config): Pair<Long, Long> {
        val b = (config.a.x * config.prize.y - config.a.y * config.prize.x) /
                (-1 * config.a.y * config.b.x + config.a.x * config.b.y)
        val a = (config.prize.x - config.b.x * b) / config.a.x
        return a to b
    }

    fun part1(input: List<String>): Long {
        val configs = config(input)
        return configs.fold(0) { acc, config ->
            val (a, b) = calculateRequiredSteps(config)
            if (config.location(a, b) == config.prize) {
                acc + (a * 3 + b)
            } else {
                acc
            }
        }
    }

    fun part2(input: List<String>): Long {
        val configs = config(input).map {
            it.copy(
                prize = it.prize.copy(
                    x = it.prize.x + 10000000000000,
                    y = it.prize.y + 10000000000000
                )
            )
        }
        return configs.fold(0) { acc, config ->
            val (a, b) = calculateRequiredSteps(config)
            if (config.location(a, b) == config.prize) {
                acc + (a * 3 + b)
            } else {
                acc
            }
        }
    }

    val testInput = readInput("Day13_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
