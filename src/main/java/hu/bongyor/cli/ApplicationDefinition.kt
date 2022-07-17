package hu.bongyor.cli

class ApplicationDefinition(
    private val classes: List<ClassDefinition>
) : ApplicationDefinitionElement {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
        classes
            .sortedBy { it.className }
            .sortedByDescending { it.annotation.defaultRun }
            .forEach { it.accept(visitor) }
    }

    fun execute(executeCommand: ExecuteCommand) {
        val runnableClass = when (executeCommand.classNameOrShortcut) {
            null -> classes.first { it.annotation.defaultRun }
            else -> classes.first {
                it.className == executeCommand.classNameOrShortcut ||
                        it.annotation.shortcut == executeCommand.classNameOrShortcut
            }
        }
        runnableClass.execute(executeCommand)
    }

}