package com.rdebokx.ltga.shared;

import java.util.Arrays;
import java.util.Iterator;

import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;

public abstract class Population {
    protected final int populationSize;
    protected final int numberOfParameters;
    protected final JobState jobState;
    
    protected final boolean[][] population;
    protected final double[] objectiveValues;
    protected final double[] constraintValues;
    
    /**
     * Constructor, constructing a new empty Population object based on the given population size and amount of parameters.
     * A copy of the job's jobstate is saved to increment the amount of evaluations when needed.
     * @param populationSize The size of the new population.
     * @param numberOfParameters The amount of parameters per solution.
     * @param jobstate The state of the job that initiated the creation of this new population.
     */
    public Population(int populationSize, int numberOfParameters, JobState jobState){
        this.populationSize = populationSize;
        this.numberOfParameters = numberOfParameters;
        this.jobState = jobState;
        
        population = new boolean[populationSize][numberOfParameters];
        objectiveValues = new double[populationSize];
        constraintValues = new double[populationSize];
    }
    
    /**
     * Constructor meant for testing purposes. This constructor sets the attributes of this population according to the arguments provided.
     * @param population The population array for this population. 
     * @param objectiveValues The array of objectiveValues corresponding to the given population array.
     * @param constraintValues The array of constraintValues corresponding to the given population array.
     * @param jobState The JobState for this population.
     */
    public Population(boolean[][] population, double[] objectiveValues, double[] constraintValues, JobState jobState){
        this.populationSize = population.length;
        this.numberOfParameters = population[0].length;
        this.jobState = jobState;
        
        this.population = population;
        this.objectiveValues = objectiveValues;
        this.constraintValues = constraintValues;
    }
    
    /**
     * This function initializes a new population and the objective values by randomly generating n solutions.
     * @param jobConfig The JobConfiguration for this job.
     * @param randomizer The randomizer that should be used when initializing the population.
     */
    public abstract void initialize(JobConfiguration jobConfig, Randomizer randomizer);
    
    /**
     * This method evaluates the current population against the given problem, saving the objectiveValues and 
     * constraintValues for the current population.
     * Note that this method is not used by the core algorithm, as the objective values are calculated when 
     * calculating the offspring.
     * @param problem The problem for which the population has to be evaluated.
     */
    public void evaluate(ProblemConfiguration problemConfig){
        for(int i = 0; i < populationSize; i++){
            double objectiveVal = ProblemEvaluator.installedProblemEvaluation(problemConfig, population[i], jobState);
            objectiveValues[i] = objectiveVal;
            constraintValues[i] = 0;
            jobState.incrementNumberOfEvaluations();
        }
    }
    
    /**
     * This method sets a given solution to the given index in this population. It also sets updates the objectValues and constraintValues array accordingly.
     * This method is for testing purposes only.
     * 
     * @param index The index on which the solution has to be put.
     * @param solution The solution that has to be set at the given index.
     */
    protected void set(int index, Solution solution){
        population[index] = solution.getSolution();
        objectiveValues[index] = solution.getObjectiveValue();
        constraintValues[index] = solution.getConstraintValue();
    }
    
    /**
     * This function returns a solution of this population at the given index.
     * @param index The index of which the solution has to be returned.
     * @return The solution at the given index.
     */
    public Solution get(int index){
        return new Solution(population[index], objectiveValues[index], constraintValues[index]);
    }
    
    /**
     * @return The size of this population.
     */
    public int getPopulationSize(){
        return populationSize;
    }
    
    /**
     * @return The number of parameters of which the solutions consist.
     */
    public int getNumberOfParameters(){
        return numberOfParameters;
    }
    
    /**
     * @return The raw population array.
     */
    public boolean[][] getPopulation(){
        return population;
    }
    
    /**
     * This function returns a solution that is the best so far. This is done by determining the current best solution in this population
     * and comparing it with the given previously best solution. The best of these two solutions is returned.
     * @param bestSoFar The previously best solution.
     * @return The new best solution so far.
     */
    public Solution determineBestSoFar(Solution bestSoFar) {
        //long timeStart = System.currentTimeMillis();
        Solution result = null;
        Solution bestFound = this.getBestSolution();
        
        //Only replace if numberOfGenerations == 0 or the found best solution has a better fitness than the inputted one.
        if(bestSoFar == null || jobState.getNumberOfGenerations() == 0 
                || FitnessComparator.betterFitness(bestFound.getObjectiveValue(), bestFound.getConstraintValue(), bestSoFar.getObjectiveValue(), bestSoFar.getConstraintValue())){
            result = bestFound;
            jobState.setNoImprovementStretch(0);
        } else {
            result = bestSoFar;
            jobState.incrementNoImprovementStretch();
        }
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("Population.determineBestSoFar", timeEnd - timeStart);
        return result;
    }
    
    /**
     * This function iterates over all solutions in this matrix and determines the best solution based on the fitness values.
     * @return The best solution in this population.
     */
    private Solution getBestSolution(){
        int best = 0;
        for(int i = 0; i < populationSize; i++){
            if(FitnessComparator.betterFitness(objectiveValues[i], constraintValues[i], objectiveValues[best], constraintValues[best])){
                best = i;
            }
        }
        return new Solution(population[best], objectiveValues[best], constraintValues[best]);
    }
    
    /**
     * This method copies the values at the positions given by the parameter set from the source array to the destination array.
     * @param source The source array from which values have to be copied.
     * @param dest The destination array to which values have to be copied.
     * @param paramSet The set of parameters indicating for which positions values have to be copied.
     */
    protected void copyForParams(boolean[] source, boolean[] dest, ParameterSet paramSet){
        Iterator<Integer> iterator = paramSet.iterator();
        while(iterator.hasNext()){
            int parameter = iterator.next();
            dest[parameter] = source[parameter];
        }
    }
    
    /**
     * This function returns the objective value for the given solution in the population.
     * @param index The index of the solution for which the objective value has to be returned.
     * @return The objective value for the solution at the given index in this population.
     */
    public double getObjectiveValue(int index){
        return objectiveValues[index];
    }
    
    /**
     * This function returns the constraint value for the given solution in the population.
     * @param index The index of the solution for which the constraint value has to be returned.
     * @return The constraint value for the solution at the given index in this population.
     */
    public double getConstraintValue(int index){
        return constraintValues[index];
    }
    
    @Override
    public String toString(){
        String result = "";
        for(int i = 0; i < populationSize; i++){
            for(int j = 0; j < numberOfParameters; j++){
                result += (population[i][j] ? 1 : 0) + " ";
            }
            result += "\t(" + objectiveValues[i] + ", " + constraintValues[i] + ")\n";
        }
        return result;
    }
    
    /**
     * This function returns a new solution which is a permutation of the given solution, based on the given Linkage Tree.
     * If no permutation could be found that would be an improvement of the given solution, the given BestSoFar solution is returned.
     * 
     * This method was made public for testing purposes.
     * 
     * @param linkageModel The Linkage Tree on which the permutation of the given solution should be based.
     * @param currentSolution The solution that should be mutated to a better solution.
     * @param bestSoFar The best solution found so far. Used for when no permutation could be found that involves and improvement of the given solution.
     * @param problem The problem agains which the new solution has to be evaluated.
     * @param maxNoImprovementStretch The maximum amount of iterations with no improvements allowed.
     * @param randomizer The Randomizer object that should be used.
     * @return A new solution, based on a permutation of the given solution.
     */
    public Solution generateNewSolution(LearningModel linkageModel, boolean[] solution, double objectiveVal, double constraintVal, 
            Solution bestSoFar, JobConfiguration jobConfig, Randomizer randomizer){
        //long timeStart = System.currentTimeMillis();
        Solution newSolution = null;

        //copy current solution
        boolean[] result = Arrays.copyOf(solution, solution.length);
        boolean[] backup = Arrays.copyOf(result, result.length);

        double objectiveValBackup = objectiveVal;
        double constraintValBackup = constraintVal;
        
        boolean solutionHasChanged = false;
        //For all elements in the LT, copy specified parameters if this increases the fitness. Skip last element as this is a loop.
        int[] randomOrder = randomizer.getRandomOrder(linkageModel.size());
        for(int i = 0; i < linkageModel.size(); i++){
            ParameterSet paramSet = linkageModel.get(randomOrder[i]);
            int randomIndex = randomizer.generator.nextInt(populationSize);

            //Convert index to binary representation and set factor variables.
            copyForParams(population[randomIndex], result, paramSet);
            if(!Arrays.equals(result, backup)){
                objectiveVal = ProblemEvaluator.installedProblemEvaluation(jobConfig.PROBLEM_CONFIG, result, jobState);
                constraintVal = 0;
                
                if(FitnessComparator.betterFitness(objectiveVal, constraintVal, objectiveValBackup, constraintValBackup)
                        || FitnessComparator.equalFitness(objectiveVal, constraintVal, objectiveValBackup, constraintValBackup)){
                    
                    copyForParams(result, backup, paramSet);
                    objectiveValBackup = objectiveVal;
                    constraintValBackup = constraintVal;
                    solutionHasChanged = true;
                } else {
                    copyForParams(backup, result, paramSet);
                    objectiveVal = objectiveValBackup;
                    constraintVal = constraintValBackup;
                }
            }
        }
        
        if(!solutionHasChanged || jobState.getNoImprovementStretch() > (1 + Math.log10(populationSize))){
            int i = linkageModel.size() - 1;
            solutionHasChanged = false;
            randomOrder = randomizer.getRandomOrder(linkageModel.size());
            while(i >= 0 && !solutionHasChanged){
                ParameterSet paramSet = linkageModel.get(randomOrder[i]);
                
                //Convert elite solution to binary representation and set factor variables
                copyForParams(bestSoFar.getSolution(), result, paramSet);
                
                //Test if the change is for the better
                if(!Arrays.equals(backup, result)){
                    objectiveVal = ProblemEvaluator.installedProblemEvaluation(jobConfig.PROBLEM_CONFIG, result, jobState);
                    constraintVal = 0;
                    
                    if(FitnessComparator.betterFitness(objectiveVal, constraintVal, objectiveValBackup, constraintValBackup)){
                        copyForParams(result, backup, paramSet);
                        objectiveValBackup = objectiveVal;
                        constraintValBackup = constraintVal;
                        solutionHasChanged = true;
                    } else {
                        copyForParams(backup, result, paramSet);
                        objectiveVal = objectiveValBackup;
                        constraintVal = constraintValBackup;
                    }
                }
                i--;
            }
        }
        
        if(!solutionHasChanged){
            result = Arrays.copyOf(bestSoFar.getSolution(), bestSoFar.getSolution().length);
            newSolution = jobConfig.EXECUTION_CONFIG.USE_LOCAL_SEARCH 
                    ? doHardLocalSearch(result, bestSoFar.getObjectiveValue(), bestSoFar.getConstraintValue(), jobConfig.PROBLEM_CONFIG, randomizer)
                    : new Solution(result, bestSoFar.getObjectiveValue(), bestSoFar.getConstraintValue());
        } else {
            newSolution = jobConfig.EXECUTION_CONFIG.USE_LOCAL_SEARCH
                    ? doHardLocalSearch(backup, objectiveValBackup, constraintValBackup, jobConfig.PROBLEM_CONFIG, randomizer)
                    : new Solution(backup, objectiveValBackup, constraintValBackup);
        }
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("Population.generateNewSolution", timeEnd - timeStart);
        
        return newSolution;
    }
    
    /**
     * This function does a hard local search by bit flipping every parameter in the given solution. Each flip
     * will only be preserved if this is for the better, meaning that the new fitness is at least as good as the
     * fitness before the flipping.
     * @param solution The solution on which a local search has to be performed.
     * @param objectiveVal The objective value of the given solution.
     * @param constraintVal The constraint value of the given solution.
     * @param problemConfig The problem configuration against which the solution has to be evaluated.
     * @param randomizer The randomizer used for generating a random order in which the local search has to be executed.
     * @return The solution that was possibly improved by means of local search.
     */
    public Solution doHardLocalSearch(boolean[] solution, double objectiveVal, double constraintVal, 
            ProblemConfiguration problemConfig, Randomizer randomizer){
        
    	//System.out.println("Doing hard local search");
        boolean[] newSolution = Arrays.copyOf(solution, numberOfParameters);
        double objectiveValBackup = objectiveVal;
        double constraintValBackup = constraintVal;
        int[] order = randomizer.getRandomOrder(numberOfParameters);
        for(int i = 0; i < this.numberOfParameters; i++){
            newSolution[order[i]] = !newSolution[order[i]];
            objectiveVal = ProblemEvaluator.installedProblemEvaluation(problemConfig, newSolution, jobState);
            constraintVal = 0;
            if(FitnessComparator.betterFitness(objectiveVal, constraintVal, objectiveValBackup, constraintValBackup)
                    || FitnessComparator.equalFitness(objectiveVal, constraintVal, objectiveValBackup, constraintValBackup)){
                objectiveValBackup = objectiveVal;
                constraintValBackup = constraintVal;
            } else {
                newSolution[order[i]] = solution[order[i]];
                objectiveVal = objectiveValBackup;
                constraintVal = constraintValBackup;
            }
        }
        
        return new Solution(newSolution, objectiveVal, constraintVal);
    }
}
