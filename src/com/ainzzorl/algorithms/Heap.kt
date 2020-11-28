package com.ainzzorl.algorithms

interface Heap<K: Comparable<K>, V> {
    fun getMin(): Node<K, V>?

    fun extractMin(): Node<K, V>?

    fun insert(key: K, value: V): Node<K, V>

    fun decreaseKey(node: Node<K, V>, key: K)

    fun visualize()
}
