package com.rdebokx.ltga.experiments.popSizeDeterminators;

import java.util.Arrays;

import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.sequential.SequentialJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.Solution;

public class SequentialInstancesPopSizeDeterminator {
    
    private final static int SEARCHES = 10;
    private static int runs = 100;
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
            SequentialPopSizeDeterminator.printStatistics("InstancesPopSizeDeterminator", configs[0], popSizes, evaluations);
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
        if(args.length == 4){
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
            
            for(int i = 0; i < runs; i++){
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
            System.out.println("Arguments could not be parsed. Please provide: problem | numberOfParameters | runs | inputMask");
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
            SequentialJobRunner runner = new SequentialJobRunner(configs[runsCount], false);
            runner.run();
            Solution best = runner.getBestFound();
            //System.out.println("Best solution found: " + best);
            InstancesConfiguration nkConfig = (InstancesConfiguration) configs[runsCount].PROBLEM_CONFIG;
            if(!Arrays.equals(nkConfig.OPTIMAL_SOLUTION, best.getSolution())){
                //System.out.println("===FAILED===");
                fails++;
            }
            
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
     * @param searchId The id of the search for which we're looking for the population size.
     * @param start The lower bound of the window.
     * @param end The upper bound of the window.
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
		    setPopSizes(middle);
		    System.out.println("Try binSearch at " + middle);
		    
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
     * This function gathers evaluation statistics based on the current configuration. These statistics are based
     * on 100 runs with the current configuration.
     * @return Array containing the evaluations of 100 test runs. 
     */
    private static int[] getEvaluationsStatistics(){
        int[] evaluations = new int[runs];
        for(int i = 0; i < runs; i++){
            SequentialJobRunner runner = new SequentialJobRunner(configs[i], false);
            runner.run();
            evaluations[i] = runner.getJobState().getNumberOfEvaluations();
        }
        return evaluations;
    }
    
}
