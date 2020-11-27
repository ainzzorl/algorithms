package com.ainzzorl.algorithms

class FibonacciHeap<K : Comparable<K>, V> : Heap<K, V> {
    private var rootNode: FibonacciHeapNode<K, V>? = null
    private var minNode: FibonacciHeapNode<K, V>? = null

    override fun getMin() : Node<K, V>? {
        return minNode
    }

    override fun extractMin() : Node<K, V>? {
        if (minNode == null) {
            return null
        }

        val result = minNode

        // TODO: consolidate instead!

        removeFromRootList(minNode!!)
        minNode = null

        if (rootNode != null) {
            val stop = rootNode
            var cur = rootNode!!
            do {
                if (minNode == null || cur.key < minNode!!.key) {
                    minNode = cur
                }
                cur = cur.right
            } while (cur != stop)
        }

        return result
    }

    override fun insert(key: K, value: V) : Node<K, V> {
        val node = FibonacciHeapNode(key, value)
        if (minNode == null || node.key < minNode!!.key) {
            minNode = node
        }
        insertIntoRootNodes(node)
        println("After inserting $key")
        printTree()
        return node
    }

    override fun decreaseKey(node: Node<K, V>, key: K) {
        check(key < node.key) { "New value is greater than the old" }
        check(node is FibonacciHeapNode) { "Expected an instance of FibonacciHeapNode" }

        node.key = key
        // TODO: implement
    }

    private fun insertIntoRootNodes(node: FibonacciHeapNode<K, V>) {
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
            if (rootNode!!.left == rootNode!!.right) {
                // last element
                rootNode = null
            } else {
                rootNode = node.right
            }
        }
        node.left.right = node.right
        node.right.left = node.left
    }

    private fun printTree() {
        if (rootNode == null) {
            println("Empty root nodes")
            return
        }
        val stop = rootNode
        var cur = rootNode!!
        do {
            print("[${cur.key}] ")
            if (minNode == null || cur.key < minNode!!.key) {
                minNode = cur
            }
            cur = cur.right
        } while (cur != stop)
        println()
    }
}
