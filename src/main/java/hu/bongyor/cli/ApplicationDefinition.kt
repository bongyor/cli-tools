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

}