package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliParam
import java.lang.Integer.parseInt
import java.lang.Long.parseLong
import java.lang.reflect.Field
import java.util.*
import kotlin.streams.toList

data class EnumValueDefinition(
    val name: String,
    val shortcut: String,
    val description: String,
    val constant: Any,
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
                        constant = it
                    )
                }
                .toList()
            false -> listOf()
        }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    fun setValue(targetInstance: Any, valueAsString: String?) {
        targetField.set(targetInstance, parseValue(valueAsString))
    }

    private fun parseValue(valueAsString: String?): Any? {
        if (enumValues.isNotEmpty()) {
            return enumValues
                .first { it.name == valueAsString || it.shortcut == valueAsString }
                .constant
        }
        return when (typeName) {
            "Boolean", "boolean" -> parseBoolean(valueAsString)
            "String" -> valueAsString
            "Integer", "int" -> parseInt(valueAsString)
            "Long", "long" -> parseLong(valueAsString)
            else -> throw CliException("Not supported type: $typeName")
        }
    }

    fun parseBoolean(valueAsString: String?): Boolean = when (valueAsString) {
        null -> !(defaultValue as Boolean)
        else -> booleanPattern.matches(valueAsString)
    }

    companion object {
        val booleanPattern = Regex("^y|yes|true|t$", RegexOption.IGNORE_CASE)
    }
}
