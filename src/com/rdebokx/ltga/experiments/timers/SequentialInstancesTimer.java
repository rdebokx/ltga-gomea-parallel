package com.rdebokx.ltga.experiments.timers;

import java.util.Arrays;

import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.sequential.SequentialJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;

public class SequentialInstancesTimer extends Timer {
    
    private static JobConfiguration[] configs;
    
    /**
     * Main entry point for the InstancesTimer. This program will gather statistics about the execution times of the sequential implementation
     * for NK_LANDSCAPES. This is done by running all 100 instances with the given parameters.
     * @param args Please provide: problem | numberOfParameters | popSize | inputMask
     */
    public static void main(String[] args) {
        loadJobConfiguration(args);
        if(configs != null){
        	long[] times = new long[RUNS];
            long[] constructMatrixTimes = new long[RUNS];
            long[] newSolutionTimes = new long[RUNS];
            long[] generations = new long[RUNS];
            System.out.print("Running run: ");
            for(int i = 0; i < RUNS; i++){
            	System.out.print(i + ", ");
            	SequentialJobRunner runner = new SequentialJobRunner(configs[i], false);
            	runner.run();
            	
            	//Save values
            	times[i] = Profiler.getProgramTime();
            	constructMatrixTimes[i] = Profiler.getRecord("MIMatrix.constructMIMatrix");
            	newSolutionTimes[i] = Profiler.getRecord("Population.generateAndEvaluateNewSolutionsToFillOffspring");
            	generations[i] = runner.getJobState().getNumberOfGenerations();
            	
            	Profiler.reset();
            }
            System.out.println();
            Arrays.sort(times);
            Arrays.sort(constructMatrixTimes);
            Arrays.sort(newSolutionTimes);
            Arrays.sort(generations);
            
            printStatistics("InstancesTimer", configs[0], times, constructMatrixTimes, newSolutionTimes, generations);
        } else {
            System.out.println("LTGA Terminated.");
        }
    }
    
    /**
     * This function returns the JobConfiguration object that was parsed from the arguments that were provided to this program.
     * The configuration will be constructed as follows:
     * - The problem, provided by the first parameter.
     * - The number of parameters, provided by the second parameter.
     * - The population size, provided by the third parameter.
     * - The maximum amount evaluations, this is disabled.
     * - The useValueToReach, set to false.
     * - The valueToReach, not applicable.
     * - The fitnessVarianceTolerance, set to a value close to 0, to avoid rounding errors.
     * - The noImprovementStretch, this is disabled.
     * @param args The arguments for this program that should be parsed.
     * @return The constructed JobConfiguration based on the provided arguments.
     */
    private static void loadJobConfiguration(String[] args) {
        if(args.length == 4){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            String inputMask = args[3];
            
            configs = new JobConfiguration[RUNS];
            
            final int populationSize = Integer.parseInt(args[2]);
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double valueToReach = -1;
            final double fitnessVarianceTolerance = ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
            final int noImprovementStretch = -1;
            
            for(int i = 0; i < RUNS; i++){
                ProblemConfiguration problemConfig = null;
                switch(problem){
                case NK_LANDSCAPES:
                    problemConfig = Main.readInstanceConfig(problem, numberOfParameters, inputMask + "_" + i + ".txt");
                    break;
                default:
                    System.out.println("Problem could not be parsed! Please check your configuration.");
                }
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(1, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
                configs[i] = new JobConfiguration(genConfig, execConfig, problemConfig);
            }
        } else {
            System.out.println("Arguments could not be parsed. Please provide: problem | numberOfParameters | popSize | inputMask");
        }
    }
    
    
}
