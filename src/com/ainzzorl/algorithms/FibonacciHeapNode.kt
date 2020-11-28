package com.ainzzorl.algorithms

class FibonacciHeapNode<K : Comparable<K>, V>(override var key: K, override val value: V) : Node<K, V> {
    var degree = 0
    var marked = false
    var parent: FibonacciHeapNode<K, V>? = null
    var left: FibonacciHeapNode<K, V> = this
    var right: FibonacciHeapNode<K, V> = this
    var anyChild: FibonacciHeapNode<K, V>? = null
}
