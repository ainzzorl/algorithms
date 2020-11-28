package com.ainzzorl.algorithms

import kotlin.math.ceil
import kotlin.math.log2

// TODO: generic way to iterate nodes in a list
class FibonacciHeap<K : Comparable<K>, V> : Heap<K, V> {
    private var rootNode: FibonacciHeapNode<K, V>? = null
    private var minNode: FibonacciHeapNode<K, V>? = null
    private var size = 0

    override fun getMin(): Node<K, V>? {
        return minNode
    }

    override fun extractMin(): Node<K, V>? {
        if (minNode == null) {
            return null
        }

        val result = minNode

        if (minNode!!.anyChild != null) {
            val stop = minNode!!.anyChild!!
            var cur = minNode!!.anyChild!!

            do {
                val nextChild = cur.right
                insertIntoRootNodes(cur)
                cur = nextChild
            } while (cur != stop)
            minNode!!.anyChild = null
        }

        removeFromRootList(minNode!!)
        size--
        minNode = null

        if (rootNode != null) {
//            val stop = rootNode
//            var cur = rootNode!!
//            do {
//                if (minNode == null || cur.key < minNode!!.key) {
//                    minNode = cur
//                }
//                cur = cur.right
//            } while (cur != stop)
            println("After extracting min (${result!!.key}), pre consolidation")
            visualize()
            consolidate()
        }

        if (result != null) {
            println("After extracting min (${result!!.key}), post consolidation")
            visualize()
        }
        return result
    }

    override fun insert(key: K, value: V): Node<K, V> {
        val node = FibonacciHeapNode(key, value)
        if (minNode == null || node.key < minNode!!.key) {
            minNode = node
        }
        insertIntoRootNodes(node)
        size++
        println("After inserting $key")
        visualize()
        return node
    }

    override fun decreaseKey(node: Node<K, V>, key: K) {
        check(key < node.key) { "New value is greater than the old" }
        check(node is FibonacciHeapNode) { "Expected an instance of FibonacciHeapNode" }

        node.key = key

        if (node.parent != null && node.key < node.parent!!.key) {
            cut(node)
            cascadingCut(node)
        }

        if (node.key < minNode!!.key) {
            minNode = node
        }
    }

    private fun cut(node: FibonacciHeapNode<K, V>) {
        val parent = node.parent!!
        parent.degree--

        node.left.right = node.right
        node.right.left = node.left

        if (node == parent.anyChild) {
            parent.anyChild = if (node.right == node) {
                // only child
                null
            } else {
                node.right
            }
        }

        insertIntoRootNodes(node)

        node.parent = null // TODO: seems unnecessary
        node.marked = false
    }

    private fun cascadingCut(node: FibonacciHeapNode<K, V>) {
        val parent = node.parent
        if (parent != null) {
            if (!node.marked) {
                node.marked = true
            } else {
                cut(node)
                cascadingCut(node.parent!!)
            }
        }
    }

    private fun insertIntoRootNodes(node: FibonacciHeapNode<K, V>) {
        node.parent = null
        if (rootNode == null) {
            node.left = node
            node.right = node
            rootNode = node
        } else {
            node.right = rootNode!!
            node.left = rootNode!!.left

            rootNode!!.left.right = node
            rootNode!!.left = node
        }
    }

    private fun removeFromRootList(node: FibonacciHeapNode<K, V>) {
        node.parent = null
        if (rootNode == node) {
            rootNode = if (rootNode!!.right == rootNode!!) {
                // last element
                null
            } else {
                node.right
            }
        }
        node.left.right = node.right
        node.right.left = node.left
    }

    override fun visualize() {
        if (rootNode == null) {
            println("Empty root nodes")
            return
        }
        println("Size: $size")
        val stop = rootNode
        var cur = rootNode!!
        do {
            val minMark = if (cur == minNode) {
                "*"
            } else {
                ""
            }
            println("[${cur.key}]$minMark, degree: ${cur.degree}")
            if (cur.anyChild != null) {
                printTree(cur.anyChild!!, 2)
            }
            cur = cur.right
        } while (cur != stop)
        println()
    }

    private fun printTree(printStart: FibonacciHeapNode<K, V>, offset: Int) {
        var cur = printStart
        do {
            println(" ".repeat(offset) + "[${cur.key}], child of ${cur.parent!!.key}, degree: ${cur.degree}")
            if (cur.anyChild != null) {
                printTree(cur.anyChild!!, offset + 2)
            }
            cur = cur.right
        } while (cur != printStart)
    }

    private fun consolidate() {
        val maxDegree = ceil(log2(size.toDouble())).toInt() * 2 + 1
        val degreeArray = arrayOfNulls<FibonacciHeapNode<K, V>>(maxDegree)

        val initialRootNodes = mutableListOf<FibonacciHeapNode<K, V>>()
        val stop = rootNode
        var it = rootNode!!
        do {
            initialRootNodes.add(it)
            it = it.right
        } while (it != stop)

        for (it in initialRootNodes) {
            var node = it
            var degree = node.degree
            while (degreeArray[degree] != null) {
                var other = degreeArray[degree]!!
                if (node.key > other.key) {
                    val t = node
                    node = other
                    other = t
                }
                heapLink(other, node)
                degreeArray[degree] = null
                degree++
            }
            degreeArray[degree] = node
        }

        minNode = null
        rootNode = null
        degreeArray.forEach { node ->
            if (node != null) {
                if (minNode == null) {
                    rootNode = node
                    rootNode!!.left = rootNode!!
                    rootNode!!.right = rootNode!!
                    minNode = node
                    node.parent = null
                } else {
                    insertIntoRootNodes(node)
                    if (node.key < minNode!!.key) {
                        minNode = node
                    }
                }
            }
        }
    }

    private fun heapLink(fromNode: FibonacciHeapNode<K, V>, toNode: FibonacciHeapNode<K, V>) {
        removeFromRootList(fromNode)
        fromNode.marked = false
        fromNode.parent = toNode
        toNode.degree++

        if (toNode.anyChild == null) {
            fromNode.left = fromNode
            fromNode.right = fromNode
            toNode.anyChild = fromNode
        } else {
            fromNode.right = toNode.anyChild!!
            fromNode.left = toNode.anyChild!!.left

            toNode.anyChild!!.left.right = fromNode
            toNode.anyChild!!.left = fromNode
        }
    }
}
