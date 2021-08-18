package com.rdebokx.ltga.experiments.dynamic;

import java.util.Arrays;

import com.rdebokx.ltga.experiments.timers.Timer;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;
import com.rdebokx.ltga.parallel.EPJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;

public class EPInstancesDynamicLTGADeterminator extends Timer {
    
	private static JobConfiguration[] configs;
	
    /**
     * Main entry point for the PPDynamicLTGADeterminator. This program will gather statistics about the execution of the Dynamic 
     * PerfectParallel implementation of the algorithm with the provided parameters by simulating its behavior.
     * @param args Please provide the problem | numberOfParameters | threads or problem | numberOfParameters | fileBase | fileName | threads for MAXCUT
     */
    public static void main(String[] args) {
        loadJobConfiguration(args);
        if(configs != null){
            long[] times = new long[RUNS];
            long[] popSizes = new long[RUNS];
            final int start = Integer.parseInt(args[4]);
            System.out.print("Running run: ");
            for(int i = start; i < RUNS; i++){
            	//System.out.print(i + ", ");
            	
            	long timeStart = System.currentTimeMillis();
            	popSizes[i] = determinePopSize(configs[i]);
            	times[i] = System.currentTimeMillis() - timeStart;
            	System.out.println("Run" + i + " " + times[i] + " " + popSizes[i]);
            }
            System.out.println();
            Arrays.sort(times);
            Arrays.sort(popSizes);
            
            printStatistics("PPDynamicLTGADeterminator", configs[0], times, popSizes);
        } else {
            System.out.println("LTGA Terminated.");
        }
    }
    
    private static int determinePopSize(JobConfiguration config) {
    	
    	boolean[] optimalSolution = ((InstancesConfiguration) config.PROBLEM_CONFIG).OPTIMAL_SOLUTION;
    	EPJobRunner runner = new EPJobRunner(config, false);
		runner.run();
		boolean found = Arrays.equals(optimalSolution, runner.getBestFound().getSolution());
    	while(!found){
    		config = config.copyForPopSize(config.GENETIC_CONFIG.POPULATION_SIZE * 2);
    		//System.out.println("Trying for popSize " + config.GENETIC_CONFIG.POPULATION_SIZE);
    		runner = new EPJobRunner(config, false);
    		runner.run();
    		found = Arrays.equals(optimalSolution, runner.getBestFound().getSolution()); 
    	}
    	
		return config.GENETIC_CONFIG.POPULATION_SIZE;
	}

    /**
     * This function returns the JobConfiguration object that was parsed from the arguments that were provided to this program.
     * The configuration will be constructed as follows:
     * - The problem, provided by the first parameter.
     * - The number of parameters, provided by the second parameter.
     * - The population size, set to 1.
     * - The NK_LANDSCAPE configuration, based on the input mask of the 4th parameter.
     * - The amount of threads, provided by the 5th parameter.
     * - The maximum amount evaluations, this is disabled.
     * - The useValueToReach, set to false.
     * - The valueToReach, read from the instance files.
     * - The fitnessVarianceTolerance, set to a value close to 0, to avoid rounding errors.
     * - The noImprovementStretch, this is disabled.
     * @param args The arguments for this program that should be parsed.
     * @return The constructed JobConfiguration based on the provided arguments.
     */
    private static void loadJobConfiguration(String[] args) {
        if(args.length == 5){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            String inputMask = args[2];
            
            configs = new JobConfiguration[RUNS];
            
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double fitnessVarianceTolerance = ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
            final int noImprovementStretch = -1;
            final int threads = Integer.parseInt(args[3]);
            
            for(int i = 0; i < RUNS; i++){
                InstancesConfiguration problemConfig = null;
                switch(problem){
                case NK_LANDSCAPES:
                    problemConfig = Main.readInstanceConfig(problem, numberOfParameters, inputMask + "_" + i + ".txt");
                    break;
                default:
                    System.out.println("Problem could not be parsed! Please check your configuration.");
                }
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, problemConfig.OPTIMAL_VALUE, fitnessVarianceTolerance);
                configs[i] = new JobConfiguration(genConfig, execConfig, problemConfig);
            }
        } else {
            System.out.println("Arguments could not be parsed. Please provide: problem | numberOfParameters | inputMask | threads | start");
        }
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
