package com.ainzzorl.algorithms

class BasicHeap<K: Comparable<K>, V> : Heap<BasicHeapNode<K, V>, K, V> {
    private val nodes = ArrayList<BasicHeapNode<K, V>>()

    override fun getMin() : BasicHeapNode<K, V>? {
        return if (nodes.isEmpty()) {
            null
        } else {
            nodes[0]
        }
    }

    override fun extractMin() : BasicHeapNode<K, V>? {
        if (nodes.isEmpty()) {
            return null
        }
        swap(0, nodes.size - 1)
        val result = nodes.removeLast()
        heapifyDown(0)
        return result
    }

    override fun insert(key: K, value: V) : BasicHeapNode<K, V> {
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

    private fun heapifyUp(index: Int) {
        if (index == 0  || nodes[index].key > nodes[index/2].key) {
            return
        }
        swap(index, index / 2)
        heapifyUp(index / 2)
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
