private enum class Quadrant {
    One, Two, Three, Four;
}

fun main() {
    data class Point(val x: Int, val y: Int)
    data class Robot(
        val position: Point,
        val velocity: Point,
    )


    fun Robot.quadrant(width: Int, height: Int): Quadrant? {
        val middleX = width / 2
        val middleY = height / 2
        if (position.x == middleX) return null
        if (position.y == middleY) return null
        if (position.x < middleX) {
            if (position.y < middleY) return Quadrant.One
            else return Quadrant.Three
        } else {
            if (position.y < middleY) return Quadrant.Two
            else return Quadrant.Four
        }
    }

    fun robots(
        input: List<String>,
        width: Int,
        height: Int,
        steps: Int,
    ): List<Robot> {
        val robots = input.map { line ->
            val (pos, vel) = line.split(" ")
            val (px, py) = pos.replace("p=", "").split(",").map { it.toInt() }
            val (vx, vy) = vel.replace("v=", "").split(",").map { it.toInt() }
            Robot(
                position = Point(px, py),
                velocity = Point(vx, vy)
            )
        }.map { robot ->
            robot.copy(
                position = Point(
                    (robot.position.x + (robot.velocity.x * steps)).mod(width),
                    (robot.position.y + (robot.velocity.y * steps)).mod(height),
                )
            )
        }
        return robots
    }

    fun part1(input: List<String>, width: Int, height: Int, steps: Int): Long {
        val robots = robots(input, width, height, steps)
        var quadrant1 = 0L
        var quadrant2 = 0L
        var quadrant3 = 0L
        var quadrant4 = 0L
        robots.forEach { robot ->
            when (robot.quadrant(width, height)) {
                Quadrant.One -> quadrant1++
                Quadrant.Two -> quadrant2++
                Quadrant.Three -> quadrant3++
                Quadrant.Four -> quadrant4++
                else -> Unit
            }
        }
        val map = Array(height) { Array(width) { "." } }
        robots.forEach { robot ->
            val pos = map[robot.position.y][robot.position.x]
            map[robot.position.y][robot.position.x] = when (pos) {
                "." -> "1"
                else -> (pos.toInt() + 1).toString()
            }
        }
        println(
            map.joinToString("\n") {
                it.joinToString(" ")
            }
        )
        return quadrant1 * quadrant2 * quadrant3 * quadrant4
    }

    fun part2(input: List<String>): Long {
        // Brute force not verified
        var i = 0
        while (true) {
            println(i)
            val robots = robots(input, 101, 103, i)
            val map = Array(103) { Array(101) { "." } }
            robots.forEach { robot ->
                val pos = map[robot.position.y][robot.position.x]
                map[robot.position.y][robot.position.x] = when (pos) {
                    "." -> "1"
                    else -> (pos.toInt() + 1).toString()
                }
            }
            println(
                map.joinToString("\n") {
                    it.joinToString(" ")
                }
            )
            i++
        }
    }

    val testInput = readInput("Day14_test")
    part1(testInput, 11, 7, 100).println()
    part2(testInput).println()

    val input = readInput("Day14")
    part1(input, 101, 103, 100).println()
    part2(input).println()
}
