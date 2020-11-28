package com.ainzzorl.algorithms

class BasicHeap<K : Comparable<K>, V> : Heap<K, V> {
    private val nodes = ArrayList<BasicHeapNode<K, V>>()

    override fun getMin() : Node<K, V>? {
        return if (nodes.isEmpty()) {
            null
        } else {
            nodes[0]
        }
    }

    override fun extractMin() : Node<K, V>? {
        if (nodes.isEmpty()) {
            return null
        }
        swap(0, nodes.size - 1)
        val result = nodes.removeLast()
        heapifyDown(0)
        return result
    }

    override fun insert(key: K, value: V) : Node<K, V> {
        val nextIndex = nodes.size
        val node = BasicHeapNode(key, value, nextIndex)
        nodes.add(node)
        heapifyUp(nextIndex)
        return node
    }

    override fun decreaseKey(node: Node<K, V>, key: K) {
        check(key < node.key) { "New value is greater than the old" }
        check(node is BasicHeapNode) { "Expected an instance of BasicHeapNode" }

        node.key = key
        heapifyUp(node.index)
    }

    override fun visualize() {
        if (nodes.isEmpty()) {
            println("Empty heap")
        } else {
            visualize(0, 0)
        }
        println()
    }

    private fun visualize(index: Int, offset: Int) {
        println(" ".repeat(offset) + "[${nodes[index].key}]")
        if (index * 2 + 1 < nodes.size) {
            visualize(index * 2 + 1, offset + 2)
        }
        if (index * 2 + 2 < nodes.size) {
            visualize(index * 2 + 2, offset + 2)
        }
    }

    private fun heapifyUp(index: Int) {
        val parentIndex = (index - 1) / 2
        if (index == 0  || nodes[index].key > nodes[parentIndex].key) {
            return
        }
        swap(index, parentIndex)
        heapifyUp(parentIndex)
    }

    private fun heapifyDown(index: Int) {
        var min = index
        if (index * 2 + 1 < nodes.size && nodes[index * 2 + 1].key < nodes[min].key) {
            min = index * 2 + 1
        }
        if (index * 2 + 2 < nodes.size && nodes[index * 2 + 2].key < nodes[min].key) {
            min = index * 2 + 2
        }
        if (min != index) {
            swap(index, min)
            heapifyDown(min)
        }
    }

    private fun swap(i: Int, j: Int) {
        val tmp = nodes[i]
        nodes[i] = nodes[j]
        nodes[j] = tmp

        nodes[i].index = i
        nodes[j].index = j
    }
}
