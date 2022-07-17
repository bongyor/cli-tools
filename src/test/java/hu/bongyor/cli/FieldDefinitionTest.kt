package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliParam
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FieldDefinitionTest {
    @Suppress("unused")
    class X {
        @CliParam
        var booleanDefaultTrue: Boolean = true
        @CliParam
        var booleanDefaultFalse: Boolean = false
    }
    private val fieldDefinitionTrue = FieldDefinition(
        targetInstance = X(),
        targetField = X::class.java.getDeclaredField("booleanDefaultTrue"),
    )
    private val fieldDefinitionFalse = FieldDefinition(
        targetInstance = X(),
        targetField = X::class.java.getDeclaredField("booleanDefaultFalse"),
    )

    @Test
    fun parseBoolean() {
        assertTrue(fieldDefinitionTrue.parseBoolean("true"))
        assertTrue(fieldDefinitionTrue.parseBoolean("t"))
        assertTrue(fieldDefinitionTrue.parseBoolean("y"))
        assertTrue(fieldDefinitionTrue.parseBoolean("yes"))
        assertFalse(fieldDefinitionTrue.parseBoolean(null))
        assertTrue(fieldDefinitionFalse.parseBoolean(null))
        assertFalse(fieldDefinitionTrue.parseBoolean("false"))
        assertFalse(fieldDefinitionTrue.parseBoolean("f"))
        assertFalse(fieldDefinitionTrue.parseBoolean("no"))
        assertFalse(fieldDefinitionTrue.parseBoolean("n"))
    }
}