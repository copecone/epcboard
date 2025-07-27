package io.github.copecone.epcboard.util.random

import java.util.concurrent.ConcurrentLinkedQueue

internal typealias FloatRange = ClosedFloatingPointRange<Float>
internal typealias DoubleRange = ClosedFloatingPointRange<Double>

abstract class RandomNumberGenerator {
    abstract val supportBatch: Boolean

    protected val intQueue = ConcurrentLinkedQueue<Int>()
    protected var latestIntRange: IntRange? = null

    protected val floatQueue = ConcurrentLinkedQueue<Float>()
    protected var latestFloatRange: FloatRange? = null

    protected val doubleQueue = ConcurrentLinkedQueue<Double>()
    protected var latestDoubleRange: DoubleRange? = null

    val isIntQueueEmpty: Boolean
        get() = intQueue.isEmpty()

    val isFloatQueueEmpty: Boolean
        get() = floatQueue.isEmpty()

    val isDoubleQueueEmpty: Boolean
        get() = doubleQueue.isEmpty()

    open fun pregenerateInt(n: Int, range: IntRange) {
        if (latestIntRange != range) {
            latestIntRange = range
            intQueue.clear()
        }

        if (!supportBatch) repeat(n) { intQueue.add(getInt(range)) }
        throw IllegalStateException("Batch support doesn't implemented on RNG")
    }

    open fun pregenerateFloat(n: Int, range: FloatRange) {
        if (latestFloatRange != range) {
            latestFloatRange = range
            floatQueue.clear()
        }

        if (!supportBatch) repeat(n) { floatQueue.add(getFloat(range)) }
        throw IllegalStateException("Batch support doesn't implemented on RNG")
    }

    open fun pregenerateDouble(n: Int, range: DoubleRange) {
        if (latestDoubleRange != range) {
            latestDoubleRange = range
            doubleQueue.clear()
        }

        if (!supportBatch) repeat(n) { doubleQueue.add(getDouble(range)) }
        throw IllegalStateException("Batch support doesn't implemented on RNG")
    }

    abstract fun getInt(): Int
    abstract fun getInt(range: IntRange): Int

    abstract fun getFloat(): Float
    abstract fun getFloat(range: FloatRange): Float

    abstract fun getDouble(): Double
    abstract fun getDouble(range: DoubleRange): Double
}