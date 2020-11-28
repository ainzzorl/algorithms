package com.ainzzorl.algorithms

import kotlin.math.ceil
import kotlin.math.log2

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

        moveChildrenToRoot(minNode!!)
        removeFromRootList(minNode!!)

        size--
        minNode = null

        if (rootNode != null) {
            consolidate()
        }

        return result
    }

    override fun insert(key: K, value: V): Node<K, V> {
        val node = FibonacciHeapNode(key, value)
        insertIntoRootNodes(node)
        tryForMin(node)
        size++
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
        for (node in SiblingNodeIterator(rootNode!!)) {
            val minMark = if (node == minNode) {
                "*"
            } else {
                ""
            }
            println("[${node.key}]$minMark, degree: ${node.degree}")
            if (node.anyChild != null) {
                visualize(node, 2)
            }
        }
        println()
    }

    private fun visualize(printStart: FibonacciHeapNode<K, V>, offset: Int) {
        for (child in SiblingNodeIterator(printStart.anyChild!!)) {
            println(" ".repeat(offset) + "[${child.key}], child of ${child.parent!!.key}, degree: ${child.degree}")
            if (child.anyChild != null) {
                visualize(child.anyChild!!, offset + 2)
            }
        }
    }

    private fun consolidate() {
        val maxDegree = ceil(log2(size.toDouble())).toInt() * 2 + 1
        val degreeArray = arrayOfNulls<FibonacciHeapNode<K, V>>(maxDegree)

        // Can't iterate root nodes while they are changing.
        // Expanding them to a list first.
        val initialRootNodes = SiblingNodeIterator(rootNode!!).asSequence().toList()

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
                insertIntoRootNodes(node)
                tryForMin(node)
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

    private fun moveChildrenToRoot(node: FibonacciHeapNode<K, V>) {
        if (node.anyChild != null) {
            for (child in SiblingNodeIterator(node.anyChild!!)) {
                insertIntoRootNodes(child)
            }
            node.anyChild = null
            node.degree = 0
        }
    }

    private fun tryForMin(node: FibonacciHeapNode<K, V>) {
        if (minNode == null || node.key < minNode!!.key) {
            minNode = node
        }
    }

    inner class SiblingNodeIterator(private val start: FibonacciHeapNode<K, V>) : Iterator<FibonacciHeapNode<K, V>> {
        var nextValue = start
        var done = false

        override fun hasNext(): Boolean {
            return !done
        }

        override fun next(): FibonacciHeapNode<K, V> {
            val result = nextValue
            if (nextValue.right == start) {
                done = true
            } else {
                nextValue = nextValue.right
            }
            return result
        }
    }
}
