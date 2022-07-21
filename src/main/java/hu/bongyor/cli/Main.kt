package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliClass
import hu.bongyor.cli.annotation.CliFunction
import hu.bongyor.cli.annotation.CliParam
import hu.bongyor.cli.theme.LinuxConsoleTheme
import org.reflections.Reflections


var print = { str: String -> println(str) }
fun main(args: Array<String>) {
    val applicationDefinition = ApplicationDefinition(
        classes = ClassLoader
            .getSystemClassLoader()
            .definedPackages
            .flatMap {
                    packageRef -> Reflections(packageRef.name)
                .getTypesAnnotatedWith(CliClass::class.java)
                .map { targetClass -> ClassDefinition(targetClass = targetClass) }
            }
            .toList()
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