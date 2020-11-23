package com.ainzzorl.algorithms

import org.junit.Test
import kotlin.test.assertNull

class HeapTest {
    @Test
    fun testBasic() {
        val heap =  BasicHeap<Int, String>()

        assertNull(heap.getMin())
        assertNull(heap.extractMin())
    }
}