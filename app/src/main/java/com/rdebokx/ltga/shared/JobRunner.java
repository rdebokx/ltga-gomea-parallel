package com.rdebokx.ltga.shared;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;

public abstract class JobRunner implements Runnable{

    protected Solution bestSoFar;
    protected final JobState jobState;
    protected final JobConfiguration CONFIG;
    protected Population population;
    protected LearningModel fixedFOS;
    protected boolean printResults;
    protected final Randomizer randomizer;
    
    /**
     * Constructor, constructing a new JobRunner with the given configuration and jobState.
     * @param config The configuration for this jobRunner.
     * @param jobState The jobState, used to keep track of the amount of evaluations and generations.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards.
     * @param randomizer The randomizer object that should be used for this jobRunner.
     */
    protected JobRunner(JobConfiguration config, JobState jobState, boolean printResults, Randomizer randomizer){
        this.CONFIG = config;
        this.jobState = jobState;
        this.printResults = printResults;
        this.randomizer = randomizer;
    }
    
    /**
     * Constructor, constructing a new JobRunner with the given configuration and jobState.
     * @param config The configuration for this jobRunner.
     * @param jobState The jobState, used to keep track of the amount of evaluations and generations.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards.
     * @param fixedFOS the FOS that should be used for as a fixed Learning Model for this JobRunner.
     * @param randomizer The randomizer object that should be used for this jobRunner.
     */
    protected JobRunner(JobConfiguration config, JobState jobState, boolean printResults, LearningModel fixedFOS, Randomizer randomizer){
        this(config, jobState, printResults, randomizer);
        this.fixedFOS = fixedFOS;
    }
    
    /**
     * This method starts the JobRunner.
     */
    @Override
    public abstract void run();
    
    /**
     * This function calculates the fitness variance of the current population and returns true iff this exceeds the given threshold.
     * @param threshold The threshold which the fitness variance should exceed.
     * @return The fitness variance of the current population.
     */
    protected boolean checkFitnessVarianceLargerThan(double threshold){
        double avgObjectiveValue = 0;
        for(int i = 0; i < CONFIG.GENETIC_CONFIG.POPULATION_SIZE; i++){
            avgObjectiveValue += population.getObjectiveValue(i);
        }
        avgObjectiveValue /= (CONFIG.GENETIC_CONFIG.POPULATION_SIZE * 1.0);
        
        double variance = 0;
        for(int i = 0; i < CONFIG.GENETIC_CONFIG.POPULATION_SIZE; i++){
            variance += (population.getObjectiveValue(i) - avgObjectiveValue) * (population.getObjectiveValue(i) - avgObjectiveValue);
        }
        variance /= (CONFIG.GENETIC_CONFIG.POPULATION_SIZE * 1.0);

        return variance > threshold;
    }
    
    /**
     * This function checks whether one of the defined termination conditions was met and returns true if this is the case.
     * This function checks, depending on the configuration of this job, the following termination conditions:
     * - Whether the maximum number of evaluations was reached.
     * - Whether the valueToReach was reached.
     * - Whether the fitness variance was reached.
     * - Whether the maximum no improvement stretch was met.
     * @return True iff any of the above conditions was met.
     */
    protected boolean checkTerminationCondition() {
        //long timeStart = System.currentTimeMillis();
        
        boolean result = false;
        
        if(CONFIG.EXECUTION_CONFIG.MAX_NUMBER_OF_EVALUATIONS >= 0 && jobState.getNumberOfEvaluations() >= CONFIG.EXECUTION_CONFIG.MAX_NUMBER_OF_EVALUATIONS){
            System.out.println("Termination condition met: " + jobState.getNumberOfEvaluations() + " evaluations exceeded " + CONFIG.EXECUTION_CONFIG.MAX_NUMBER_OF_EVALUATIONS);
            result = true;
        }
        if(CONFIG.EXECUTION_CONFIG.USE_VALUE_TO_REACH && 
        		(bestSoFar.getConstraintValue() == 0 && bestSoFar.getObjectiveValue() >= CONFIG.EXECUTION_CONFIG.VALUE_TO_REACH
        		|| CONFIG.PROBLEM_CONFIG.PROBLEM == Problem.NK_LANDSCAPES && 
        			Arrays.equals(((InstancesConfiguration) CONFIG.PROBLEM_CONFIG).OPTIMAL_SOLUTION, bestSoFar.getSolution()))){
            System.out.println("Termination condition met: best value so far (" + bestSoFar.getSolution() + ") matched the optimal solution");
            result = true;
        }
        if(!checkFitnessVarianceLargerThan(CONFIG.EXECUTION_CONFIG.FITNESS_VARIANCE_TOLERANCE)){
            System.out.println("Termination condition met: fitness variance was smaller or equal to " + CONFIG.EXECUTION_CONFIG.FITNESS_VARIANCE_TOLERANCE);
            result = true;
        }
        if(CONFIG.EXECUTION_CONFIG.MAX_NO_IMPROVEMENT_STRETCH > 0 && jobState.getNoImprovementStretch() > CONFIG.EXECUTION_CONFIG.MAX_NO_IMPROVEMENT_STRETCH){
            System.out.println("Termination condition met: NoImprovementStretch of " + jobState.getNoImprovementStretch() + " exceeded the max_no_improvement_stretch");
            result = true;
        }
        if(CONFIG.EXECUTION_CONFIG.TERMINATION_TIME > 0 && System.currentTimeMillis() > CONFIG.EXECUTION_CONFIG.TERMINATION_TIME){
            System.out.println("Termination time exceeded. Stopping after generation " + jobState.getNumberOfGenerations());
            result = true;
        }
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("JobRunner.checkTerminationCondition", timeEnd - timeStart);
        
        return result;
    }
    
    /**
     * @return The best found solution so far.
     */
    public Solution getBestFound(){
        return bestSoFar;
    }
    
    /**
     * @return The jobState of this jobRunner.
     */
    public JobState getJobState(){
        return jobState;
    }
    
    /**
     * This method writes the current population to file for debugging purposes.
     * @param generation The generation number, used for the filename of the file to write to.
     */
    protected void writePopulation(int generation){
        PrintWriter writer;
        try {
            writer = new PrintWriter("../out/generations/generation" + generation + ".csv", "UTF-8");
            boolean[][] population = this.population.getPopulation();
            for(int i = 0; i < CONFIG.GENETIC_CONFIG.POPULATION_SIZE; i++){
                String solutionLine = "";
                for(int j = 0; j < CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS - 1; j++){
                    solutionLine += population[i][j] + ",";
                }
                solutionLine += population[i][CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS - 1];
                solutionLine += "," + this.population.getObjectiveValue(i);
                writer.println(solutionLine);
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This function returns a newly initiated JobRunner of this class, but then with the given configuration.
     * This function is used for running multiple JobRunners, such as for minimal requireddetermining population sizes.
     * @param config The configuration for the new JobRunner.
     * @return The fresh JobRunner, with the given configuration
     */
    public abstract JobRunner getNewRunnerWithConfig(JobConfiguration config);
    
    /**
     * @return The JobConfiguration of this JobRunner.
     */
    public JobConfiguration getConfig(){
        return this.CONFIG;
    }
}
