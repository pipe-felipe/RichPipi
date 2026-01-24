plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    // Spotless for formatting Kotlin & organizing imports
    id("com.diffplug.spotless") version "6.21.0" apply false
}

// Convenience aggregator tasks

tasks.register("allUnitTests") {
    group = "verification"
    description = "Runs all unit tests for all modules (e.g. :composeApp:test)."
    dependsOn(":composeApp:test")
}

tasks.register("allInstrumentedTests") {
    group = "verification"
    description = "Runs Android instrumented tests (requires a connected device/emulator)."
    dependsOn(":composeApp:connectedDebugAndroidTest")
}

tasks.register("allTests") {
    group = "verification"
    description = "Runs the full test suite. By default this runs unit tests; run allInstrumentedTests separately when needed."
    dependsOn("allUnitTests")
}


subprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            // Use ktlint for formatting and basic lint rules
            ktlint("0.50.0").editorConfigOverride(mapOf(
                "max_line_length" to "off",
                "trailing_comma_on_call_site" to "false",
                "trailing_comma_on_declaration_site" to "false"
            ))
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint("0.48.2")
            trimTrailingWhitespace()
            endWithNewline()
        }

        format("miscKotlin") {
            target("**/*.kts")
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

tasks.register("formatKotlin") {
    group = "format"
    description = "Run Spotless formatting (spotlessApply) across all subprojects."
    dependsOn(subprojects.map { it.path + ":spotlessApply" })
}
