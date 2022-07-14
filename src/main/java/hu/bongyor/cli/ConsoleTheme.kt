package hu.bongyor.cli

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