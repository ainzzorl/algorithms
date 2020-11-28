package com.ainzzorl.algorithms

class BinaryHeapNode<K : Comparable<K>, V>(override var key: K, override val value: V, var index: Int) : Node<K, V>
