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
//TODO: point to these tasks in the readme
//TODO: document terminology in readme
//TODO: document per task

task("RunSequentialOnemax", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf(
            "ONEMAX", // Problem
            "10", // Number of parameters
            "10", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.5", // Fitness value tolerance
            "25" // Max no improvement stretch
    )
    classpath = sourceSets["main"].runtimeClasspath
}
task("RunSequentialDeceptiveTrap", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf(
            "DECEPTIVE_TRAP_5_TIGHT_ENCODING", // Problem
            "25", // Number of parameters
            "5", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.01", // Fitness tolerance
            "25" // Max no improvements
    )
    classpath = sourceSets["main"].runtimeClasspath
}
task("RunSequentialNkLandscapes", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf(
            "NK_LANDSCAPES", // Problem
            "10", // Number of parameters. Should equal the number of parameters in the provided file.
            "10", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.25", // Fitness tolerance
            "25", // Max no improvements stretch
            "src/main/resources/problemdata/nklandscapes/N10K5S1M6_0.txt" // File with problem definition.
    )
    classpath = sourceSets["main"].runtimeClasspath
}
task("RunSequentialMaxcut", JavaExec::class) {
    main = "com.rdebokx.ltga.sequential.executables.Main"
    args = listOf(
            "MAXCUT", // Problem
            "6", // Number of parameters. Should equal the number of parameters in the provided file.
            "10", // Population size
            "10000", // Max number of evaluations
            "false",  // Use value to reach
            "0", // Value to reach
            "0.5", // Fitness tolerance
            "25", // Max no improvements stretch
            "src/main/resources/problemdata/maxcut/", // Base directory with problem definition. Should contain the provided file and matching .bkv (Best Known Value) and .arv (Average Random Value) files in the BKV and ARV folder respectively.
            "n0000006i00" // File name of the file containing the problem definition, without .txt extension.
    )
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
