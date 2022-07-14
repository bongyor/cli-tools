package hu.bongyor.cli

class HelpGeneratorVisitor(
    private val consoleTheme: ConsoleTheme
) : Visitor {
    private var content = StringBuilder()
    val help get() = content.toString()
    override fun visit(definition: ApplicationDefinition) {
        content.append(consoleTheme.default)
        content.appendLine("Java CLI runnable")
        content.append(consoleTheme.indent(1))
        content.appendLine("java -jar <application.jar> [<class name>.][<function name>] [parameters]")
        content.append(consoleTheme.default)
    }

    override fun visit(definition: ClassDefinition) {
        val classStyle = when (definition.annotation.defaultRun) {
            true -> consoleTheme.defaultClassname
            false -> consoleTheme.classname
        }
        val className = definition.className
        val shortcut = when (definition.annotation.shortcut) {
            "" -> ""
            else -> " (${definition.annotation.shortcut})"
        }
        val description = when (definition.annotation.description) {
            "" -> ""
            else -> " - ${definition.annotation.description}"
        }
        val defaultFlag = when (definition.annotation.defaultRun) {
            true -> " (DEFAULT)"
            false -> ""
        }
        val detailFormat = when (definition.annotation.defaultRun) {
            true -> consoleTheme.bold
            false -> consoleTheme.default
        }
        content.appendLine("\n$classStyle$className$detailFormat$shortcut$description$defaultFlag")
        content.append(consoleTheme.default)
    }

    override fun visit(definition: FunctionDefinition) {
        val defaultTheme = consoleTheme.default
        val indent = consoleTheme.indent(1)
        val isDefault = definition.annotation.defaultRun
        val methodTheme = when {
            isDefault -> consoleTheme.defaultMethod
            else -> consoleTheme.method
        }
        val methodName = definition.name
        val shortcut = when {
            definition.annotation.shortcut.isNotBlank() -> " (${definition.annotation.shortcut})"
            else -> ""
        }
        val defaultFlag = when {
            isDefault -> "${consoleTheme.bold} (DEFAULT)"
            else -> ""
        }
        val description = when {
            definition.annotation.description.isNotBlank() -> " // ${definition.annotation.description}"
            else -> ""
        }

        content.appendLine(
            "$defaultTheme\n" +
                    "$indent$methodTheme$methodName$shortcut$defaultFlag$defaultTheme$description"
        )
        content.append(defaultTheme)
    }

    override fun visit(definition: FieldDefinition) {
        val defaultTheme = consoleTheme.default
        val indent = consoleTheme.indent(1)
        val paramTheme = consoleTheme.param
        val longVersion = "--${definition.name} "
        val shortcut = when {
            definition.annotation.shortcut.isNotBlank() -> "-${definition.annotation.shortcut} "
            else -> ""
        }
        val typeName = definition.typeName
        val defaultValue = when (typeName) {
            "String" -> """"${definition.defaultValue}""""
            else -> definition.defaultValue
        }
        val description = when {
            definition.annotation.description.isNotBlank() -> " // ${definition.annotation.description}"
            else -> ""
        }
        content.appendLine(
            "$indent$paramTheme$shortcut$longVersion($typeName, default: $defaultValue)$description"
        )
        val doubleIndent = consoleTheme.indent(2)
        definition.enumValues
            .forEach {
                val valueShortcut = when {
                    it.shortcut.isNotBlank() -> " (${it.shortcut})"
                    else -> ""
                }
                val valueDescription = when {
                    it.description.isNotBlank() -> " // ${it.description}"
                    else -> ""
                }
                content.appendLine(
                    "$doubleIndent${it.name}$valueShortcut$valueDescription"
                )
            }
        content.append(defaultTheme)
    }
}