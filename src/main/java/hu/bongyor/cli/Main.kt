package hu.bongyor.cli

import hu.bongyor.cli.theme.LinuxConsoleTheme

var print = { str: String -> println(str) }
fun main(args: Array<String>) {
    val applicationDefinition = ApplicationDefinition(
        classes = ClassScanner()
            .getCliClasses()
            .map { ClassDefinition(targetClass = it) }
    )
    val argsParser = ArgsParser(args)
    if (argsParser.isHelpRequest) {
        val helpGenerator = HelpGeneratorVisitor(consoleTheme = LinuxConsoleTheme())
        applicationDefinition.accept(helpGenerator)
        print(helpGenerator.help)
    } else {
        applicationDefinition.execute(argsParser.executeCommand)
    }
}