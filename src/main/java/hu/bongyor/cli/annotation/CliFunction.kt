package hu.bongyor.cli.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class CliFunction(
    val defaultRun: Boolean = true,
    val shortcut: String = "",
    val description: String = ""
)