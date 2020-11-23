package com.ainzzorl.algorithms

class BasicHeap<K, V> : Heap<BasicHeapNode<K, V>, K, V> {
    override fun getMin() : BasicHeapNode<K, V>? {
        return null
    }

    override fun extractMin() : BasicHeapNode<K, V>? {
        return null
    }

    override fun insert(k: K, v: V) {
    }

    override fun decreaseKey(node: Node<K, V>, key: K) {
    }
}
