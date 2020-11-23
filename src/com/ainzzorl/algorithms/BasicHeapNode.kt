package com.ainzzorl.algorithms

class BasicHeapNode<K: Comparable<K>, V>(private val k: K, private val v: V, var index: Int) : Node<K, V> {
    override fun getKey(): K {
        return k
    }

    override fun getValue(): V {
        return v
    }
}
