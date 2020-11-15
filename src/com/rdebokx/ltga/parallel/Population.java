package com.rdebokx.ltga.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.shared.ProblemEvaluator;
import com.rdebokx.ltga.shared.Randomizer;
import com.rdebokx.ltga.shared.Solution;

public class Population extends com.rdebokx.ltga.shared.Population {
    
    private volatile int currentPointer;
    
    /**
     * Constructor, constructing a new empty Population of possible solutions with the given parameters.
     * @param populationSize The size of the population.
     * @param numberOfParameters The number of parameters for the problem.
     * @param jobState The jobState, used to track the amount of evaluations performed.
     */
    public Population(int populationSize, int numberOfParameters, JobState jobState){
        super(populationSize, numberOfParameters, jobState);
    }
    
    /**
     * Constructor, constructing a new Population with the given parameters.
     * @param population The population as a boolean matrix that should be stored in this Population object.
     * @param objectiveValues The objective values corresponding to the given population.
     * @param constraintValues The constraint values corresponding to the given population.
     * @param jobState The jobState, used to track the amount of evaluations performed.
     */
    public Population(boolean[][] population, double[] objectiveValues, double[] constraintValues, JobState jobState){
        super(population, objectiveValues, constraintValues, jobState);
    }
    
    @Override
    public void initialize(JobConfiguration jobConfig, Randomizer randomizer){
        //long timeStart = System.currentTimeMillis();

        //First fill population with random solutions. This is fast, and Random generators are not to be used in parallel.
        for(int i = 0; i < populationSize; i++){
            for(int j = 0; j < numberOfParameters; j++){
                population[i][j] = randomizer.generator.nextBoolean();
            }
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(jobConfig.EXECUTION_CONFIG.THREADS);
        currentPointer = 0;
        
        for(int t = 0; t < jobConfig.EXECUTION_CONFIG.THREADS; t++){
            executor.submit(() -> {
                int processableIndex = this.getNextPointer();
                while(processableIndex < this.populationSize){
                    double objectiveVal = ProblemEvaluator.installedProblemEvaluation(jobConfig.PROBLEM_CONFIG, population[processableIndex], jobState);
                    
                    if(jobConfig.EXECUTION_CONFIG.USE_LOCAL_SEARCH){
                        Solution localSearchSol = this.doHardLocalSearch(population[processableIndex], objectiveVal, 0, jobConfig.PROBLEM_CONFIG, randomizer);
                        population[processableIndex] = localSearchSol.getSolution();
                        objectiveValues[processableIndex] = localSearchSol.getObjectiveValue();
                        constraintValues[processableIndex] = localSearchSol.getConstraintValue();
                    } else {
                        objectiveValues[processableIndex] = objectiveVal;
                        constraintValues[processableIndex] = 0;
                    }
                    
                    processableIndex = this.getNextPointer();
                }
            });
        };
        executor.shutdown();
        try{
            executor.awaitTermination(365, TimeUnit.DAYS);
        } catch(InterruptedException e){
            e.printStackTrace();
        } 
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("Population.initialize", timeEnd - timeStart);
    }
    
    /**
     * This function returns a new Population which is an offspring of this population.
     * @param bestSoFar The best solution found so far.
     * @param genConfig The Genetic Configuration.
     * @param linkageModel The Linkage Tree that should be used for creating the offspring.
     * @param randomizer The Randomizer object that should be used.
     * @return An offspring of this Population.
     */
    public Population makeOffspring(Solution bestSoFar, JobConfiguration jobConfig, LearningModel linkageModel, Randomizer randomizer) {
        //long timeStart = System.currentTimeMillis();
        
        Population offspring = generateAndEvaluateNewSolutionsToFillOffspring(linkageModel, bestSoFar, jobConfig, randomizer);
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("Population.makeOffspring", timeEnd - timeStart);
        return offspring;
    }
    
    /**
     * This function creates new offspring of this population by altering variables based on the given Linkage Tree.
     * It also evaluates this offspring against the given problem.
     * @param linkageModel The Linkage Tree which is used to decide what variables should be altered.
     * @param bestSoFar The best solution found so far.
     * @param problem The problem for which the solutions have to be evaluated.
     * @param maxNoImprovementStretch The maximum amount of iterations that are allowed without any improvement.
     * @param randomizer The Randomizer object that should be used.
     * @return The offspring from this population.
     */
    public Population generateAndEvaluateNewSolutionsToFillOffspring(LearningModel linkageModel, Solution bestSoFar, JobConfiguration config, Randomizer randomizer){
        long timeStart = System.currentTimeMillis();
        final Population offspring = new Population(populationSize, numberOfParameters, jobState);
        
        ExecutorService executor = Executors.newFixedThreadPool(config.EXECUTION_CONFIG.THREADS);
        currentPointer = 0;
        
        for(int t = 0; t < config.EXECUTION_CONFIG.THREADS; t++){
            executor.submit(() -> {
                int processableIndex = this.getNextPointer();
                while(processableIndex < this.populationSize){
                    Solution newSolution = generateNewSolution(linkageModel, population[processableIndex], 
                        objectiveValues[processableIndex], constraintValues[processableIndex], bestSoFar, config, randomizer);
                    offspring.set(processableIndex, newSolution);
                    processableIndex = this.getNextPointer();
                }
            });
        };
        executor.shutdown();
        try{
            executor.awaitTermination(365, TimeUnit.DAYS);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        
        long timeEnd = System.currentTimeMillis();
        Profiler.recordExecution("Population.generateAndEvaluateNewSolutionsToFillOffspring", timeEnd - timeStart);
        return offspring;
    }
    
    private synchronized int getNextPointer(){
        int result = currentPointer;
        currentPointer++;
        return result;
    }
}
