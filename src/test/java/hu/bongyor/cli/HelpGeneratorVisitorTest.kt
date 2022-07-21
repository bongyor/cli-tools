package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliClass
import hu.bongyor.cli.annotation.CliFunction
import hu.bongyor.cli.annotation.CliParam
import hu.bongyor.cli.theme.ConsoleTheme
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("unused")
enum class TestEnum{
    @CliParam(shortcut = "shortcut1", description = "description1")
    VALUE1,
    @CliParam(shortcut = "shortcut2")
    VALUE2,
    VALUE3,
}

class TestConsoleTheme : ConsoleTheme {
    override val default: String get() = "{DEFAULT}"
    override val param: String get() = "{PARAM}"
    override val defaultMethod: String get() = "{DEFAULT_METHOD}"
    override val method: String get() = "{METHOD}"
    override val classname: String get() = "{CLASSNAME}"
    override val defaultClassname: String get() = "{DEFAULT_CLASSNAME}"
    override val bold: String get() = "{BOLD}"
    override fun indent(indentation: Int) = "{indent:${indentation}}"
}
internal class HelpGeneratorVisitorTest {
    private val helpGeneratorVisitor = HelpGeneratorVisitor(TestConsoleTheme())

    @Test
    internal fun `Empty application help`() {
        val appDefinition = ApplicationDefinition(classes = listOf())
        appDefinition.accept(helpGeneratorVisitor)
        assertThat(helpGeneratorVisitor.help)
            .isEqualTo(
                """
                {DEFAULT}Java CLI runnable
                {indent:1}java -jar <application.jar> [<class name>.][<function name>] [parameters]
                {DEFAULT}
            """.trimIndent()
            )
    }

    @Nested
    inner class ClassHelp {
        @Test
        internal fun `Default class with shortcut and description`() {
            @CliClass(
                defaultRun = true,
                shortcut = "shortcut",
                description = "description"
            )
            class ClassName

            val definition = ClassDefinition(targetClass = ClassName::class.java)
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """

                {DEFAULT_CLASSNAME}ClassName{BOLD} (shortcut) - description (DEFAULT)
                {DEFAULT}
            """.trimIndent()
                )
        }

        @Test
        internal fun `Not default class without shortcut and description`() {
            @CliClass(
                defaultRun = false,
                shortcut = "",
                description = ""
            )
            class ClassName

            val definition = ClassDefinition(targetClass = ClassName::class.java)
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """

                {CLASSNAME}ClassName{DEFAULT}
                {DEFAULT}
            """.trimIndent()
                )
        }

        @Test
        internal fun `Not default class with shortcut and description`() {
            @CliClass(
                defaultRun = false,
                shortcut = "shortcut",
                description = "description"
            )
            class ClassName

            val definition = ClassDefinition(targetClass = ClassName::class.java)
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """

                {CLASSNAME}ClassName{DEFAULT} (shortcut) - description
                {DEFAULT}
            """.trimIndent()
                )
        }

    }

    @Nested
    inner class FunctionHelp {

        @Test
        internal fun `Default function with shortcut and description`() {
            class X {
                @Suppress("unused")
                @CliFunction(
                    defaultRun = true,
                    shortcut = "shortcut",
                    description = "description"
                )
                fun testMethod() {
                }
            }

            val definition = FunctionDefinition(targetMethod = X::class.java.getMethod("testMethod"))
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """
                {DEFAULT}
                {indent:1}{DEFAULT_METHOD}testMethod (shortcut){BOLD} (DEFAULT){DEFAULT} // description
                {DEFAULT}
            """.trimIndent()
                )

        }
        @Test
        internal fun `Non default function with shortcut and description`() {
            class X {
                @Suppress("unused")
                @CliFunction(
                    defaultRun = false,
                    shortcut = "shortcut",
                    description = "description"
                )
                fun testMethod() {
                }
            }

            val definition = FunctionDefinition(targetMethod = X::class.java.getMethod("testMethod"))
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """
                {DEFAULT}
                {indent:1}{METHOD}testMethod (shortcut){DEFAULT} // description
                {DEFAULT}
            """.trimIndent()
                )

        }
        @Test
        internal fun `Non default function without shortcut and description`() {
            class X {
                @Suppress("unused")
                @CliFunction(
                    defaultRun = false,
                    shortcut = "",
                    description = ""
                )
                fun testMethod() {
                }
            }

            val definition = FunctionDefinition(targetMethod = X::class.java.getMethod("testMethod"))
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """
                {DEFAULT}
                {indent:1}{METHOD}testMethod{DEFAULT}
                {DEFAULT}
            """.trimIndent()
                )

        }
    }

    @Nested
    inner class ParamHelp {
        @Test
        internal fun `Int with shortcut and description`() {
            class X {
                @Suppress("unused")
                @CliParam(
                    shortcut = "shortcut",
                    description = "description",
                )
                private var parameterName = 1
            }

            val definition = FieldDefinition(
                targetField = X::class.java.getDeclaredField("parameterName"),
                targetInstance = X()
            )
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """
                {indent:1}{PARAM}-shortcut --parameterName (int, default: 1) // description
                {DEFAULT}
            """.trimIndent()
                )

        }

        @Test
        internal fun `Integer without shortcut and description`() {
            class X {
                @Suppress("unused")
                @CliParam(
                    shortcut = "",
                    description = "",
                )
                private var parameterName: Int? = null
            }

            val definition = FieldDefinition(
                targetField = X::class.java.getDeclaredField("parameterName"),
                targetInstance = X()
            )
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """
                {indent:1}{PARAM}--parameterName (Integer, default: null)
                {DEFAULT}
            """.trimIndent()
                )

        }

        @Test
        internal fun `String without shortcut and description`() {
            class X {
                @Suppress("unused")
                @CliParam(
                    shortcut = "",
                    description = "",
                )
                private var parameterName: String = "defaultValue"
            }

            val definition = FieldDefinition(
                targetField = X::class.java.getDeclaredField("parameterName"),
                targetInstance = X()
            )
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """
                {indent:1}{PARAM}--parameterName (String, default: "defaultValue")
                {DEFAULT}
            """.trimIndent()
                )

        }

        @Test
        internal fun `Enum with shortcut and description`() {
            class X {
                @Suppress("unused")
                @CliParam(
                    shortcut = "shortcut",
                    description = "description",
                )
                private var parameterName: TestEnum = TestEnum.VALUE1
            }

            val definition = FieldDefinition(
                targetField = X::class.java.getDeclaredField("parameterName"),
                targetInstance = X()
            )
            definition.accept(helpGeneratorVisitor)
            assertThat(helpGeneratorVisitor.help)
                .isEqualTo(
                    """
                {indent:1}{PARAM}-shortcut --parameterName (TestEnum, default: VALUE1) // description
                {indent:2}VALUE1 (shortcut1) // description1
                {indent:2}VALUE2 (shortcut2)
                {indent:2}VALUE3
                {DEFAULT}
            """.trimIndent()
                )

        }

    }

    @Test
    internal fun `Acceptance test with complex definition`() {
        @CliClass(
            defaultRun = true,
            shortcut = "shortcut",
            description = "description"
        )
        class DefaultClass{
            @Suppress("unused")
            @CliParam(
                shortcut = "i1",
                description = "Integer 1"
            )
            val int1 = 1
            @Suppress("unused")
            @CliParam(
                shortcut = "s",
                description = ""
            )
            val string1 = "string1"

            @Suppress("unused")
            @CliParam(
                shortcut = "e",
                description = "Test enum value"
            )
            val testEnum: TestEnum = TestEnum.VALUE1

            @Suppress("unused")
            @CliFunction(
                defaultRun = true,
                shortcut = "shortcut",
                description = "description"
            )
            fun default() {}
            @Suppress("unused")
            @CliFunction(
                defaultRun = false,
                shortcut = "",
                description = ""
            )
            fun other1() {}
            @Suppress("unused")
            @CliFunction(
                defaultRun = false,
                shortcut = "",
                description = ""
            )
            fun other2() {}
        }

        @CliClass(
            defaultRun = false,
            shortcut = "",
            description = ""
        )
        class OtherClass

        val definition = ApplicationDefinition(
            classes = listOf(
                ClassDefinition(DefaultClass::class.java),
                ClassDefinition(OtherClass::class.java),
            )
        )
        definition.accept(helpGeneratorVisitor)
        @Suppress("unused")
        assertThat(helpGeneratorVisitor.help)
            .isEqualTo(
                """
                {DEFAULT}Java CLI runnable
                {indent:1}java -jar <application.jar> [<class name>.][<function name>] [parameters]
                {DEFAULT}
                {DEFAULT_CLASSNAME}DefaultClass{BOLD} (shortcut) - description (DEFAULT)
                {DEFAULT}{indent:1}{PARAM}-i1 --int1 (int, default: 1) // Integer 1
                {DEFAULT}{indent:1}{PARAM}-s --string1 (String, default: "string1")
                {DEFAULT}{indent:1}{PARAM}-e --testEnum (TestEnum, default: VALUE1) // Test enum value
                {indent:2}VALUE1 (shortcut1) // description1
                {indent:2}VALUE2 (shortcut2)
                {indent:2}VALUE3
                {DEFAULT}{DEFAULT}
                {indent:1}{DEFAULT_METHOD}default (shortcut){BOLD} (DEFAULT){DEFAULT} // description
                {DEFAULT}{DEFAULT}
                {indent:1}{METHOD}other1{DEFAULT}
                {DEFAULT}{DEFAULT}
                {indent:1}{METHOD}other2{DEFAULT}
                {DEFAULT}
                {CLASSNAME}OtherClass{DEFAULT}
                {DEFAULT}
            """.trimIndent()
            )
    }
}