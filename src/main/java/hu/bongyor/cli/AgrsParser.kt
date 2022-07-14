package hu.bongyor.cli

data class ParamSetRequest(
    val fieldShortcut: String?,
    val fieldName: String?,
    val value: String?,
)

data class ExecuteRequest(
    val classNameOrShortcut: String?,
    val functionNameOrShortcut: String?,
    val paramSetRequests: List<ParamSetRequest>
)

class ArgsParser(
    private val args: Array<String>
) {
    val isHelpRequest: Boolean get() = args.size == 1 && helpPattern.matches(args[0])
    val executeRequest: ExecuteRequest get() = ExecuteRequestBuilder(args.toList()).build()
    companion object {
        private val helpPattern = Regex("^(\\?)|(-\\?)|(h)|(-h)|(help)|(-help)|(--help)$")
    }
}

class ExecuteRequestBuilder(private val args: List<String>) {
    private var classNameOrShortcut: String? = null
    private var functionNameOrShortcut: String? = null
    private var firstArgIsClassOrFunction = false

    fun build(): ExecuteRequest {
        init()
        val paramArgs = when (firstArgIsClassOrFunction) {
            true -> args.drop(1)
            false -> args
        }
        return ExecuteRequest(
            classNameOrShortcut = classNameOrShortcut,
            functionNameOrShortcut = functionNameOrShortcut,
            paramSetRequests = ExecuteRequestParamBuilder(paramArgs).build()
        )
    }

    private fun init() {
        val classAndFunctionMatch = classAndFunctionPattern.matchEntire(args[0])
        if (classAndFunctionMatch != null) {
            firstArgIsClassOrFunction = true
            classNameOrShortcut = classAndFunctionMatch.groups["class"]?.value
            functionNameOrShortcut = classAndFunctionMatch.groups["function"]?.value
        }
    }

    companion object {
        private val classAndFunctionPattern = Regex("(?<class>[^.-]+)?(?:\\.?(?<function>[^.-]+))?")
    }
}

class ExecuteRequestParamBuilder(private val args: List<String>) {
    private var drop = 0
    private var shortcut: String? = null
    private var name: String? = null
    private var value: String? = null

    fun build(): List<ParamSetRequest> {
        if (args.isEmpty()) {
            return listOf()
        }

        fillShortcut()
        fillName()
        fillValue()
        val paramSetRequest = ParamSetRequest(
            fieldName = name,
            fieldShortcut = shortcut,
            value = value,
        )
        return listOf(paramSetRequest) + getOtherParamSetRequests()
    }

    private fun getOtherParamSetRequests(): List<ParamSetRequest> {
        var otherParams: List<ParamSetRequest> = listOf()
        if (args.size > drop) {
            otherParams = ExecuteRequestParamBuilder(args.drop(drop)).build()
        }
        return otherParams
    }

    private fun fillValue() {
        if (args.size > 1 && !nameOrShortcutPattern.matches(args[1])) {
            value = args[1]
            drop++
        }
    }

    private fun fillShortcut() {
        val shortcutMatcher = shortcutPattern.matchEntire(args[0])
        if (shortcutMatcher != null) {
            drop++
            shortcut = shortcutMatcher.groups[1]?.value
        }
    }

    private fun fillName() {
        if (shortcut != null) {
            return
        }
        val nameMatcher = namePattern.matchEntire(args[0])
        if (nameMatcher != null) {
            drop++
            name = nameMatcher.groups[1]?.value
        }
    }

    companion object {
        private val shortcutPattern = Regex("-([^-]+)")
        private val namePattern = Regex("--([^-]+)")
        private val nameOrShortcutPattern = Regex("^--?([^-]+)$")
    }
}