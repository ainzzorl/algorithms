package com.ainzzorl.algorithms.maps

import org.junit.Test
import java.util.*
import kotlin.collections.HashMap
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MyHashMapTest {
    @Test
    fun testBasic() {
        val myHashMap = MyHashMap<String, String>()

        assertFalse(myHashMap.containsKey("a"))
        myHashMap["a"] = "1"
        assertTrue(myHashMap.containsKey("a"))
        assertEquals("1", myHashMap["a"])

        assertFalse(myHashMap.containsKey("b"))
        myHashMap["b"] = "2"
        assertTrue(myHashMap.containsKey("b"))
        assertEquals("1", myHashMap["a"])
        assertEquals("2", myHashMap["b"])

        myHashMap["a"] = "11"
        assertTrue(myHashMap.containsKey("a"))
        assertEquals("11", myHashMap["a"])

        myHashMap.remove("b")
        assertFalse(myHashMap.containsKey("b"))
        assertTrue(myHashMap.containsKey("a"))

        myHashMap.remove("a")
        assertFalse(myHashMap.containsKey("a"))
        assertFalse(myHashMap.containsKey("b"))
    }

    @Test
    fun manyStrings() {
        val myHashMap = MyHashMap<String, String>()
        val standardHashMap = HashMap<String, String>()

        repeat(1000) { i ->
            val s = UUID.randomUUID().toString()
            assertFalse(myHashMap.containsKey(s))
            myHashMap[s] = i.toString()
            standardHashMap[s] = i.toString()
            assertTrue(myHashMap.containsKey(s))
            assertEquals(i.toString(), myHashMap[s])
        }

        val keys = standardHashMap.keys.sorted()
        for (key in keys) {
            assertTrue(myHashMap.containsKey(key))
            assertEquals(standardHashMap[key], myHashMap[key])
            myHashMap.remove(key)
            standardHashMap.remove(key)
            assertFalse(myHashMap.containsKey(key))
        }
    }

    // TODO: test size
    // TODO: test isEmpty
    // TODO: test clear
    // TODO: test iteration
}
