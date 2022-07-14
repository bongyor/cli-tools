package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliClass
import hu.bongyor.cli.annotation.CliFunction
import hu.bongyor.cli.annotation.CliParam
import java.util.*

class ClassDefinition(
    private val targetClass: Class<*>,
) :ApplicationDefinitionElement {
    val annotation: CliClass = targetClass.getAnnotation(CliClass::class.java)
    val className: String get() = targetClass.simpleName

    private val params: List<FieldDefinition> = Arrays.stream(targetClass.declaredFields)
        .filter { it.isAnnotationPresent(CliParam::class.java) }
        .map { FieldDefinition(targetField = it) }
        .toList()

    private val functions: List<FunctionDefinition> = Arrays.stream(targetClass.methods)
        .filter { it.isAnnotationPresent(CliFunction::class.java) }
        .map { FunctionDefinition(targetMethod = it) }
        .toList()

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
        params
            .forEach { it.accept(visitor) }
        functions
            .sortedBy { it.name }
            .sortedByDescending { it.annotation.defaultRun }
            .forEach { it.accept(visitor) }

    }
}