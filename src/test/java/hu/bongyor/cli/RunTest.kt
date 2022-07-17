package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliClass
import hu.bongyor.cli.annotation.CliFunction
import hu.bongyor.cli.annotation.CliParam
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("unused")
internal class RunTest {
    internal class X
    enum class TestEnum {
        @CliParam(shortcut = "a")
        VALUE_A,
        VALUE_B,
    }
    @CliClass(
        defaultRun = true,
        shortcut = "d",
        description = "Default class"
    )
    internal class TestAppClassDefault {
        @CliParam(
            shortcut = "b",
            description = "Test boolean"
        )
        var testBoolean: Boolean = false
        @CliParam(
            shortcut = "b2",
            description = "Test boolean"
        )
        var testBooleanDefaultTrue: Boolean = true
        @CliParam
        var string: String? = null
        @CliParam
        var xInstance: X? = null
        @CliParam
        var int: Int? = null
        @CliParam
        var long: Long? = null
        @CliParam
        var testEnum: TestEnum? = null

        @CliFunction(
            defaultRun = true,
            shortcut = "d",
            description = "Default function"
        )
        fun defaultFun() {
            executedFunction = "TestAppClassDefault.defaultFun"
            defaultInstance = this
        }

        @CliFunction(
            defaultRun = false,
            shortcut = "s",
            description = "Second function"
        )
        fun secondFun() {
            executedFunction = "TestAppClassDefault.secondFun"
        }
    }

    @CliClass(
        defaultRun = false,
        shortcut = "S",
        description = "Second class"
    )
    internal class TestAppClassSecond {
        @CliFunction(
            defaultRun = true,
            shortcut = "d",
            description = "Default function"
        )
        fun defaultFun() {
            executedFunction = "TestAppClassSecond.defaultFun"
        }

        @CliFunction(
            defaultRun = false,
            shortcut = "s",
            description = "Second function"
        )
        fun secondFun() {
            executedFunction = "TestAppClassSecond.secondFun"
        }
    }

    private val testAppDefinition = ApplicationDefinition(
        classes = listOf(
            ClassDefinition(TestAppClassDefault::class.java),
            ClassDefinition(TestAppClassSecond::class.java),
        )
    )

    @BeforeEach
    internal fun setUp() {
        clear()
    }

    @Test
    fun `Run default class default method`() {
        testAppDefinition.execute(
            ExecuteCommand(
                classNameOrShortcut = null,
                functionNameOrShortcut = null,
                paramSetCommands = listOf()
            )
        )
        assertThat(executedFunction).isEqualTo("TestAppClassDefault.defaultFun")
    }

    @Test
    fun `Run default class second method`() {
        testAppDefinition.execute(
            ExecuteCommand(
                classNameOrShortcut = null,
                functionNameOrShortcut = "secondFun",
                paramSetCommands = listOf()
            )
        )
        assertThat(executedFunction).isEqualTo("TestAppClassDefault.secondFun")
    }

    @Test
    fun `Run default class second method shortcut`() {
        testAppDefinition.execute(
            ExecuteCommand(
                classNameOrShortcut = null,
                functionNameOrShortcut = "s",
                paramSetCommands = listOf()
            )
        )
        assertThat(executedFunction).isEqualTo("TestAppClassDefault.secondFun")
    }

    @Test
    fun `Run second class default method`() {
        testAppDefinition.execute(
            ExecuteCommand(
                classNameOrShortcut = "TestAppClassSecond",
                functionNameOrShortcut = null,
                paramSetCommands = listOf()
            )
        )
        assertThat(executedFunction).isEqualTo("TestAppClassSecond.defaultFun")
    }

    @Test
    fun `Run second class shortcut default method`() {
        testAppDefinition.execute(
            ExecuteCommand(
                classNameOrShortcut = "S",
                functionNameOrShortcut = null,
                paramSetCommands = listOf()
            )
        )
        assertThat(executedFunction).isEqualTo("TestAppClassSecond.defaultFun")
    }

    @Nested
    inner class ParamTest {
        @Test
        internal fun `Set boolean with name`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = "testBoolean",
                            fieldShortcut = null,
                            value = "true"
                        )
                    )
                )
            )
            assertThat(defaultInstance?.testBoolean).isTrue
        }
        @Test
        internal fun `Set boolean with shortcut`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = null,
                            fieldShortcut = "b",
                            value = "true"
                        )
                    )
                )
            )
            assertThat(defaultInstance?.testBoolean).isTrue
        }
        @Test
        internal fun `Set boolean with shortcut to false`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = null,
                            fieldShortcut = "b2",
                            value = "false"
                        )
                    )
                )
            )
            assertThat(defaultInstance?.testBooleanDefaultTrue).isFalse
        }
        @Test
        internal fun `Set boolean with shortcut toggle`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = null,
                            fieldShortcut = "b2",
                            value = null,
                        )
                    )
                )
            )
            assertThat(defaultInstance?.testBooleanDefaultTrue).isFalse
        }
        @Test
        internal fun `Set string`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = "string",
                            fieldShortcut = null,
                            value = "testString"
                        )
                    )
                )
            )
            assertThat(defaultInstance?.string).isEqualTo("testString")
        }
        @Test
        internal fun `Set int`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = "int",
                            fieldShortcut = null,
                            value = "101"
                        )
                    )
                )
            )
            assertThat(defaultInstance?.int).isEqualTo(101)
        }
        @Test
        internal fun `Set long`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = "long",
                            fieldShortcut = null,
                            value = "1000000000000"
                        )
                    )
                )
            )
            assertThat(defaultInstance?.long).isEqualTo(1_000_000_000_000)
        }
        @Test
        internal fun `Set enum with name`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = "testEnum",
                            fieldShortcut = null,
                            value = "VALUE_B"
                        )
                    )
                )
            )
            assertThat(defaultInstance?.testEnum).isEqualTo(TestEnum.VALUE_B)
        }
        @Test
        internal fun `Set enum with shortcut`() {
            testAppDefinition.execute(
                ExecuteCommand(
                    classNameOrShortcut = "TestAppClassDefault",
                    functionNameOrShortcut = "defaultFun",
                    paramSetCommands = listOf(
                        ParamSetCommand(
                            fieldName = "testEnum",
                            fieldShortcut = null,
                            value = "a"
                        )
                    )
                )
            )
            assertThat(defaultInstance?.testEnum).isEqualTo(TestEnum.VALUE_A)
        }
        @Test
        internal fun `Not supported type`() {
            assertThatThrownBy {
                testAppDefinition.execute(
                    ExecuteCommand(
                        classNameOrShortcut = "TestAppClassDefault",
                        functionNameOrShortcut = "defaultFun",
                        paramSetCommands = listOf(
                            ParamSetCommand(
                                fieldName = "xInstance",
                                fieldShortcut = null,
                                value = "testString"
                            )
                        )
                    )
                )
            }
                .isInstanceOf(CliException::class.java)
                .hasMessage("Not supported type: X")
        }
    }

    companion object RunnerSpy {
        var executedFunction: String? = null
        var defaultInstance: TestAppClassDefault? = null
        fun clear() {
            executedFunction = null
            defaultInstance = null
        }
    }
}
