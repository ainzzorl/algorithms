package com.ainzzorl.algorithms

class BasicHeapNode<K, V>(val k: K, val v: V) : Node<K, V> {
    override fun getKey(): K {
        return k
    }

    override fun getValue(): V {
        return v
    }
}
