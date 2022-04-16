package com.ainzzorl.algorithms.heaps

interface Node<K : Comparable<K>, V> {
    var key: K
    val value: V
}
