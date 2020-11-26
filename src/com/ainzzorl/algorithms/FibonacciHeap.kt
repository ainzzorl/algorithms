package com.ainzzorl.algorithms

class FibonacciHeap<K : Comparable<K>, V> : Heap<K, V> {
    override fun getMin() : Node<K, V>? {
        // TODO: implement
        return null
    }

    override fun extractMin() : Node<K, V>? {
        // TODO: implement
        return null
    }

    override fun insert(key: K, value: V) : Node<K, V> {
        val node = FibonacciHeapNode(key, value)
        // TODO: implement
        return node
    }

    override fun decreaseKey(node: Node<K, V>, key: K) {
        check(key < node.key) { "New value is greater than the old" }
        check(node is FibonacciHeapNode) { "Expected an instance of FibonacciHeapNode" }

        node.key = key
        // TODO: implement
    }
}
