package com.ainzzorl.algorithms

import org.junit.Test

class BinaryHeapTest {
    @Test
    fun testBasicScenario() {
        HeapTest.testBasicScenario(BinaryHeap())
    }

    @Test
    fun testInsertExtract() {
        HeapTest.testInsertExtract(BinaryHeap())
    }
}
