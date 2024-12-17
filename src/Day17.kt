import kotlin.math.pow

fun main() {
    data class Computer(
        private var registerA: Int,
        private var registerB: Int,
        private var registerC: Int,
        private val instructions: List<Int>,
        private val output: MutableList<Int>,
        private var instructionPointer: Int = 0,
    ) {
        private fun combo(operand: Int): Int {
            return when {
                operand in 0..3 -> operand
                operand == 4 -> registerA
                operand == 5 -> registerB
                operand == 6 -> registerC
                else -> throw IllegalStateException("Unknown combo operand $operand")
            }
        }

        private fun adv(operand: Int) {
            val numerator = registerA
            val denominator = 2.0.pow(combo(operand).toDouble())
            registerA = (numerator / denominator).toLong().toInt()
            nextInstruction()
        }

        private fun bxl(operand: Int) {
            registerB = registerB xor operand
            nextInstruction()
        }

        private fun bst(operand: Int) {
            registerB = combo(operand).mod(8)
            nextInstruction()
        }

        private fun jnz(operand: Int) {
            if (registerA == 0) {
                nextInstruction()
                return
            }
            instructionPointer = operand
        }

        private fun bxc(operand: Int) {
            registerB = registerB xor registerC
            nextInstruction()
        }

        private fun out(operand: Int) {
            output += combo(operand).mod(8)
            nextInstruction()
        }

        private fun bdv(operand: Int) {
            val numerator = registerA
            val denominator = Math.pow(2.0, combo(operand).toDouble())
            registerB = (numerator / denominator).toLong().toInt()
            nextInstruction()
        }

        private fun cdv(operand: Int) {
            val numerator = registerA
            val denominator = Math.pow(2.0, combo(operand).toDouble())
            registerC = (numerator / denominator).toLong().toInt()
            nextInstruction()
        }

        private fun nextInstruction() {
            instructionPointer += 2
        }

        fun execute(
            strictMode: Boolean = false,
            failedStates: Set<Computer> = setOf(),
            executionStates: MutableSet<Computer> = mutableSetOf(),
        ) {
            while (instructionPointer < instructions.size) {
                if (strictMode) {
                    if (output.isNotEmpty()) {
                        if (output.size > instructions.size) return
                        if (output.last() != instructions[output.lastIndex]) return
                    }
                    if (this in failedStates) {
                        return
                    }
                    executionStates += this
                }
                val opcode = instructions[instructionPointer]
                val operand = instructions[instructionPointer + 1]
                when (opcode) {
                    0 -> adv(operand)
                    1 -> bxl(operand)
                    2 -> bst(operand)
                    3 -> jnz(operand)
                    4 -> bxc(operand)
                    5 -> out(operand)
                    6 -> bdv(operand)
                    7 -> cdv(operand)
                    else -> throw IllegalStateException("Unknown opcode $opcode")
                }
            }
        }
    }

    fun part1(input: List<String>): String {
        val output = mutableListOf<Int>()
        Computer(
            registerA = input[0].replace("Register A: ", "").toInt(),
            registerB = input[1].replace("Register B: ", "").toInt(),
            registerC = input[2].replace("Register C: ", "").toInt(),
            instructions = input[4].replace("Program: ", "").split(",").map { it.toInt() },
            output = output,
        ).execute()
        return output.joinToString(separator = ",")
    }

    fun part2(input: List<String>): String {
        // Brute force not working, exploding due to Java heap space.
        // Didn't find the real solution.
        val failedStates = mutableSetOf<Computer>()
        val instructions = input[4].replace("Program: ", "").split(",").map { it.toInt() }
        var registerA = Int.MAX_VALUE
        while (true) {
            val output = mutableListOf<Int>()
            val executionStates = mutableSetOf<Computer>()
            Computer(
                registerA = registerA,
                registerB = input[1].replace("Register B: ", "").toInt(),
                registerC = input[2].replace("Register C: ", "").toInt(),
                instructions = instructions,
                output = output,
            ).execute(strictMode = true, failedStates = failedStates, executionStates = executionStates)
            if (output == instructions) {
                return registerA.toString()
            }
            failedStates += executionStates
            registerA--
        }
    }

    val testInput = readInput("Day17_test")
    part1(testInput).println()
    // part2(testInput).println()

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
