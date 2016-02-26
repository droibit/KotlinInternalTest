package com.github.droibit.kotlin.internal.test

import org.junit.Test

/**
 *
 *
 * @author kumagai
 */
class InternalClassTest {

    @Test
    fun test() {
        val internalClass = InternalClass()

        assert(internalClass.publicValue == 1)
        assert(internalClass.internalValue == 2)
    }
}