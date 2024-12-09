fun main() {
    fun expandedDiskMap(input: String): List<String> {
        val expandedDiskMap = mutableListOf<String>()
        var i = 0
        var isFile = true
        for (size in input) {
            if (isFile) {
                repeat(size.digitToInt()) {
                    expandedDiskMap.add(i.toString())
                }
                i++
                isFile = false
            } else {
                repeat(size.digitToInt()) {
                    expandedDiskMap.add(".")
                }
                isFile = true
            }
        }
        return expandedDiskMap
    }

    fun List<String>.nextEmptySpace(from: Int): Int {
        return subList(from, size).indexOfFirst { it == "." }.let {
            if (it == -1) -1
            else it + from
        }
    }

    fun List<String>.nextNonEmptySpace(from: Int): Int {
        return subList(0, from + 1).indexOfLast { it != "." }
    }

    fun rearrange(map: List<String>): List<String> {
        val mutableListMap = map.toMutableList()
        var emptySpaceMapIndex = mutableListMap.nextEmptySpace(0)
        var filledMapIndex = mutableListMap.nextNonEmptySpace(mutableListMap.lastIndex)
        while (emptySpaceMapIndex < filledMapIndex) {
            mutableListMap[emptySpaceMapIndex] = mutableListMap[filledMapIndex]
            mutableListMap[filledMapIndex] = "."
            emptySpaceMapIndex = mutableListMap.nextEmptySpace(emptySpaceMapIndex)
            if (emptySpaceMapIndex == -1) return mutableListMap
            filledMapIndex = mutableListMap.nextNonEmptySpace(filledMapIndex)
            if (filledMapIndex == -1) return mutableListMap
        }
        return mutableListMap
    }

    fun part1(input: String): Long {
        val expandedDiskMap = expandedDiskMap(input)
        return rearrange(expandedDiskMap)
            .foldIndexed(0) { index, acc, item ->
                acc + (index * (item.toIntOrNull() ?: 0))
            }
    }

    data class File(
        val id: Int,
        val size: Int,
    )

    data class Space(
        val size: Int,
    )

    fun expandedDiskMap2(input: String): List<Any> {
        val expandedDiskMap = mutableListOf<Any>()
        var i = 0
        var isFile = true
        for (size in input) {
            if (isFile) {
                expandedDiskMap.add(File(i, size.digitToInt()))
                i++
                isFile = false
            } else {
                expandedDiskMap.add(Space(size.digitToInt()))
                isFile = true
            }
        }
        return expandedDiskMap
    }

    fun expand(map: List<Any>): List<String> {
        val expandedMap = mutableListOf<String>()
        for (item in map) {
            when (item) {
                is File -> {
                    repeat(item.size) { expandedMap.add(item.id.toString()) }
                }

                is Space -> {
                    repeat(item.size) { expandedMap.add(".") }
                }
            }
        }
        return expandedMap
    }

    fun mergeEmptySpace(map: MutableList<Any>, index: Int) {
        var currentIndex = index
        var current = map[currentIndex] as Space
        var nextIndex = index + 1
        if ((index - 1) >= 0) {
            val previous = map[index - 1]
            if (previous is Space) {
                map[index - 1] = previous.copy(size = previous.size + current.size)
                map.removeAt(index)
                currentIndex = index - 1
                current = map[index - 1] as Space
                nextIndex = index
            }
        }
        if (nextIndex < map.size) {
            val next = map[nextIndex]
            if (next is Space) {
                map[currentIndex] = current.copy(size = current.size + next.size)
                map.removeAt(nextIndex)
            }
        }
    }


    fun rearrange2(map: List<Any>): List<Any> {
        val mutableDiskMap = map.toMutableList()
        var fileIndex = map.indexOfLast { it is File }
        while (fileIndex >= 0) {
            val fileSize = (mutableDiskMap[fileIndex] as File).size
            val emptySpaceIndex = mutableDiskMap.indexOfFirst { it is Space && it.size >= fileSize }
            if (emptySpaceIndex == -1 || emptySpaceIndex > fileIndex) {
                fileIndex = mutableDiskMap.subList(0, fileIndex).indexOfLast { it is File }
                continue
            }
            val file = mutableDiskMap[fileIndex] as File
            val emptySpace = mutableDiskMap[emptySpaceIndex] as Space
            mutableDiskMap[emptySpaceIndex] = file
            mutableDiskMap[fileIndex] = Space(file.size)
            mergeEmptySpace(mutableDiskMap, fileIndex)
            if (emptySpace.size > file.size) {
                mutableDiskMap.add(emptySpaceIndex + 1, Space(emptySpace.size - file.size))
            }
            fileIndex = mutableDiskMap.subList(0, fileIndex).indexOfLast { it is File }
        }
        return mutableDiskMap
    }

    fun part2(input: String): Long {
        val expandedDiskMap = expandedDiskMap2(input)
        return expand(rearrange2(expandedDiskMap))
            .foldIndexed(0) { index, acc, item ->
                acc + (index * (item.toIntOrNull() ?: 0))
            }
    }

    val testInput = readInput("Day09_test").single()
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day09").single()
    part1(input).println()
    part2(input).println()
}
