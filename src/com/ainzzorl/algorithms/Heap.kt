package com.ainzzorl.algorithms

interface Heap<N : Node<K, V>, K: Comparable<K>, V> {
    fun getMin(): N?

    fun extractMin(): N?

    fun insert(key: K, value: V): N

    fun decreaseKey(node: Node<K, V>, key: K)
}
