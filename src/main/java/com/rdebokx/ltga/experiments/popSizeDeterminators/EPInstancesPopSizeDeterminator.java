package com.rdebokx.ltga.experiments.popSizeDeterminators;

import java.util.Arrays;

import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.parallel.EPJobRunner;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.Solution;

public class EPInstancesPopSizeDeterminator extends PopSizeDeterminator{
    
    private static int runs;
    private static JobConfiguration[] configs;
    
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
        loadJobConfiguration(args);
        if(configs != null){
            int[] popSizes = new int[SEARCHES];
            for(int i = 0; i < SEARCHES; i++){
                setPopSizes(1);
                popSizes[i] = determinePopSize(i);
            }
            
            Arrays.sort(popSizes);
            setPopSizes(popSizes[4]);
            
            
            int[] evaluations = getEvaluationsStatistics();
            Arrays.sort(evaluations);
            SequentialPopSizeDeterminator.printStatistics("EPInstancesPopSizeDeterminator", configs[0], popSizes, evaluations);
        } else {
            System.out.println("LTGA Terminated.");
        }
    }
    
    /**
     * This function determines the population size. If more than 1 run has failed, it will not perform any more searches and 
     * will proceed to the next population size to be tested. Once a window of possible minimum population sizes was determined,
     * a binary search will be done in this window to determine the minimum required population size.
     * @param config The configuration for which the minimum required population size has to be determined.
     * @return The minimum required population size that was determined.
     */
    private static int determinePopSize(int searchId){
        boolean found = tryPopSize();
        while(!found){
            System.out.println("Problem could not be solved for n=" + configs[0].GENETIC_CONFIG.POPULATION_SIZE + ". Trying for double this size.");
            setPopSizes(configs[0].GENETIC_CONFIG.POPULATION_SIZE * 2);
            found = tryPopSize();
        }
        System.out.println("Search " + searchId + ": Problem could be solved for n=" + configs[0].GENETIC_CONFIG.POPULATION_SIZE);
        return doBinSearch(searchId, configs[0].GENETIC_CONFIG.POPULATION_SIZE / 2, configs[0].GENETIC_CONFIG.POPULATION_SIZE);
    }

    /**
     * This function returns the JobConfiguration object that was parsed from the arguments that were provided to this program.
     * The configuration will be constructed as follows:
     * - The problem, provided by the first parameter.
     * - The number of parameters, provided by the second parameter.
     * - The population size, initially set to 1.
     * - The maximum amount evaluations, this is disabled.
     * - The useValueToReach, set to false.
     * - The valueToReach, not applicable.
     * - The fitnessVarianceTolerance, set to a value close to 0, to avoid rounding errors.
     * - The noImprovementStretch, this is disabled.
     * @param args The arguments for this program that should be parsed.
     * @return The constructed JobConfiguration based on the provided arguments.
     */
    private static void loadJobConfiguration(String[] args) {
        if(args.length == 5){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            runs = Integer.parseInt(args[2]);
            //fitnessTolerance = Double.parseDouble(args[3]);
            String inputMask = args[3];
            
            configs = new JobConfiguration[runs];
            
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double valueToReach = -1;
            final double fitnessVarianceTolerance = ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
            final int noImprovementStretch = -1;
            final int threads = Integer.parseInt(args[4]);
            
            for(int i = 0; i < runs; i++){
                ProblemConfiguration problemConfig = Main.readInstanceConfig(problem, numberOfParameters, inputMask + "_" + i + ".txt");
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
                configs[i] = new JobConfiguration(genConfig, execConfig, problemConfig);
            }
        } else {
            System.out.println("Arguments could not be parsed. Please provide: problem | numberOfParameters | runs | inputMask | #threads");
        }
    }
    
    /**
     * This function tests whether a solution could be found at least 99 out of 100 times. This function returns false
     * as soon as more than 1 run was not successful. It returns true otherwise.
     * @param jobConfig The configuration for the job to be tested, including the right population size.
     * @return Whether or not the proble could be solved for the given population size.
     */
    private static boolean tryPopSize(){
        int fails = 0;
        int runsCount = 0;
        
        System.out.print("Running run: ");
        while(runsCount < runs && fails < 2){
            System.out.print(runsCount + ", ");
            EPJobRunner runner = new EPJobRunner(configs[runsCount], false);
            runner.run();
            Solution best = runner.getBestFound();
            //System.out.println("Best solution found: " + best);
            InstancesConfiguration nkConfig = (InstancesConfiguration) configs[runsCount].PROBLEM_CONFIG;
            if(!Arrays.equals(nkConfig.OPTIMAL_SOLUTION, best.getSolution())){
                //System.out.println("===FAILED===");
                fails++;
            }
            
            /*
            if(nkConfig.OPTIMAL_VALUE - best.getObjectiveValue() > fitnessTolerance){
                System.out.println("===FAILED===");
                fails++;
            }
            */
            runsCount++;
        }
        System.out.println();
        //System.out.println("Runs performed before returning: " + runsCount + " of which " + fails + " were failed");
        return fails < 2;
    }
    
    /**
     * This method changes the population size in all config objects in the configs array to the given value.
     * @param popSize The required population size for all configs.
     */
    private static void setPopSizes(int popSize){
        for(int i = 0; i < runs; i++){
            configs[i] = configs[i].copyForPopSize(popSize);
        }
    }
    
    /**
     * This function performs a binary search for the minimum required population size within the given window.
     * @param start The lower bound of the window.
     * @param end The upper bound of the window.
     * @param jobConfig The JobConfiguration object of the job for which the minimum required population size should be determined. 
     * @param valueToReach The valueToReach which should be tested for in order to determine whether or not a run was succesful.
     * @return The determined minimum required population size within the given window.
     */
    private static int doBinSearch(int searchId, int start, int end){
        int result = -1;
        if(end - start <= 1){
            //save found pop size.
            System.out.println("Search " + searchId + ": Threshold population found: " + end);
            result = end;
            //save average needed evaluations
        } else {
	        int middle = Math.floorDiv(start + end, 2);
	        System.out.println("Do binsearch at " + middle);
	        setPopSizes(middle);
	        
	        //Do recursive bin search
	        int newStart;
	        int newEnd;
	        if(tryPopSize()){
	            newStart = start;
	            newEnd = middle;
	        } else {
	            newStart = middle;
	            newEnd = end;
	        }
            result = doBinSearch(searchId, newStart, newEnd);
        }
        return result;
    }
    
    /**
     * This function returns the evaluation statistics for 100 runs with the current configuration.
     * @return The array of evaluations performed in the 100 runs.
     */
    private static int[] getEvaluationsStatistics(){
        int[] evaluations = new int[runs];
        for(int i = 0; i < runs; i++){
        	System.out.println("Running run " + i + " for gathering evaluations statistics.");
            EPJobRunner runner = new EPJobRunner(configs[i], false);
            runner.run();
            evaluations[i] = runner.getNumberOfEvaluations();
        }
        return evaluations;
    }
    
}
