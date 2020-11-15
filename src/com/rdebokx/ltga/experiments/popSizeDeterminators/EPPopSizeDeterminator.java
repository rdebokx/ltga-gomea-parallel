package com.rdebokx.ltga.experiments.popSizeDeterminators;

import java.util.Arrays;

import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.parallel.EPJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.Solution;

public class EPPopSizeDeterminator extends PopSizeDeterminator{
    
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

            int[] popSizes = new int[SEARCHES];
            for(int i = 0; i < SEARCHES; i++){
                popSizes[i] = determinePopSize(i, config);
            }
            
            Arrays.sort(popSizes);
            config = config.copyForPopSize(popSizes[4]);
            int[] evaluations = getEvaluationsStatistics(config);
            Arrays.sort(evaluations);
            
            SequentialPopSizeDeterminator.printStatistics("EPPopSizeDeterminator", config, popSizes, evaluations);
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
     * @return The minimum required population size that was determined.
     */
    private static int determinePopSize(int searchId, JobConfiguration config){
        boolean found = tryPopSize(config);
        while(!found){
            System.out.println("Problem could not be solved for n=" + config.GENETIC_CONFIG.POPULATION_SIZE + ". Trying for double this size.");
            config = config.copyForPopSize(config.GENETIC_CONFIG.POPULATION_SIZE * 2);
            found = tryPopSize(config);
        }
        System.out.println("Search " + searchId + ": Problem could be solved for n=" + config.GENETIC_CONFIG.POPULATION_SIZE);
        return doBinSearch(searchId, config.GENETIC_CONFIG.POPULATION_SIZE / 2, config.GENETIC_CONFIG.POPULATION_SIZE, config);
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
    private static JobConfiguration loadJobConfiguration(String[] args) {
        JobConfiguration result = null;
        
        if(args.length == 4 || args.length == 5){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double fitnessVarianceTolerance = 0.0000001;
            final int noImprovementStretch = -1;
            
            if(args.length == 5 && problem == Problem.MAXCUT){
                final int threads = Integer.parseInt(args[4]);
                MaxCutConfiguration problemConfig = Main.readMaxCutConfiguration(numberOfParameters, args[2], args[3]);
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, problemConfig.BEST_KNOWN_VALUE, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, problemConfig);
            } else if(args.length == 4){
                final int threads = Integer.parseInt(args[3]);
                final double valueToReach = Double.parseDouble(args[2]);
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
                result = new JobConfiguration(genConfig, execConfig, new ProblemConfiguration(problem));
            } else {
                System.out.println("Arguments could not be parsed. Please provide the problem | numberOfParameters | valueToReach | threads or\n"
                        + "MAXCUT | numberOfParameters | fileBase | inputFile | threads");
            }
        } else {
            System.out.println("Arguments could not be parsed. Please provide the problem | numberOfParameters | valueToReach | threads or\n"
                    + "MAXCUT | numberOfParameters | fileBase | inputFile | threads");
        }
        
        return result;
    }
    
    /**
     * This function tests whether a solution could be found at least 99 out of 100 times. This function returns false
     * as soon as more than 1 run was not successful. It returns true otherwise.
     * @param jobConfig The configuration for the job to be tested, including the right population size.
     * @param valueToReach The valueToReach, which should be tested for in order to determine of a run was a success or not.
     * @return Whether or not the proble could be solved for the given population size.
     */
    private static boolean tryPopSize(JobConfiguration jobConfig){
        int fails = 0;
        int runs = 0;
        
        while(runs < RUNS && fails < 2){
            EPJobRunner runner = new EPJobRunner(jobConfig, false);
            runner.run();
            Solution best = runner.getBestFound();
            if(Math.abs(best.getObjectiveValue() - jobConfig.EXECUTION_CONFIG.VALUE_TO_REACH) > .0000001){
                fails++;
            }
            runs++;
        }
        return fails < 2;
    }
    
    /**
     * This function performs a binary search for the minimum required population size within the given window.
     * @param start The lower bound of the window.
     * @param end The upper bound of the window.
     * @param jobConfig The JobConfiguration object of the job for which the minimum required population size should be determined. 
     * @param valueToReach The valueToReach which should be tested for in order to determine whether or not a run was succesful.
     * @return The determined minimum required population size within the given window.
     */
    private static int doBinSearch(int searchId, int start, int end, JobConfiguration jobConfig){
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
	        if(tryPopSize(jobConfig)){
	            newStart = start;
	            newEnd = middle;
	        } else {
	            newStart = middle;
	            newEnd = end;
	        }
            result = doBinSearch(searchId, newStart, newEnd, jobConfig);
        }
        return result;
    }
    
    /**
     * This function returns the array with evaluation statistics for the given configuration.
     * @param config The configuration for which evaluation statistics have to be gathered.
     * @return The array with evaluations made for the 100 runs.
     */
    private static int[] getEvaluationsStatistics(JobConfiguration config){
        int[] evaluations = new int[RUNS];
        for(int i = 0; i < RUNS; i++){
            EPJobRunner runner = new EPJobRunner(config, false);
            runner.run();
            evaluations[i] = runner.getNumberOfEvaluations();
        }
        return evaluations;
    }
    
}
