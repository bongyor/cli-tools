package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliClass
import hu.bongyor.cli.annotation.CliFunction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MainKtTest {
    @CliClass
    class MainKtTestXClass {
        @CliFunction
        fun defaultFun() {
            defaultExecuted = true
        }

        companion object {
            var defaultExecuted = false
        }
    }

    private val printedLines = mutableListOf<String>()

    @BeforeEach
    internal fun setUp() {
        MainKtTestXClass.defaultExecuted = false
        print("")
        print = { printedLines.add(it) }
    }

    @Test
    fun `Help printed`() {
        main(arrayOf("-h"))
        assertThat(printedLines.joinToString())
            .contains("MainKtTestXClass")
            .contains("defaultFun")
    }

    @Test
    internal fun `Default executed`() {
        main(arrayOf("MainKtTestXClass"))
        assertThat(MainKtTestXClass.defaultExecuted).isTrue
    }
}