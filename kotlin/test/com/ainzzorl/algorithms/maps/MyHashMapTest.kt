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
        assertEquals(0, myHashMap.size)
        assertTrue(myHashMap.isEmpty())

        assertFalse(myHashMap.containsKey("a"))
        myHashMap["a"] = "1"
        assertTrue(myHashMap.containsKey("a"))
        assertEquals("1", myHashMap["a"])
        assertEquals(1, myHashMap.size)
        assertFalse(myHashMap.isEmpty())

        assertFalse(myHashMap.containsKey("b"))
        myHashMap["b"] = "2"
        assertTrue(myHashMap.containsKey("b"))
        assertEquals("1", myHashMap["a"])
        assertEquals("2", myHashMap["b"])
        assertEquals(2, myHashMap.size)
        assertFalse(myHashMap.isEmpty())

        myHashMap["a"] = "11"
        assertTrue(myHashMap.containsKey("a"))
        assertEquals("11", myHashMap["a"])
        assertEquals(2, myHashMap.size)
        assertFalse(myHashMap.isEmpty())

        myHashMap.remove("b")
        assertFalse(myHashMap.containsKey("b"))
        assertTrue(myHashMap.containsKey("a"))
        assertEquals(1, myHashMap.size)
        assertFalse(myHashMap.isEmpty())

        myHashMap.remove("a")
        assertFalse(myHashMap.containsKey("a"))
        assertFalse(myHashMap.containsKey("b"))
        assertEquals(0, myHashMap.size)
        assertTrue(myHashMap.isEmpty())
    }

    @Test
    fun manyStrings() {
        val myHashMap = MyHashMap<String, String>()
        assertEquals(0, myHashMap.size)
        assertTrue(myHashMap.isEmpty())

        val standardHashMap = HashMap<String, String>()

        repeat(1000) { i ->
            val s = UUID.randomUUID().toString()
            assertFalse(myHashMap.containsKey(s))
            myHashMap[s] = i.toString()
            standardHashMap[s] = i.toString()
            assertTrue(myHashMap.containsKey(s))
            assertEquals(i.toString(), myHashMap[s])
            assertEquals(i + 1, myHashMap.size)
            assertFalse(myHashMap.isEmpty())
        }

        val keys = standardHashMap.keys.sorted()
        for (key in keys) {
            assertTrue(myHashMap.containsKey(key))
            assertEquals(standardHashMap[key], myHashMap[key])
            myHashMap.remove(key)
            standardHashMap.remove(key)
            assertFalse(myHashMap.containsKey(key))
            assertEquals(standardHashMap.size, myHashMap.size)
        }

        assertEquals(0, myHashMap.size)
        assertTrue(myHashMap.isEmpty())
    }

    @Test
    fun testClear() {
        val myHashMap = MyHashMap<String, String>()

        myHashMap["a"] = "1"
        myHashMap["b"] = "2"
        assertEquals(2, myHashMap.size)

        myHashMap.clear()
        assertTrue(myHashMap.isEmpty())
        assertFalse(myHashMap.containsKey("a"))
        assertFalse(myHashMap.containsKey("b"))
    }

    // TODO: test iteration
}
