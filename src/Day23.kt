fun main() {
    data class Triple(val node1: String, val node2: String, val node3: String) {
        override fun equals(other: Any?): Boolean {
            if (other !is Triple) return false
            return setOf(node1, node2, node3) == setOf(other.node1, other.node2, other.node3)
        }

        override fun hashCode(): Int {
            return setOf(node1, node2, node3).hashCode()
        }
    }

    data class Edge(val node1: String, val node2: String)

    class Graph {
        private val edges =
            mutableMapOf<String, Set<String>>().withDefault { emptySet() }

        fun registerEdge(node1: String, node2: String) {
            edges[node1] = edges.getValue(node1) + node2
            edges[node2] = edges.getValue(node2) + node1
        }

        fun edges(): Iterable<Edge> {
            return edges.toList()
                .flatMap { (node1, edges) ->
                    edges.map { node2 ->
                        Edge(node1, node2)
                    }
                }
        }

        fun edges(node: String): Set<String> {
            return edges.getValue(node)
        }

        fun nodes(): Set<String> = edges.keys
    }

    fun part1(input: List<String>): Long {
        val graph = Graph()
        input.forEach { line ->
            val (node1, node2) = line.split("-")
            graph.registerEdge(node1, node2)
        }
        val sets = mutableSetOf<Triple>()
        graph.edges().filter { edge -> edge.node1.startsWith('t') || edge.node2.startsWith('t') }.forEach { edge ->
            (graph.edges(edge.node1) intersect graph.edges(edge.node2)).forEach { node3 ->
                sets += Triple(edge.node1, edge.node2, node3)
            }
        }
        return sets.size.toLong()
    }

    fun findStronglyConnectedGraphs(
        graph: Graph,
        stronglyConnectedGraph: Set<String>,
        memo: MutableMap<Set<String>, Set<Set<String>>>,
    ): Set<Set<String>> {
        memo[stronglyConnectedGraph]?.let { return it }
        val remainingNodes = graph.nodes() - stronglyConnectedGraph
        if (remainingNodes.isEmpty()) return setOf(stronglyConnectedGraph).also {
            memo[stronglyConnectedGraph] = it
        }
        return remainingNodes.filter { node ->
            stronglyConnectedGraph.all { node in graph.edges(it) }
        }.flatMap { node ->
            findStronglyConnectedGraphs(graph, stronglyConnectedGraph + node, memo)
        }.toSet().ifEmpty { setOf(stronglyConnectedGraph) }.also {
            memo[stronglyConnectedGraph] = it
        }
    }

    fun part2(input: List<String>): String {
        val graph = Graph()
        input.forEach { line ->
            val (node1, node2) = line.split("-")
            graph.registerEdge(node1, node2)
        }
        val stronglyConnectedGraphs = mutableSetOf<Set<String>>()
        val memo = mutableMapOf<Set<String>, Set<Set<String>>>()
        graph.nodes().forEach { node ->
            stronglyConnectedGraphs += findStronglyConnectedGraphs(graph, setOf(node), memo)
        }
        val largestGraph = stronglyConnectedGraphs.maxBy { it.size }
        return largestGraph.sorted().joinToString(separator = ",")
    }

    val testInput = readInput("Day23_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}
