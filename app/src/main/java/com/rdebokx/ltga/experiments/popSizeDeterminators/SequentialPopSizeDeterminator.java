package com.rdebokx.ltga.experiments.popSizeDeterminators;

import java.util.Arrays;

import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.experiments.optimalFixedFOS.OFFDeterminator;
import com.rdebokx.ltga.sequential.SequentialJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.Solution;

public class SequentialPopSizeDeterminator extends PopSizeDeterminator {
    
    private static final String SEEDS_FILE = "../data/seeds.txt";
    
    /**
     * Main entry point for the PopSizeDeterminator. This program will determine the minimum required population size that is needed
     * to solve the provided problem. The problem was solved if the valueToReach was reached for at least 99 out of 100 runs.
     * This is done by first determining a window within the minimum population size can probably be found, by doubling the population
     * size every time the problem could not be solved. Once the window was determined, a binary search was done for determining
     * the minimum required population size.
     * This entire cycle is executed 10 times, after which the average required population size is returned. 
     * @param args The arguments, containing the parameters for the problem for which the minimum required population size should be determined.
     * Please provide:
     * - The problem
     * - The amount of parameters
     * - The valueToReach
     */
    public static void main(String[] args) {
        JobConfiguration config = loadJobConfiguration(args);
        if(config != null){
            /*
            config = setPopSize(config, 64);
            tryPopSize(config, valueToReach);
            */
            
            long seeds[][] = OFFDeterminator.readSeeds(SEEDS_FILE);
            
            int[] popSizes = new int[SEARCHES];
            for(int i = 0; i < SEARCHES; i++){
                popSizes[i] = determinePopSize(i, config, seeds[i]);
            }
            Arrays.sort(popSizes);
            config = config.copyForPopSize(popSizes[4]);
            int[] evaluations = getEvaluationsStatistics(config, seeds);
            Arrays.sort(evaluations);
            
            printStatistics("PopSizeDeterminator", config, popSizes, evaluations);
        } else {
            System.out.println("LTGA Terminated.");
        }
    }
    
    /**
     * This function determines the population size. If more than 1 run has failed, it will not perform any more searches and 
     * will proceed to the next population size to be tested. Once a window of possible minimm population sizes was determined,
     * a binary search will be done in this window to determine the minimum required population size.
     * @param config The configuration for which the minumum required population size has to be determined.
     * @param valueToReach The valueToReach.
     * @param seeds An array of 100 seeds used for the Randomizers in the 100 runs.
     * @return The minimum required population size that was determined.
     */
    private static int determinePopSize(int searchId, JobConfiguration config, long[] seeds){
        boolean found = tryPopSize(config, seeds);
        while(!found){
            //System.out.println("Problem could not be solved for n=" + config.GENETIC_CONFIG.POPULATION_SIZE + ". Trying for double this size.");
            config = config.copyForPopSize(config.GENETIC_CONFIG.POPULATION_SIZE * 2);
            found = tryPopSize(config, seeds);
        }
        System.out.println("Search " + searchId + ": Problem could be solved for n=" + config.GENETIC_CONFIG.POPULATION_SIZE);
        return doBinSearch(searchId, config.GENETIC_CONFIG.POPULATION_SIZE / 2, config.GENETIC_CONFIG.POPULATION_SIZE, config, seeds);
    }

    /**
     * This function returns the JobConfiguration object that was parsed from the arguments that were provided to this program.
     * The configuration will be constructed as follows:
     * - The problem, provided by the first parameter.
     * - The number of parameters, provided by the second parameter.
     * - The population size, initially set to 1.
     * - The maximum amount evaluatsions, this is disabled.
     * - The useValueToReach, set to false.
     * - The valueToReach, not applicable.
     * - The fitnessVarianceTolerance, set to a value close to 0, to avoid rounding errors.
     * - The noImprovementStretch, this is disabled.
     * @param args The arguments for this program that should be parsed.
     * @return The constructed JobConfiguration based on the provided arguments.
     */
    public static JobConfiguration loadJobConfiguration(String[] args) {
        JobConfiguration result = null;
        if(args.length == 3 || args.length == 4){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double fitnessVarianceTolerance = 0.0000001;
            final int noImprovementStretch = -1;
            if(args.length == 4 && problem == Problem.MAXCUT){
                MaxCutConfiguration problemConfig = Main.readMaxCutConfiguration(numberOfParameters, args[2], args[3]);
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(1, noImprovementStretch, maxEvaluations, useValueToReach, problemConfig.BEST_KNOWN_VALUE, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, problemConfig);
            } else if(args.length == 4 && problem == Problem.NK_LANDSCAPES){
                InstancesConfiguration nkConfig = Main.readInstanceConfig(problem, numberOfParameters, args[2] + args[3]);
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(1, noImprovementStretch, maxEvaluations, useValueToReach, nkConfig.OPTIMAL_VALUE, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, nkConfig);
            } else if(args.length == 3){
                final double valueToReach = Double.parseDouble(args[2]);
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(1, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, new ProblemConfiguration(problem));
            } else {
                System.out.println("Arguments could not be parsed. Please provide the problem | numberOfParameters | valueToReach or\n"
                        + "MAXCUT/NK_LANDSCAPES | numberOfParameters | fileBase | inputFile");
            }
        } else {
            System.out.println("Arguments could not be parsed. Please provide the problem | numberOfParameters | valueToReach or\n"
                    + "MAXCUT/NK_LANDSCAPES | numberOfParameters | fileBase | inputFile");
        }
        
        return result;
    }
    
    /**
     * This function tests whether a solution could be found at least 99 out of 100 times. This function returns false
     * as soon as more than 1 run was not successful. It returns true otherwise.
     * @param jobConfig The configuration for the job to be tested, including the right population size.
     * @param valueToReach The valueToReach, which should be tested for in order to determine of a run was a success or not.
     * @param seeds Array containing 100 seeds for the Randomizers of the 100 runs.
     * @return Whether or not the proble could be solved for the given population size.
     */
    private static boolean tryPopSize(JobConfiguration jobConfig, long[] seeds){
        int fails = 0;
        int runs = 0;
        
        while(runs < RUNS && fails < 2){
            SequentialJobRunner runner = new SequentialJobRunner(jobConfig, false, seeds[runs]);
            runner.run();
            Solution best = runner.getBestFound();
            
            if(jobConfig.PROBLEM_CONFIG.PROBLEM == Problem.NK_LANDSCAPES && !Arrays.equals(((InstancesConfiguration) jobConfig.PROBLEM_CONFIG).OPTIMAL_SOLUTION, best.getSolution()) 
                    || jobConfig.PROBLEM_CONFIG.PROBLEM != Problem.NK_LANDSCAPES && Math.abs(best.getObjectiveValue() - jobConfig.EXECUTION_CONFIG.VALUE_TO_REACH) > .0000001){
                fails++;
            }
            runs++;
        }
        return fails < 2;
    }
    
    /**
     * This function performs a binary search for the minimum required population size within the given window.
     * @param searchId The id of the search, used for outputting where we are when the threshold population size is found.
     * @param start The lower bound of the window.
     * @param end The upper bound of the window.
     * @param jobConfig The JobConfiguration object of the job for which the minimum required population size should be determined.
     * @param seeds Array containing 100 seeds for the Randomizers in the 100 runs.
     * @return The determined minimum required population size within the given window.
     */
    private static int doBinSearch(int searchId, int start, int end, JobConfiguration jobConfig, long[] seeds){
        int result = -1;
        if(end - start <= 1){
            //save found pop size.
            System.out.println("Search " + searchId + ": Threshold population found: " + end);
            result = end;
            //save average needed evaluations
        } else {
	        int middle = Math.floorDiv(start + end, 2);
	        jobConfig = jobConfig.copyForPopSize(middle);
	        
	        //Do recursive bin search
	        int newStart;
	        int newEnd;
	        if(tryPopSize(jobConfig, seeds)){
	            newStart = start;
	            newEnd = middle;
	        } else {
	            newStart = middle;
	            newEnd = end;
	        }
            result = doBinSearch(searchId, newStart, newEnd, jobConfig, seeds);
        }
        return result;
    }
    
    /**
     * This function gathers evaluation statistics for the given configuration. This is done by performing 100 runs
     * with the given configuration, of which the amount of evaluations will be stored in and array and returned.
     * @param config The configuration for which evaluation statistics have to be gathered.
     * @param seeds Seeds for the Randomizers in the 10 runs. Only the first seeds of every array in will be used.
     * @return Array containing evaluations performed in 100 runs with the given configuration.
     */
    private static int[] getEvaluationsStatistics(JobConfiguration config, long[][] seeds){
        int[] evaluations = new int[SEARCHES];
        for(int i = 0; i < SEARCHES; i++){
            SequentialJobRunner runner = new SequentialJobRunner(config, false, seeds[i][0]);
            runner.run();
            evaluations[i] = runner.getJobState().getNumberOfEvaluations();
        }
        return evaluations;
    }
}
