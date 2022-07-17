package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliClass
import hu.bongyor.cli.annotation.CliFunction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Suppress("unused")
internal class RunTest {
    @CliClass(
        defaultRun = true,
        shortcut = "d",
        description = "Default class"
    )
    internal class TestAppClassDefault {
        @CliFunction(
            defaultRun = true,
            shortcut = "d",
            description = "Default function"
        )
        fun defaultFun() {
            executedFunction = "TestAppClassDefault.defaultFun"
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


    companion object RunnerSpy {
        var executedFunction: String? = null
        fun clear() {
            executedFunction = null
        }
    }
}
