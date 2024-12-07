private typealias Operator = (Long, Long) -> Long

fun main() {
    fun getOperatorCombinations(size: Int, operators: List<Operator>): List<List<Operator>> {
        if (size == 0) return emptyList()
        if (size == 1) return operators.map { listOf(it) }
        val previousCombinations = getOperatorCombinations(size - 1, operators)
        return previousCombinations.fold(mutableListOf()) { acc, previousCombination ->
            operators.forEach { acc.add(previousCombination + it) }
            acc
        }
    }

    data class Equation(
        val expectedResult: Long,
        val items: List<Long>,
    )

    fun convertEquation(line: String): Equation {
        val (result, items) = line.split(": ")
        return Equation(
            expectedResult = result.toLong(),
            items = items.split(" ").map { it.toLong() }
        )
    }

    fun add(v1: Long, v2: Long) = v1 + v2
    fun multiply(v1: Long, v2: Long) = v1 * v2
    fun concat(v1: Long, v2: Long) = (v1.toString() + v2.toString()).toLong()

    fun resolve(items: List<Long>, operators: List<Operator>): Long {
        if (operators.isEmpty()) return items[0]
        val operator = operators.first()
        return resolve(
            listOf(
                operator(items[0], items[1]),
                *items.subList(2, items.size).toTypedArray()
            ),
            operators.subList(1, operators.size)
        )
    }


    fun verify(equation: Equation, operators: List<Operator>): Boolean {
        val actualResult = resolve(equation.items, operators)
        return (actualResult == equation.expectedResult)
    }

    fun part1(input: List<String>): Long {
        val cacheCombinations = mutableMapOf<Int, List<List<Operator>>>()
        var sum = 0L
        input.forEach { line ->
            val equation = convertEquation(line)
            val operatorCombinations =
                cacheCombinations[equation.items.size - 1]
                    ?: getOperatorCombinations(equation.items.size - 1, listOf(::add, ::multiply)).also {
                        cacheCombinations[equation.items.size - 1] = it
                    }
            for (operators in operatorCombinations) {
                if (verify(equation, operators)) {
                    sum += equation.expectedResult
                    break
                }
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        val cacheCombinations = mutableMapOf<Int, List<List<Operator>>>()
        var sum = 0L
        input.forEach { line ->
            val equation = convertEquation(line)
            val operatorCombinations =
                cacheCombinations[equation.items.size - 1]
                    ?: getOperatorCombinations(equation.items.size - 1, listOf(::add, ::multiply, ::concat)).also {
                        cacheCombinations[equation.items.size - 1] = it
                    }
            for (operators in operatorCombinations) {
                if (verify(equation, operators)) {
                    sum += equation.expectedResult
                    break
                }
            }
        }
        return sum
    }

    val testInput = readInput("Day07_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
