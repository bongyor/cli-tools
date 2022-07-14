package hu.bongyor.cli

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested

internal class ArgsParserTest {

    @Test
    fun isHelpRequest() {
        val argsString = "?"
        assertTrue(createParser(argsString).isHelpRequest)
        assertTrue(createParser("-?").isHelpRequest)
        assertTrue(createParser("h").isHelpRequest)
        assertTrue(createParser("-h").isHelpRequest)
        assertTrue(createParser("help").isHelpRequest)
        assertTrue(createParser("-help").isHelpRequest)
        assertTrue(createParser("--help").isHelpRequest)
        assertFalse(createParser("").isHelpRequest)
        assertFalse(createParser("x").isHelpRequest)
        assertFalse(createParser("xx.xx").isHelpRequest)
        assertFalse(createParser("xx.xx -x --x").isHelpRequest)
    }

    private fun createParser(argsString: String) = ArgsParser(args = argsString.split(" ").toTypedArray())

    @Nested
    inner class ExecuteRequestTests {

        @Test
        fun `Empty string`() {
            val executeRequest = createParser("").executeRequest
            assertThat(executeRequest.classNameOrShortcut).isNull()
            assertThat(executeRequest.functionNameOrShortcut).isNull()
            assertThat(executeRequest.paramSetRequests).isEmpty()
        }

        @Test
        fun `Only class`() {
            val executeRequest = createParser("ClassName").executeRequest
            assertThat(executeRequest.classNameOrShortcut).isEqualTo("ClassName")
            assertThat(executeRequest.functionNameOrShortcut).isNull()
            assertThat(executeRequest.paramSetRequests).isEmpty()
        }

        @Test
        fun `Only function`() {
            val executeRequest = createParser(".functionName").executeRequest
            assertThat(executeRequest.classNameOrShortcut).isNull()
            assertThat(executeRequest.functionNameOrShortcut).isEqualTo("functionName")
            assertThat(executeRequest.paramSetRequests).isEmpty()
        }

        @Test
        fun `Class and function`() {
            val executeRequest = createParser("ClassName.functionName").executeRequest
            assertThat(executeRequest.classNameOrShortcut).isEqualTo("ClassName")
            assertThat(executeRequest.functionNameOrShortcut).isEqualTo("functionName")
            assertThat(executeRequest.paramSetRequests).isEmpty()
        }

        @Nested
        inner class ParamTests {
            @Test
            internal fun `Just one param`() {
                val executeRequest = createParser("--fieldName value").executeRequest
                assertThat(executeRequest.classNameOrShortcut).isNull()
                assertThat(executeRequest.functionNameOrShortcut).isNull()
                assertThat(executeRequest.paramSetRequests)
                    .containsExactly(
                        ParamSetRequest(fieldShortcut = null, fieldName = "fieldName", value = "value")
                    )
            }

            @Test
            internal fun `One param and one shortcut`() {
                val executeRequest = createParser("--fieldName value -shortcut svalue").executeRequest
                assertThat(executeRequest.classNameOrShortcut).isNull()
                assertThat(executeRequest.functionNameOrShortcut).isNull()
                assertThat(executeRequest.paramSetRequests)
                    .containsExactly(
                        ParamSetRequest(fieldShortcut = null, fieldName = "fieldName", value = "value"),
                        ParamSetRequest(fieldShortcut = "shortcut", fieldName = null, value = "svalue"),
                    )
            }

            @Test
            internal fun `One param whitout value and one shortcut`() {
                val executeRequest = createParser("--fieldName -shortcut svalue").executeRequest
                assertThat(executeRequest.classNameOrShortcut).isNull()
                assertThat(executeRequest.functionNameOrShortcut).isNull()
                assertThat(executeRequest.paramSetRequests)
                    .containsExactly(
                        ParamSetRequest(fieldShortcut = null, fieldName = "fieldName", value = null),
                        ParamSetRequest(fieldShortcut = "shortcut", fieldName = null, value = "svalue"),
                    )
            }

            @Test
            internal fun `One param and one shortcut whitout value`() {
                val executeRequest = createParser("--fieldName value -shortcut").executeRequest
                assertThat(executeRequest.classNameOrShortcut).isNull()
                assertThat(executeRequest.functionNameOrShortcut).isNull()
                assertThat(executeRequest.paramSetRequests)
                    .containsExactly(
                        ParamSetRequest(fieldShortcut = null, fieldName = "fieldName", value = "value"),
                        ParamSetRequest(fieldShortcut = "shortcut", fieldName = null, value = null),
                    )
            }

            @Test
            internal fun `Class, function and one param and one shortcut whitout value`() {
                val executeRequest = createParser("ClassName.functionName --fieldName value -shortcut").executeRequest
                assertThat(executeRequest.classNameOrShortcut).isEqualTo("ClassName")
                assertThat(executeRequest.functionNameOrShortcut).isEqualTo("functionName")
                assertThat(executeRequest.paramSetRequests)
                    .containsExactly(
                        ParamSetRequest(fieldShortcut = null, fieldName = "fieldName", value = "value"),
                        ParamSetRequest(fieldShortcut = "shortcut", fieldName = null, value = null),
                    )
            }
        }
    }
}