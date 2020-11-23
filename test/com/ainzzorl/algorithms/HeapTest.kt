package com.ainzzorl.algorithms

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HeapTest {
    @Test
    fun testBasic() {
        val heap =  BasicHeap<Int, String>()
        var node: Node<Int, String>?

        assertNull(heap.getMin())
        assertNull(heap.extractMin())

        heap.insert(4, "d")
        node = heap.getMin()
        assertNotNull(node)
        assertEquals(4, node.key)
        assertEquals("d", node.value)

        heap.insert(1, "a")
        node = heap.getMin()
        assertNotNull(node)
        assertEquals(1, node.key)
        assertEquals("a", node.value)

        heap.insert(3, "c")
        node = heap.getMin()
        assertNotNull(node)
        assertEquals(1, node.key)
        assertEquals("a", node.value)

        heap.insert(2, "b")
        node = heap.getMin()
        assertNotNull(node)
        assertEquals(1, node.key)
        assertEquals("a", node.value)

        node = heap.extractMin()
        assertNotNull(node)
        assertEquals(1, node.key)
        assertEquals("a", node.value)

        node = heap.extractMin()
        assertNotNull(node)
        assertEquals(2, node.key)
        assertEquals("b", node.value)

        node = heap.extractMin()
        assertNotNull(node)
        assertEquals(3, node.key)
        assertEquals("c", node.value)

        node = heap.extractMin()
        assertNotNull(node)
        assertEquals(4, node.key)
        assertEquals("d", node.value)

        assertNull(heap.getMin())
        assertNull(heap.extractMin())
    }
}