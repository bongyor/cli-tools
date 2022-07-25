package hu.bongyor.cli

import hu.bongyor.cli.annotation.CliClass
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder


class ClassScanner {
    fun getCliClasses(): List<Class<*>> {
        val reflections = Reflections(
            ConfigurationBuilder()
                .forPackages("api")
                .addScanners(
                    Scanners.TypesAnnotated,
                    Scanners.FieldsAnnotated,
                    Scanners.MethodsAnnotated,
                )
        )
        return reflections.getTypesAnnotatedWith(CliClass::class.java).toList()
    }
}