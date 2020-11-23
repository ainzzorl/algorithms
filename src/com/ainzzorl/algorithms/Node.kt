package com.ainzzorl.algorithms

// TODO: "native" getters and setters
interface Node<K : Comparable<K>, V> {
    fun getKey(): K

    fun getValue(): V
}
