package de.tu_chemnitz.restful

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {

    @Test
    fun `this is a nice test`() {
        val actual = true
        assertEquals(expected = true, actual = actual)
    }
}