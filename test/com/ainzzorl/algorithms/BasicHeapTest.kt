package com.ainzzorl.algorithms

import org.junit.Test

class BasicHeapTest {
    @Test
    fun testBasicScenario() {
        HeapTest.testBasicScenario(BasicHeap())
    }

    @Test
    fun testInsertExtract() {
        HeapTest.testInsertExtract(BasicHeap())
    }
}