package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliClass
import hu.bongyor.cli.annotation.CliFunction
import hu.bongyor.cli.annotation.CliParam
import java.util.*
import kotlin.streams.toList

class ClassDefinition(
    private val targetClass: Class<*>,
) : ApplicationDefinitionElement {
    val annotation: CliClass = targetClass.getAnnotation(CliClass::class.java)
    val className: String get() = targetClass.simpleName
    private val targetInstance: Any = targetClass.getDeclaredConstructor().newInstance()

    private val params: List<FieldDefinition> = Arrays.stream(targetClass.declaredFields)
        .filter { it.isAnnotationPresent(CliParam::class.java) }
        .map { FieldDefinition(targetField = it, targetInstance = targetInstance) }
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

    fun execute(executeCommand: ExecuteCommand) {
        executeCommand.paramSetCommands.forEach { paramSetCommand ->
            params
                .first {
                    paramSetCommand.fieldName != null &&
                            it.name == paramSetCommand.fieldName ||
                            paramSetCommand.fieldShortcut != null &&
                            it.annotation.shortcut == paramSetCommand.fieldShortcut
                }
                .setValue(targetInstance, paramSetCommand.value)
        }
        val runnableFunction = getExecutableFunction(executeCommand)
        runnableFunction.execute(targetInstance)
    }

    private fun getExecutableFunction(executeCommand: ExecuteCommand): FunctionDefinition {
        val runnableFunction = when (executeCommand.functionNameOrShortcut) {
            null -> functions.first { it.annotation.defaultRun }
            else -> functions.first {
                it.name == executeCommand.functionNameOrShortcut ||
                        it.annotation.shortcut == executeCommand.functionNameOrShortcut
            }
        }
        return runnableFunction
    }
}