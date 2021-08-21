package com.rdebokx.ltga.sequential;


import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.shared.ProblemEvaluator;
import com.rdebokx.ltga.shared.Randomizer;
import com.rdebokx.ltga.shared.Solution;

public class Population extends com.rdebokx.ltga.shared.Population {
    
    /**
     * Constructor, constructing an empty population with the given parameters.
     * @param populationSize The size of the population.
     * @param numberOfParameters The number of parameters of the solutions to be stored.
     * @param jobState The jobState, used to keep track of the amount of evaluations performed.
     */
    public Population(int populationSize, int numberOfParameters, JobState jobState){
        super(populationSize, numberOfParameters, jobState);
    }
    
    /**
     * Constructor, constructing a new Population based on the given parameters.
     * @param population The population as a boolean array that should be stored inside this Population object.
     * @param objectiveValues The objective values that correspond to the given population.
     * @param constraintValues The constraint values that corresond to the given population.
     * @param jobState The jobState, used to keep track of the amount of evaluations performed.
     */
    public Population(boolean[][] population, double[] objectiveValues, double[] constraintValues, JobState jobState){
        super(population, objectiveValues, constraintValues, jobState);
    }
    
    @Override
    public void initialize(JobConfiguration jobConfig, Randomizer randomizer){
        //long timeStart = System.currentTimeMillis();
        
        for(int i = 0; i < populationSize; i++){
            for(int j = 0; j < numberOfParameters; j++){
                population[i][j] = randomizer.generator.nextBoolean();
            }
            double objectiveVal = ProblemEvaluator.installedProblemEvaluation(jobConfig.PROBLEM_CONFIG, population[i], jobState);
            
            if(jobConfig.EXECUTION_CONFIG.USE_LOCAL_SEARCH){
                Solution localSearchSol = this.doHardLocalSearch(population[i], objectiveVal, 0, jobConfig.PROBLEM_CONFIG, randomizer);
                population[i] = localSearchSol.getSolution();
                objectiveValues[i] = localSearchSol.getObjectiveValue();
                constraintValues[i] = localSearchSol.getConstraintValue();
            } else {
                objectiveValues[i] = objectiveVal;
                constraintValues[i] = 0;
            }
        }
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("Population.initialize", timeEnd - timeStart);
    }
    
    /**
     * This function returns a new Population which is an offspring of this population.
     * @param bestSoFar The best solution found so far.
     * @param genConfig The Genetic Configuration.
     * @param lm The Learning Model that should be used for creating the offspring.
     * @param randomizer The Randomizer object that should be used.
     * @return An offspring of this Population.
     */
    public Population makeOffspring(Solution bestSoFar, JobConfiguration jobConfig, LearningModel lm, Randomizer randomizer) {
        //long timeStart = System.currentTimeMillis();
        
        Population offspring = generateAndEvaluateNewSolutionsToFillOffspring(lm, bestSoFar, jobConfig, randomizer);
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("Population.makeOffspring", timeEnd - timeStart);
        return offspring;
    }

    /**
     * This function creates new offspring of this population by altering variables based on the given Linkage Tree.
     * It also evaluates this offspring against the given problem.
     * @param lm The Learning Model which is used to decide what variables should be altered.
     * @param bestSoFar The best solution found so far.
     * @param problem The problem for which the solutions have to be evaluated.
     * @param maxNoImprovementStretch The maximum amount of iterations that are allowed without any improvement.
     * @param randomizer The Randomizer object that should be used.
     * @return The offspring from this population.
     */
    public Population generateAndEvaluateNewSolutionsToFillOffspring(LearningModel lm, Solution bestSoFar, JobConfiguration jobConfig, Randomizer randomizer){
        long timeStart = System.currentTimeMillis();
        
        Population offspring = new Population(populationSize, numberOfParameters, jobState);
        
        for(int i = 0; i < populationSize; i++){
            Solution newSolution = generateNewSolution(lm, population[i], objectiveValues[i], constraintValues[i], bestSoFar, jobConfig, randomizer);
            offspring.set(i, newSolution);
        }
        
        long timeEnd = System.currentTimeMillis();
        Profiler.recordExecution("Population.generateAndEvaluateNewSolutionsToFillOffspring", timeEnd - timeStart);
        return offspring;
    }
}
