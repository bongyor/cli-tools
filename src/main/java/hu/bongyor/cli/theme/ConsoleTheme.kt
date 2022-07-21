package hu.bongyor.cli.theme

interface ConsoleTheme {
    val param: String
    val defaultMethod: String
    val method: String
    val classname: String
    val default: String
    val defaultClassname: String
    val bold: String
    fun indent(indentation: Int): String
}

class LinuxConsoleTheme : ConsoleTheme {
    override val param: String
        get() = "\u001B[92m"
    override val defaultMethod: String
        get() = "\u001B[32;4m"
    override val method: String
        get() = "\u001B[32m"
    override val classname: String
        get() = "\u001B[32m"
    override val default: String
        get() = "\u001B[0m"
    override val defaultClassname: String
        get() = "\u001B[32;4m"
    override val bold: String
        get() = "\u001B[1m"

    override fun indent(indentation: Int): String = "  ".repeat(indentation)

}