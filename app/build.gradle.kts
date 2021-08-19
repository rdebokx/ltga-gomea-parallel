import org.gradle.api.tasks.options.Option

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit test framework.
    testImplementation("junit:junit:4.13.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")
}

application {
    // Define the main class for the application.
    mainClass.set("com.rdebokx.ltga.App") //TODO: update this: run parallel by default (demo run)
}

/**
 * Sequential Executables
 */
//TODO: document these and their formats in the readme
task("RunSequentialOnemax", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf("ONEMAX", "10", "10", "100000", "false", "0", "0.5", "25")
    classpath = sourceSets["main"].runtimeClasspath
}
task("RunSequentialDeceptiveTrap", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf("DECEPTIVE_TRAP_5_TIGHT_ENCODING", "25", "5", "100000", "true", "1111111111111111111111111", "0.01", "25")
    classpath = sourceSets["main"].runtimeClasspath
}
task("RunSequentialNkLandscapes", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf("NK_LANDSCAPES", "10", "10", "100000", "false", "0", "0.25", "25", "src/main/resources/problemdata/nklandscapes/N10K5S1M6_0.txt")
    classpath = sourceSets["main"].runtimeClasspath
}
task("RunSequentialMaxcut", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf("MAXCUT", "6", "10", "10000", "false", "0", "0.5", "25", "src/main/resources/problemdata/maxcut/", "n0000006i00")
    classpath = sourceSets["main"].runtimeClasspath
}

/**
 * Parallel Executables
 */
//TOOD: task for parallel execution
//TODO: document these and their formats in the readme

//TODO; include this one:
task("RunParallelOptimalFixedFos", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf("OPTIMAL_FIXED_FOS", "10", "10", "100", "false", "0", "0.5", "25")
    classpath = sourceSets["main"].runtimeClasspath
}

/**
 * Experiments
 */

//TODO
