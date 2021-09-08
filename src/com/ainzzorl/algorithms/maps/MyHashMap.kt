package com.ainzzorl.algorithms.maps

class MyHashMap <K, V> : MutableMap<K, V> {
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = HashSet()
    override val keys: MutableSet<K> = HashSet()
    override val size: Int = 0
    override val values: MutableCollection<V> = ArrayList()
    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun containsKey(key: K): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsValue(value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun put(key: K, value: V): V? {
        TODO("Not yet implemented")
    }

    override fun putAll(from: Map<out K, V>) {
        TODO("Not yet implemented")
    }

    override fun remove(key: K): V? {
        TODO("Not yet implemented")
    }
}