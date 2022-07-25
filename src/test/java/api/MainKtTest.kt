package api

import hu.bongyor.cli.annotation.CliClass
import hu.bongyor.cli.annotation.CliFunction
import hu.bongyor.cli.main
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Suppress("unused")
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
internal class MainKtTest {


    private val printedLines = mutableListOf<String>()

    @BeforeEach
    internal fun setUp() {
        MainKtTestXClass.defaultExecuted = false
        print("")
        hu.bongyor.cli.print = { printedLines.add(it) }
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