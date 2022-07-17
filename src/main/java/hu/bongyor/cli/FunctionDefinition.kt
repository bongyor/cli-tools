package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliFunction
import java.lang.reflect.Method

class FunctionDefinition(
    private val targetMethod: Method
) : ApplicationDefinitionElement {
    val name: String get() = targetMethod.name
    val annotation: CliFunction = targetMethod.getAnnotation(CliFunction::class.java)
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    fun execute(targetInstance: Any) {
        targetMethod.invoke(targetInstance)
    }

}
