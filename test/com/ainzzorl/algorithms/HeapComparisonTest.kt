package com.ainzzorl.algorithms

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class HeapComparisonTest {
    private val ROUNDS = 100000
    private val MIN_KEY = -10000000
    private val MAX_KEY = 10000000

    // More inserts and extractions.
    // Otherwise heaps will always be small.
    private val INSERT_OP_WEIGHT = 3
    private val EXTRACT_OP_WEIGHT = 1
    private val DECREASE_OP_WEIGHT = 1

    @Test
    fun compareBasicAndFibonacciHeaps() {
        val basic = BasicHeap<Int, String>()
        val fibonacci = FibonacciHeap<Int, String>()

        val random = Random(123)

        // Enforcing key uniqueness for simplicity.
        val basicKeyToNode = HashMap<Int, Node<Int, String>>()
        val fibonacciKeyToNode = HashMap<Int, Node<Int, String>>()

        repeat(ROUNDS) {
            when (val op = random.nextInt(INSERT_OP_WEIGHT + EXTRACT_OP_WEIGHT + DECREASE_OP_WEIGHT)) {
                in 0 until INSERT_OP_WEIGHT -> {
                    // Insert
                    val key = nextUnusedKey(random, basicKeyToNode.keys)
                    val value = java.util.UUID.randomUUID().toString()

                    val basicNode = basic.insert(key, value)
                    val fibonacciNode = fibonacci.insert(key, value)

                    basicKeyToNode[key] = basicNode
                    fibonacciKeyToNode[key] = fibonacciNode
                }
                in INSERT_OP_WEIGHT until INSERT_OP_WEIGHT + EXTRACT_OP_WEIGHT -> {
                    // Extract min
                    val basicNode = basic.extractMin()
                    val fibonacciNode = fibonacci.extractMin()

                    assertEqualNodes(basicNode, fibonacciNode)

                    if (basicNode != null) {
                        basicKeyToNode.remove(basicNode!!.key)
                    }
                    if (fibonacciNode != null) {
                        fibonacciKeyToNode.remove(fibonacciNode!!.key)
                    }
                }
                in INSERT_OP_WEIGHT + EXTRACT_OP_WEIGHT until INSERT_OP_WEIGHT + EXTRACT_OP_WEIGHT + DECREASE_OP_WEIGHT -> {
                    // Decrease key
                    if (basic.getMin() != null && fibonacci.getMin() != null) {
                        val oldKey = basicKeyToNode.keys.random(random)
                        val newKey = nextUnusedDecreasedKey(random, basicKeyToNode.keys, oldKey)

                        val basicNode = basicKeyToNode[oldKey]!!
                        val fibonacciNode = fibonacciKeyToNode[oldKey]!!

                        basic.decreaseKey(basicNode, newKey)
                        fibonacci.decreaseKey(fibonacciNode, newKey)

                        basicKeyToNode.remove(oldKey)
                        fibonacciKeyToNode.remove(oldKey)
                        basicKeyToNode[newKey] = basicNode
                        fibonacciKeyToNode[newKey] = fibonacciNode
                    }
                }
                else -> {
                    fail("Unexpected op: $op")
                }
            }

            // Uncomment for debugging
            // println("Visualizing basic heap")
            // basic.visualize()
            // println("Visualizing Fibonacci heap")
            // basic.visualize()

            val basicMin = basic.getMin()
            val fibonacciMin = fibonacci.getMin()

            assertEqualNodes(basicMin, fibonacciMin)
        }
    }

    private fun nextUnusedKey(random: Random, used: Set<Int>): Int {
        val key = random.nextInt(MIN_KEY, MAX_KEY)
        return if (used.contains(key)) {
            nextUnusedKey(random, used)
        } else {
            key
        }
    }

    private fun nextUnusedDecreasedKey(random: Random, used: Set<Int>, oldKey: Int): Int {
        val minus = random.nextInt(MAX_KEY - MIN_KEY)
        return if (used.contains(oldKey - minus)) {
            nextUnusedDecreasedKey(random, used, oldKey)
        } else {
            oldKey - minus
        }
    }

    private fun assertEqualNodes(node1: Node<Int, String>?, node2: Node<Int, String>?) {
        if (node1 == null || node2 == null) {
            assertNull(node1)
            assertNull(node2)
            return
        }
        assertEquals(node1.key, node2.key)
        assertEquals(node1.value, node2.value)
    }
}
