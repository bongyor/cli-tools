package hu.bongyor.cli.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class CliParam(
    val shortcut: String = "",
    val description: String = ""
)