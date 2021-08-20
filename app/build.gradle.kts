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

/**************************
 * Sequential Executables *
 **************************/
//TODO: point to these tasks in the readme
//TODO: document terminology in readme

/**
 * Solve a randomly generated Onemax problem using the LTGA in sequential mode.
 */
task("RunSeqOnemax", JavaExec::class) {
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

/**
 * Solve a randomly generated Deceptive Trap with blocks size 5 using tight encoding, using the LTGA in sequential mode.
 */
task("RunSeqDeceptiveTrap", JavaExec::class) {
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

/**
 * Solve a provided NK Landscapes problem, using the LTGA in sequential mode.
 */
task("RunSeqNkLandscapes", JavaExec::class) {
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

/**
 * Solve a provided Maxcut problem, using the LTGA in sequential mode.
 */
task("RunSeqMaxcut", JavaExec::class) {
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

/********************************
 * Perfect Parallel Executables *
 ********************************/

/**
 * Solve a randomly generated Onemax problem using the LTGA in perfect parallel mode.
 */
//TODO: point to these tasks in the Readme
//TODO: document terminology in the Readme
task("RunPPOnemax", JavaExec::class) {
    main = "com.rdebokx.ltga.parallel.executables.Main"
    args = listOf(
            "ONEMAX", // Problem
            "10", // Number of parameters
            "16", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.00001", // Fitness value tolerance
            "25", // Max no improvement stretch
            "4" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}

/**
 * Solve a randomly generated Deceptive Trap with blocks size 5 using tight encoding, using the LTGA in perfect parallel mode.
 */
task("RunPPDeceptiveTrap", JavaExec::class) {
    main = "com.rdebokx.ltga.parallel.executables.Main"
    args = listOf(
            "DECEPTIVE_TRAP_5_TIGHT_ENCODING", // Problem
            "25", // Number of parameters
            "5", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.01", // Fitness tolerance
            "25", // Max no improvement stretch
            "4" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}

/**
 * Solve a provided NK Landscapes problem, using the LTGA in perfect parallel mode.
 */
task("RunPPNkLandscapes", JavaExec::class) {
    main = "com.rdebokx.ltga.parallel.executables.Main"
    args = listOf(
            "NK_LANDSCAPES", // Problem
            "10", // Number of parameters. Should equal the number of parameters in the provided file.
            "10", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.25", // Fitness tolerance
            "25", // Max no improvement stretch
            "src/main/resources/problemdata/nklandscapes/N10K5S1M6_0.txt", // File with problem definition.
            "4" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}

/**
 * Solve a provided Maxcut problem, using the LTGA in perfect parallel mode.
 */
task("RunPPMaxcut", JavaExec::class) {
    main = "com.rdebokx.ltga.parallel.executables.Main"
    args = listOf(
            "MAXCUT", // Problem
            "6", // Number of parameters. Should equal the number of parameters in the provided file.
            "10", // Population size
            "10000", // Max number of evaluations
            "false",  // Use value to reach
            "0", // Value to reach
            "0.5", // Fitness tolerance
            "25", // Max no improvement stretch
            "src/main/resources/problemdata/maxcut/", // Base directory with problem definition. Should contain the provided file and matching .bkv (Best Known Value) and .arv (Average Random Value) files in the BKV and ARV folder respectively.
            "n0000006i00", // File name of the file containing the problem definition, without .txt extension.
            "4" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}

/********************************
 * Embarrassingly Parallel Executables *
 ********************************/

/**
 * Solve a randomly generated Onemax problem using the LTGA in embarrassingly parallel mode.
 */
//TODO: point to these tasks in the Readme
//TODO: document terminology in the Readme
task("RunEPOnemax", JavaExec::class) {
    main = "com.rdebokx.ltga.parallel.executables.EmbarrassinglyParallel"
    args = listOf(
            "ONEMAX", // Problem
            "10", // Number of parameters
            "16", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.00001", // Fitness value tolerance
            "25", // Max no improvement stretch
            "4" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}

/**
 * Solve a randomly generated Deceptive Trap with blocks size 5 using tight encoding, using the LTGA in embarrassingly parallel mode.
 */
task("RunEPDeceptiveTrap", JavaExec::class) {
    main = "com.rdebokx.ltga.parallel.executables.EmbarrassinglyParallel"
    args = listOf(
            "DECEPTIVE_TRAP_5_TIGHT_ENCODING", // Problem
            "25", // Number of parameters
            "5", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.01", // Fitness tolerance
            "25", // Max no improvement stretch
            "4" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}

/**
 * Solve a provided NK Landscapes problem, using the LTGA in embarrassingly parallel mode.
 */
task("RunEPNkLandscapes", JavaExec::class) {
    main = "com.rdebokx.ltga.parallel.executables.EmbarrassinglyParallel"
    args = listOf(
            "NK_LANDSCAPES", // Problem
            "10", // Number of parameters. Should equal the number of parameters in the provided file.
            "10", // Population size
            "100000", // Max number of evaluations
            "false", // Use value to reach
            "0", // Value to reach
            "0.25", // Fitness tolerance
            "25", // Max no improvement stretch
            "src/main/resources/problemdata/nklandscapes/N10K5S1M6_0.txt", // File with problem definition.
            "4" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}

/**
 * Solve a provided Maxcut problem, using the LTGA in embarrassingly parallel mode.
 */
task("RunEPMaxcut", JavaExec::class) {
    main = "com.rdebokx.ltga.parallel.executables.EmbarrassinglyParallel"
    args = listOf(
            "MAXCUT", // Problem
            "6", // Number of parameters. Should equal the number of parameters in the provided file.
            "10", // Population size
            "10000", // Max number of evaluations
            "false",  // Use value to reach
            "0", // Value to reach
            "0.5", // Fitness tolerance
            "25", // Max no improvement stretch
            "src/main/resources/problemdata/maxcut/", // Base directory with problem definition. Should contain the provided file and matching .bkv (Best Known Value) and .arv (Average Random Value) files in the BKV and ARV folder respectively.
            "n0000006i00", // File name of the file containing the problem definition, without .txt extension.
            "4" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}

/************************************************************
 * Meta-LTGA: Determine the Recombinative Optimal Fixed FOS *
 ************************************************************/
//TODO: document this one in the Readme in particular
task("RunOptimalFixedFos", JavaExec::class) {
    main = "com.rdebokx.ltga.experiments.optimalFixedFOS.ROFFDeterminator"
    args = listOf(
            "ONEMAX", // Problem
            "5", // Number of parameters
            "2" // Threads
    )
    classpath = sourceSets["main"].runtimeClasspath
}
