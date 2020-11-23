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
        assertEquals(4, node.getKey())
        assertEquals("d", node.getValue())

        heap.insert(1, "a")
        node = heap.getMin()
        assertNotNull(node)
        assertEquals(1, node.getKey())
        assertEquals("a", node.getValue())

        heap.insert(3, "c")
        node = heap.getMin()
        assertNotNull(node)
        assertEquals(1, node.getKey())
        assertEquals("a", node.getValue())

        heap.insert(2, "b")
        node = heap.getMin()
        assertNotNull(node)
        assertEquals(1, node.getKey())
        assertEquals("a", node.getValue())

        node = heap.extractMin()
        assertNotNull(node)
        assertEquals(1, node.getKey())
        assertEquals("a", node.getValue())

        node = heap.extractMin()
        assertNotNull(node)
        assertEquals(2, node.getKey())
        assertEquals("b", node.getValue())

        node = heap.extractMin()
        assertNotNull(node)
        assertEquals(3, node.getKey())
        assertEquals("c", node.getValue())

        node = heap.extractMin()
        assertNotNull(node)
        assertEquals(4, node.getKey())
        assertEquals("d", node.getValue())

        assertNull(heap.getMin())
        assertNull(heap.extractMin())
    }
}