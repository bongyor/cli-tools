package hu.bongyor.cli.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class CliClass(
    val defaultRun: Boolean = true,
    val shortcut: String = "",
    val description: String = ""
)