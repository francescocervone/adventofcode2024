fun main() {
    data class Result(
        val value: Int,
        val skipBy: Int,
    )

    fun applyMultiplication(string: String, index: Int): Result {
        var substring = string.substring(index)
        if (!substring.startsWith("mul(")) return Result(0, 1)
        if (substring.length < 5) return Result(0, 4)
        substring = substring.substring(4)
        val commaIndex = substring.indexOf(',')
        if (commaIndex == -1) return Result(0, 4)
        if (commaIndex > 3) return Result(0, 4)
        val value1 = substring.substring(0, commaIndex).toIntOrNull() ?: return Result(0, 4)
        substring = substring.substring(commaIndex + 1)
        val endParenthesisIndex = substring.indexOf(')')
        if (endParenthesisIndex == -1) return Result(0, 4)
        if (endParenthesisIndex > 3) return Result(0, 4)
        val value2 = substring.substring(0, endParenthesisIndex).toIntOrNull() ?: return Result(0, 4)
        return Result(value1 * value2, 4 + commaIndex + 1 + endParenthesisIndex + 1)
    }

    fun part1(input: List<String>): Int {
        var sum = 0
        input.forEach { line ->
            var index = 0
            while (index < line.length) {
                val char = line[index]
                if (char == 'm') {
                    val result = applyMultiplication(line, index)
                    sum += result.value
                    index += result.skipBy
                } else {
                    index++
                }
            }
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        var enabled = true
        var sum = 0
        input.forEach { line ->
            var index = 0
            while (index < line.length) {
                val char = line[index]
                if (char == 'm') {
                    val result = applyMultiplication(line, index)
                    if (enabled) {
                        sum += result.value
                    }
                    index += result.skipBy
                } else if (char == 'd') {
                    if (line.length >= index + 7) {
                        val dont = line.substring(index, index + 7)
                        if (dont == "don't()") {
                            enabled = false
                            index += 7
                        } else if (dont.startsWith("do()")) {
                            enabled = true
                            index += 4
                        } else {
                            index++
                        }
                    } else if (line.length >= index + 4) {
                        val `do` = line.substring(index, index + 4)
                        if (`do` == "do()") {
                            enabled = true
                            index += 4
                        } else {
                            index++
                        }
                    } else {
                        index++
                    }
                } else {
                    index++
                }
            }
        }
        return sum
    }

    val testInput = readInput("Day03_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}