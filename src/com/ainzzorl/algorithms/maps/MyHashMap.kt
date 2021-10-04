package com.ainzzorl.algorithms.maps

import kotlin.math.absoluteValue

class MyHashMap <K, V> : MutableMap<K, V> {
    companion object {
        const val INITIAL_CAPACITY = 16
        const val MAXIMUM_CAPACITY: Int = 1.shl(30)
        const val LOAD_FACTOR = 0.75
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = HashSet()
    override val keys: MutableSet<K> = HashSet()
    override var size: Int = 0
    override val values: MutableCollection<V> = ArrayList()

    private var buckets: Array<MutableList<MutableMap.MutableEntry<K, V>>> = Array(INITIAL_CAPACITY) { ArrayList() }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun containsKey(key: K): Boolean {
        val hash = key.hashCode().absoluteValue
        val bucket = buckets[hash % buckets.size]
        for (kv in bucket) {
            if (kv.key!! == key) {
                return true
            }
        }
        return false
    }

    override fun containsValue(value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(key: K): V? {
        val hash = key.hashCode().absoluteValue
        val bucket = buckets[hash % buckets.size]
        for (kv in bucket) {
            if (kv.key!! == key) {
                return kv.value!!
            }
        }
        return null
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun put(key: K, value: V): V? {
        val hash = key.hashCode().absoluteValue
        val bucket = buckets[hash % buckets.size]
        for (kv in bucket) {
            if (kv.key!! == key) {
                val prev = kv.value
                kv.setValue(value)
                return prev
            }
        }
        bucket.add(MyMutableEntry(key, value))
        size++
        return null
    }

    override fun putAll(from: Map<out K, V>) {
        TODO("Not yet implemented")
    }

    override fun remove(key: K): V? {
        val hash = key.hashCode().absoluteValue
        val bucket = buckets[hash % buckets.size]
        repeat(bucket.size) { i ->
            val kv = bucket[i]
            if (kv.key!! == key) {
                bucket.removeAt(i)
                return kv.value
            }
        }
        return null
    }

    class MyMutableEntry<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V> {
        override fun setValue(newValue: V): V {
            val old = value
            value = newValue
            return old
        }
    }
}