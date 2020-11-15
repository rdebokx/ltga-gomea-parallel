package com.rdebokx.ltga.experiments.timers;

import java.util.Arrays;

import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.parallel.EPJobRunner;
import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;

public class EPTimer extends Timer {
    
    /**
     * The main entry point for the EPTimer. This program will gather statistics about the execution time of the algorithm
     * when executed with the given parameters. 100 runs are performed with the Embarrassingly Parallel implementation to gather these statistics.
     * @param args Please provide the problem | numberOfParameters | popSize | threads or problem | numberOfParameters | popSize | fileBase | fileName | threads for MAXCUT
     */
    public static void main(String[] args) {
        JobConfiguration config = loadJobConfiguration(args);
        if(config != null){
            long[] times = new long[RUNS];
            long[] constructMatrixTimes = new long[RUNS];
            long[] newSolutionTimes = new long[RUNS];
            long[] generations = new long[RUNS];
            System.out.print("Running run: ");
            for(int i = 0; i < RUNS; i++){
            	System.out.print(i + ", ");
            	EPJobRunner runner = new EPJobRunner(config, true);
            	runner.run();
            	
            	//Save values
            	times[i] = Profiler.getProgramTime();
            	constructMatrixTimes[i] = Profiler.getRecord("MIMatrix.constructMIMatrix");
            	newSolutionTimes[i] = Profiler.getRecord("Population.generateAndEvaluateNewSolutionsToFillOffspring");
            	generations[i] = runner.getNumberOfGenerations();
            	
            	Profiler.reset();
            }
            System.out.println();
            Arrays.sort(times);
            Arrays.sort(constructMatrixTimes);
            Arrays.sort(newSolutionTimes);
            Arrays.sort(generations);
            
            printStatistics("PPTimer", config, times, constructMatrixTimes, newSolutionTimes, generations);
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
     * - The amount of threads, provided by the fourth parameter.
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
        if(args.length == 4 || args.length == 6){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = Integer.parseInt(args[2]);
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double valueToReach = -1;
            final double fitnessVarianceTolerance = 0.0000001;
            final int noImprovementStretch = -1;
            final int threads = Integer.parseInt(args[args.length - 1]);
            
            if(args.length == 6 && problem == Problem.MAXCUT){
                MaxCutConfiguration problemConfig = Main.readMaxCutConfiguration(numberOfParameters, args[3], args[4]);
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, problemConfig.BEST_KNOWN_VALUE, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, problemConfig);
            } else if(args.length == 4){
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, new ProblemConfiguration(problem));
            } else {
                System.out.println("Arguments could not be parsed. Please provide the problem | numberOfParameters | popSize | threads or \n"
                        + "problem | numberOfParameters | popSize | fileBase | fileName | threads for MAXCUT");
            }
        } else {
            System.out.println("Arguments could not be parsed. Please provide the problem | numberOfParameters | popSize | threads or \n"
                    + "problem | numberOfParameters | popSize | fileBase | fileName | threads for MAXCUT");
        }
        
        return result;
    }
}
