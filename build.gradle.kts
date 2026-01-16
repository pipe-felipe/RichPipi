plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
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
