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
    fun compareBinaryAndFibonacciHeaps() {
        val binary = BinaryHeap<Int, String>()
        val fibonacci = FibonacciHeap<Int, String>()

        val random = Random(123)

        // Enforcing key uniqueness for simplicity.
        val binaryKeyToNode = HashMap<Int, Node<Int, String>>()
        val fibonacciKeyToNode = HashMap<Int, Node<Int, String>>()

        repeat(ROUNDS) {
            when (val op = random.nextInt(INSERT_OP_WEIGHT + EXTRACT_OP_WEIGHT + DECREASE_OP_WEIGHT)) {
                in 0 until INSERT_OP_WEIGHT -> {
                    // Insert
                    val key = nextUnusedKey(random, binaryKeyToNode.keys)
                    val value = java.util.UUID.randomUUID().toString()

                    val binaryNode = binary.insert(key, value)
                    val fibonacciNode = fibonacci.insert(key, value)

                    binaryKeyToNode[key] = binaryNode
                    fibonacciKeyToNode[key] = fibonacciNode
                }
                in INSERT_OP_WEIGHT until INSERT_OP_WEIGHT + EXTRACT_OP_WEIGHT -> {
                    // Extract min
                    val binaryNode = binary.extractMin()
                    val fibonacciNode = fibonacci.extractMin()

                    assertEqualNodes(binaryNode, fibonacciNode)

                    if (binaryNode != null) {
                        binaryKeyToNode.remove(binaryNode.key)
                    }
                    if (fibonacciNode != null) {
                        fibonacciKeyToNode.remove(fibonacciNode.key)
                    }
                }
                in INSERT_OP_WEIGHT + EXTRACT_OP_WEIGHT until INSERT_OP_WEIGHT + EXTRACT_OP_WEIGHT + DECREASE_OP_WEIGHT -> {
                    // Decrease key
                    if (binary.getMin() != null && fibonacci.getMin() != null) {
                        val oldKey = binaryKeyToNode.keys.random(random)
                        val newKey = nextUnusedDecreasedKey(random, binaryKeyToNode.keys, oldKey)

                        val binaryNode = binaryKeyToNode[oldKey]!!
                        val fibonacciNode = fibonacciKeyToNode[oldKey]!!

                        binary.decreaseKey(binaryNode, newKey)
                        fibonacci.decreaseKey(fibonacciNode, newKey)

                        binaryKeyToNode.remove(oldKey)
                        fibonacciKeyToNode.remove(oldKey)
                        binaryKeyToNode[newKey] = binaryNode
                        fibonacciKeyToNode[newKey] = fibonacciNode
                    }
                }
                else -> {
                    fail("Unexpected op: $op")
                }
            }

            // Uncomment for debugging
            // println("Visualizing binary heap")
            // binary.visualize()
            // println("Visualizing Fibonacci heap")
            // fibonacci.visualize()

            val binaryMin = binary.getMin()
            val fibonacciMin = fibonacci.getMin()

            assertEqualNodes(binaryMin, fibonacciMin)
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
