package com.ainzzorl.algorithms

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

object HeapTest {
    fun testBasicScenario(heap: BasicHeap<Int, String>) {
        assertNull(heap.getMin())
        assertNull(heap.extractMin())

        insertAndCheck(heap, 4, "d", 4, "d")

        insertAndCheck(heap, 1, "a", 1, "a")

        insertAndCheck(heap, 3, "c", 1, "a")

        val e = insertAndCheck(heap, 6,"e", 1, "a")

        heap.decreaseKey(e, -1)

        extractMinAndCheck(heap, -1, "e")

        insertAndCheck(heap, 2, "b", 1, "a")

        extractMinAndCheck(heap, 1, "a")
        extractMinAndCheck(heap, 2, "b")
        extractMinAndCheck(heap, 3, "c")
        extractMinAndCheck(heap, 4, "d")
        assertNull(heap.getMin())
        assertNull(heap.extractMin())
    }

    private fun insertAndCheck(
            heap: Heap<Int, String>, key: Int, value: String, expectedMinKey: Int, expectedMinValue: String) : Node<Int, String> {
        val node = heap.insert(key, value)
        val min = heap.getMin()
        assertNotNull(min)
        assertEquals(expectedMinKey, min.key)
        assertEquals(expectedMinValue, min.value)
        return node
    }

    private fun extractMinAndCheck(
            heap: Heap<Int, String>, expectedMinKey: Int, expectedMinValue: String) {
        val min = heap.extractMin()
        assertNotNull(min)
        assertEquals(expectedMinKey, min.key)
        assertEquals(expectedMinValue, min.value)
    }
}