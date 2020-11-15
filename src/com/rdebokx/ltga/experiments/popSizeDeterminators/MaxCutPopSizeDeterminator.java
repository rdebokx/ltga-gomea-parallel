package com.rdebokx.ltga.experiments.popSizeDeterminators;

import java.util.Arrays;

import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.sequential.SequentialJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.Solution;

public class MaxCutPopSizeDeterminator extends PopSizeDeterminator {
	
	private static int instances;
	private static final int RUNS = 100;
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
            int[] popSizes = new int[instances];
            for(int i = 0; i < instances; i++){
                popSizes[i] = determinePopSize(i, configs[i], ((MaxCutConfiguration) configs[i].PROBLEM_CONFIG).BEST_KNOWN_VALUE);
            }
            Arrays.sort(popSizes);
            
            //TODO
            /*
            config = setPopSize(config, popSizes[4]);
            int[] evaluations = getEvaluationsStatistics();
            Arrays.sort(evaluations);
            */
            int[] evaluations = new int[instances];
            
            printStatistics(popSizes, evaluations);
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
    private static int determinePopSize(int searchId, JobConfiguration config, double valueToReach){
        boolean found = tryPopSize(config, valueToReach);
        while(!found){
            //System.out.println("Problem could not be solved for n=" + config.GENETIC_CONFIG.POPULATION_SIZE + ". Trying for double this size.");
            config = config.copyForPopSize(config.GENETIC_CONFIG.POPULATION_SIZE * 2);
            found = tryPopSize(config, valueToReach);
        }
        System.out.println("Search " + searchId + ": Problem could be solved for n=" + config.GENETIC_CONFIG.POPULATION_SIZE);
        return doBinSearch(searchId, config.GENETIC_CONFIG.POPULATION_SIZE / 2, config.GENETIC_CONFIG.POPULATION_SIZE, config, valueToReach);
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
            final int numberOfParameters = Integer.parseInt(args[0]);
            instances = Integer.parseInt(args[1]);
            String fileBase = args[2];
            String inputMask = args[3];
            
            configs = new JobConfiguration[instances];
            
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double valueToReach = -1;
            final double fitnessVarianceTolerance = ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
            final int noImprovementStretch = -1;
            
            for(int i = 0; i < instances; i++){
            	String instance = i < 10 ? "0" + i : "" + i;
                MaxCutConfiguration problemConfig = Main.readMaxCutConfiguration(numberOfParameters, fileBase, inputMask + instance);
                GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
                ExecutionConfiguration execConfig = new ExecutionConfiguration(1, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
                configs[i] = new JobConfiguration(genConfig, execConfig, problemConfig);
            }
        } else {
            System.out.println("Arguments could not be parsed. Please provide: numberOfParameters | instances | fileBase | inputMask");
        }
    }

    /**
     * This function tests whether a solution could be found at least 99 out of 100 times. This function returns false
     * as soon as more than 1 run was not successful. It returns true otherwise.
     * @param jobConfig The configuration for the job to be tested, including the right population size.
     * @param valueToReach The valueToReach, which should be tested for in order to determine of a run was a success or not.
     * @return Whether or not the proble could be solved for the given population size.
     */
    private static boolean tryPopSize(JobConfiguration jobConfig, double valueToReach){
        int fails = 0;
        int runs = 0;
        
        while(runs < RUNS && fails < 2){
            SequentialJobRunner runner = new SequentialJobRunner(jobConfig, false);
            runner.run();
            Solution best = runner.getBestFound();
            if(valueToReach - best.getObjectiveValue() > .0000001){
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
    private static int doBinSearch(int searchId, int start, int end, JobConfiguration jobConfig, double valueToReach){
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
	        if(tryPopSize(jobConfig, valueToReach)){
	            newStart = start;
	            newEnd = middle;
	        } else {
	            newStart = middle;
	            newEnd = end;
	        }
            result = doBinSearch(searchId, newStart, newEnd, jobConfig, valueToReach);
        }
        return result;
    }
    
    public static void printStatistics(int[] popSizes, int[] evaluations){
        System.out.println("MaxCutPopSizeDeterminator finished for " + Problem.MAXCUT + " with l=" + configs[0].GENETIC_CONFIG.NUMBER_OF_PARAMETERS);
        System.out.println("Printing: Pop avg | Pop var | Pop worst | Pop 10% | Pop 50% | Pop 90% | Pop best | "
                + "Eval avg | Eval var | Eval worst | Eval 10% | Eval 50% | Eval 90% | Eval best");
        
        //Pop size statistics
        int popSum = 0;
        for(int value : popSizes){
            popSum += value;
        }
        double popAverage = popSum / (instances * 1.0);
        
        double popVarSum = 0;
        for(int value : popSizes){
            popVarSum += (value - popAverage) * (value - popAverage);
        }
        double popVar = (popVarSum / (instances * 1.0));
        
        //Evaluations statistics
        double evalSum = 0;
        for(double value : evaluations){
            evalSum += value;
        }
        double evalAverage = evalSum / (RUNS * 1.0);
        double evalVarSum = 0;
        for(double value : evaluations){
            evalVarSum += (value - evalAverage) * (value - evalAverage);
        }
        double evalVar = evalVarSum / (RUNS * 1.0);
        
        System.out.println(popAverage + "\t" + popVar + "\t" + popSizes[0] + "\t" + popSizes[1] + "\t" + popSizes[4] + "\t" + popSizes[8] + "\t" + popSizes[9] +
                "\t" + evalAverage + "\t" + evalVar + "\t" + evaluations[0] + "\t" + evaluations[1] + "\t" + evaluations[4] +
                "\t" + evaluations[8] + "\t" + evaluations[9]);
        
    }
}
