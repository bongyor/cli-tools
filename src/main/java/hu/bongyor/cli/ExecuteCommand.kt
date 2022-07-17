package hu.bongyor.cli

data class ExecuteCommand(
    val classNameOrShortcut: String?,
    val functionNameOrShortcut: String?,
    val paramSetCommands: List<ParamSetCommand>
)

data class ParamSetCommand(
    val fieldShortcut: String?,
    val fieldName: String?,
    val value: String?,
)