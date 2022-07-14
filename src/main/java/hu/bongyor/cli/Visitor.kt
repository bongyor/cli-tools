package hu.bongyor.cli

interface Visitor {
    fun visit(definition: ApplicationDefinition)
    fun visit(definition: ClassDefinition)
    fun visit(definition: FunctionDefinition)
    fun visit(definition: FieldDefinition)
}