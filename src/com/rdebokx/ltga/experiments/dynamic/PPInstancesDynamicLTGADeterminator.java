package com.rdebokx.ltga.experiments.dynamic;

import java.util.Arrays;

import com.rdebokx.ltga.experiments.timers.Timer;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;
import com.rdebokx.ltga.parallel.DynamicJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;

public class PPInstancesDynamicLTGADeterminator extends Timer {
    
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
            long[] evals = new long[RUNS];
            System.out.print("Running run: ");
            for(int i = 0; i < RUNS; i++){
                System.out.print(i + ", ");
                
                JobConfiguration runnersConfig = PPDynamicLTGADeterminator.getRunnersConfig(configs[i]);
                
                long timeStart = System.currentTimeMillis();
                DynamicJobRunner runner = new DynamicJobRunner(configs[i], runnersConfig, false);
                runner.run();
                times[i] = System.currentTimeMillis() - timeStart;
                popSizes[i] = runner.getConfig().GENETIC_CONFIG.POPULATION_SIZE / 2;
                evals[i] = runner.getJobState().getNumberOfEvaluations();
            }
            System.out.println();
            Arrays.sort(times);
            Arrays.sort(popSizes);
            Arrays.sort(evals);
            
            PPDynamicLTGADeterminator.printStatistics("PPDynamicLTGADeterminator", configs[0], popSizes, evals);
        } else {
            System.out.println("LTGA Terminated.");
        }
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
        if(args.length == 4){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            String inputMask = args[2];
            
            configs = new JobConfiguration[RUNS];
            
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = true;
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
            System.out.println("Arguments could not be parsed. Please provide: problem | numberOfParameters | inputMask | threads");
        }
    }
}
