package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliParam
import java.lang.reflect.Field
import java.util.*
import kotlin.streams.toList

data class EnumValueDefinition(
    val name: String,
    val shortcut: String,
    val description: String,
)

class FieldDefinition(
    private val targetInstance: Any,
    private val targetField: Field
) : ApplicationDefinitionElement {
    init {
        targetField.trySetAccessible()
    }

    val annotation: CliParam get() = targetField.getAnnotation(CliParam::class.java)
    val name: String get() = targetField.name
    val typeName: String get() = targetField.type.simpleName
    val defaultValue: Any? get() = targetField.get(targetInstance)
    val enumValues: List<EnumValueDefinition>
        get() = when (targetField.type.isEnum) {
            true -> Arrays.stream(targetField.type.enumConstants)
                .map {
                    val valueAnnotation: CliParam? = targetField
                        .type
                        .getField(it.toString())
                        .getAnnotation(CliParam::class.java)
                    EnumValueDefinition(
                        name = it.toString(),
                        shortcut = valueAnnotation?.shortcut ?: "",
                        description = valueAnnotation?.description ?: "",
                    )
                }
                .toList()
            false -> listOf()
        }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

}
