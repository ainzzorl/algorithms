package com.ainzzorl.algorithms

import java.util.*

class FibonacciHeap<K : Comparable<K>, V> : Heap<K, V> {
    private val rootNodes = LinkedList<FibonacciHeapNode<K, V>>()
    private var minNode: FibonacciHeapNode<K, V>? = null

    override fun getMin() : Node<K, V>? {
        return minNode
    }

    override fun extractMin() : Node<K, V>? {
        val result = minNode

        // TODO: consolidate instead!

        // TODO: not this
        rootNodes.remove(minNode)

        minNode = null
        for (rootNode in rootNodes) {
            if (minNode == null || rootNode.key < minNode!!.key) {
                minNode = rootNode
            }
        }

        return result
    }

    override fun insert(key: K, value: V) : Node<K, V> {
        val node = FibonacciHeapNode(key, value)
        rootNodes.add(node)
        if (minNode == null || node.key < minNode!!.key) {
            minNode = node
        }
        return node
    }

    override fun decreaseKey(node: Node<K, V>, key: K) {
        check(key < node.key) { "New value is greater than the old" }
        check(node is FibonacciHeapNode) { "Expected an instance of FibonacciHeapNode" }

        node.key = key
        // TODO: implement
    }
}
