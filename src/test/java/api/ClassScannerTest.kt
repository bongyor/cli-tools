package api

import hu.bongyor.cli.ClassScanner
import hu.bongyor.cli.annotation.CliClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@Suppress("unused")
@CliClass
class TestClass
internal class ClassScannerTest{
    @Test
    internal fun `Find classes annotated with @CliClass`() {
        val classes = ClassScanner().getCliClasses()
        assertThat(classes).isNotEmpty
    }
}