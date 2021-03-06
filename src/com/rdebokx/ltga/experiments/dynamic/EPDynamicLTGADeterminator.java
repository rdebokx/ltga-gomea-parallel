package com.rdebokx.ltga.experiments.dynamic;

import java.util.Arrays;

import com.rdebokx.ltga.experiments.timers.Timer;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.parallel.EPJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;

public class EPDynamicLTGADeterminator extends Timer {
    
    /**
     * Main entry point for the PPDynamicLTGADeterminator. This program will gather statistics about the execution of the Dynamic 
     * PerfectParallel implementation of the algorithm with the provided parameters by simulating its behavior.
     * @param args Please provide the problem | numberOfParameters | threads or problem | numberOfParameters | fileBase | fileName | threads for MAXCUT
     */
    public static void main(String[] args) {
        JobConfiguration config = loadJobConfiguration(args);
        if(config != null){
            long[] times = new long[RUNS];
            long[] popSizes = new long[RUNS];
            System.out.print("Running run: ");
            for(int i = 0; i < RUNS; i++){
            	System.out.print(i + ", ");
            	
            	long timeStart = System.currentTimeMillis();
            	popSizes[i] = determinePopSize(config);
            	times[i] = System.currentTimeMillis() - timeStart;
            }
            System.out.println();
            Arrays.sort(times);
            Arrays.sort(popSizes);
            
            printStatistics("EPDynamicLTGADeterminator", config, times, popSizes);
        } else {
            System.out.println("LTGA Terminated.");
        }
    }
    
    private static int determinePopSize(JobConfiguration config) {
    	EPJobRunner runner = new EPJobRunner(config, false);
		runner.run();
		boolean found = config.EXECUTION_CONFIG.VALUE_TO_REACH - runner.getBestFound().getObjectiveValue() < ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
    	while(!found){
    		config = config.copyForPopSize(config.GENETIC_CONFIG.POPULATION_SIZE * 2);
    		runner = new EPJobRunner(config, false);
    		runner.run();
    		found = config.EXECUTION_CONFIG.VALUE_TO_REACH - runner.getBestFound().getObjectiveValue() < ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
    	}
    	
		return config.GENETIC_CONFIG.POPULATION_SIZE;
	}

	/**
     * This function returns the JobConfiguration object that was parsed from the arguments that were provided to this program.
     * The configuration will be constructed as follows:
     * - The problem, provided by the first parameter.
     * - The number of parameters, provided by the second parameter.
     * - The population size, initially set to 1.
     * - The amount of threads, provided by the 3rd parameter.
     * - The maximum amount evaluations, this is disabled.
     * - The useValueToReach, set to false.
     * - The valueToReach, not applicable.
     * - The fitnessVarianceTolerance, set to a value close to 0, to avoid rounding errors.
     * - The noImprovementStretch, this is disabled.
     * @param args The arguments for this program that should be parsed.
     * @return The constructed JobConfiguration based on the provided arguments.
     */
    public static JobConfiguration loadJobConfiguration(String[] args) {
        JobConfiguration result = null;
        if(args.length == 4 || args.length == 5){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            double valueToReach = -1;
            if(problem != Problem.MAXCUT){
                valueToReach = Integer.parseInt(args[2]);
            }
            final double fitnessVarianceTolerance = 0.0000001;
            final int noImprovementStretch = -1;
            final int threads = Integer.parseInt(args[args.length - 1]);
            
            if(args.length == 5 && problem == Problem.MAXCUT){
                MaxCutConfiguration problemConfig = Main.readMaxCutConfiguration(numberOfParameters, args[2], args[3]);
                valueToReach = problemConfig.BEST_KNOWN_VALUE;
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, problemConfig.BEST_KNOWN_VALUE, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, problemConfig);
            } else if(args.length == 4){
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, new ProblemConfiguration(problem));
            } else {
                System.out.println("Arguments could not be parsed. Please provide the problem | numberOfParameters | valueToReach | threads or \n"
                        + "problem | numberOfParameters | fileBase | fileName | threads for MAXCUT");
            }
        } else {
        	System.out.println("Arguments could not be parsed. Please provide the problem | numberOfParameters | valueToReach | threads or \n"
                    + "problem | numberOfParameters | fileBase | fileName | threads for MAXCUT");
        }
        
        return result;
    }
    
    /**
     * This function prints the timing and population size statistics that were found by the given Timer. 
     * @param determinator The type of timer that was executed.
     * @param config The configuration for which the determinator was run.
     * @param times Sorted array of total execution times that were recorded.
     * @param popSizes Sorted array of population sizes for which the solutions were found.
     */
    public static void printStatistics(String determinator, JobConfiguration config, long[] times, long[] popSizes){
        System.out.println(determinator + " finished for " + config.PROBLEM_CONFIG.PROBLEM + " with l=" + config.GENETIC_CONFIG.NUMBER_OF_PARAMETERS);
        System.out.println("Printing: Time avg | Time var | Time worst | Time 10% | Time 50% | Time 90% | Time best | "
                + "PopSize avg | PopSize var | PopSize worst | PopSize 10% | PopSize 50% | PopSize 90% | PopSize best");
        
        printArrayStatistics(times, " ");
        printArrayStatistics(popSizes, " ");
        System.out.println();
		
		        
    }
}
