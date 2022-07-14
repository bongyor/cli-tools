package hu.bongyor.cli

interface ApplicationDefinitionElement {
    fun accept(visitor: Visitor)
}