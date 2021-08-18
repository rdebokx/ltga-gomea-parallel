package com.rdebokx.ltga.shared;

import java.util.ArrayList;
import java.util.Arrays;

import com.rdebokx.ltga.sequential.DynamicJobRunner;
import com.rdebokx.ltga.sequential.SequentialJobRunner;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;
import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;

public class ProblemEvaluator {

	public static final int MAX_POPSIZE = 100000;
    /**
     * Computes the value of the single objective
     * and the sum of all constraint violations
     * function.
     */
    public static double installedProblemEvaluation(ProblemConfiguration problemConfig, boolean[] solution, JobState jobState){
        //long timeStart = System.currentTimeMillis();
        
        double result = 0;
        
        try{
        	if(problemConfig instanceof InstancesConfiguration){
        		result = UniformProblemEvaluation((InstancesConfiguration) problemConfig, solution);
        	} else {
	            switch(problemConfig.PROBLEM){
	            case ONEMAX:
	                result = onemaxFunctionProblemEvaluation(solution);
	                break;
	            case DECEPTIVE_TRAP_4_TIGHT_ENCODING:
	                result = deceptiveTrapKFunctionProblemEvaluation(solution, 4, true);
	                break;
	            case DECEPTIVE_TRAP_4_LOOSE_ENCODING:
	                result = deceptiveTrapKFunctionProblemEvaluation(solution, 4, false);
	                break;
	            case DECEPTIVE_TRAP_5_TIGHT_ENCODING:
	                result = deceptiveTrapKFunctionProblemEvaluation(solution, 5, true);
	                break;
	            case DECEPTIVE_TRAP_5_LOOSE_ENCODING:
	                result = deceptiveTrapKFunctionProblemEvaluation(solution, 5, false);
	                break;
	            case MAXCUT:
            		result = ((MaxCutConfiguration) problemConfig).WEIGHTS_FUNCTION.evaluate(solution);
	            	break;
                case OPTIMAL_FIXED_FOS:
                    result = OptimalFixedFOSFunctionProblemEvaluation((OptimalFixedFOSConfiguration) problemConfig, solution);
                    break;
                default:
                    break;
	            }
        	}
        } catch(ProblemEvaluationException e){
            e.printStackTrace();
        }
        
        jobState.incrementNumberOfEvaluations();
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("ProblemEvaluator.installedProblemEvaluation", timeEnd - timeStart);
        
        return result;
    }
    
    /**
     * This function calculates the score of the given solution, evaluated against the ONEMAX problem.
     * This comes down to the sum of the given array, where true is mapped to 1 and false to 0.
     * @param solution The solution which has to be evaluated.
     * @return The score of the given solution against the ONEMAX problem.
     */
    private static double onemaxFunctionProblemEvaluation(boolean[] solution){
        double result = 0;
        for(int i = 0; i < solution.length; i++){
            result += solution[i] ? 1 : 0;
        }
        return result;
    }
    
    /**
     * This function is a wrapper function for evaluating a given problem against the Deceptive Trap problem.
     * @param solution The solution which has to be evaluated against the deceptive k-trap function
     * @param k The value of k for the k-trap problem.
     * @param tightEncoding True iff the solutions are tightly encoded. False if they are loosly encoded.
     * @return The score of the given solution for the k-trap function.
     * @throws ProblemEvaluationException
     */
    private static double deceptiveTrapKFunctionProblemEvaluation(boolean[] solution, int k, boolean tightEncoding) throws ProblemEvaluationException{
        double result = 0;
        if(solution.length % k != 0){
            throw new ProblemEvaluationException("Error in evaluating deceptive trap k: Number of parameters is not a multiple of " + k);
        }
        
        int m = solution.length / k;
        for(int i = 0; i < m; i++){
            int u = tightEncoding ? getTightEncodingKTrapScore(solution, k, i) : getLooseEncondingKTrapScore(solution, k, m, i);
            if(u == k){
                result += 1;
            } else {
                result += (k-1-u) / (k * 1.0);
            }
        }
        return result;
    }
    
    /**
     * This function counts the amount of 1's in the given solution for tightly encoded solutions.
     * @param solution The solution for which the amount of 1's have to be counted at the i'th block.
     * @param k The value of k in the k-trap problem.
     * @param i The block in the solution for which the amount of 1's have to be counted.
     * @return The amount of 1's encountered.
     */
    private static int getTightEncodingKTrapScore(boolean[] solution, int k, int i){
        int u = 0;
        for(int j = 0; j < k; j++){
            u+= solution[i*k+j] ? 1 : 0;
        }
        return u;
    }
    
    /**
     * This function counts the amount 1's in the given solution for loosely encoded solutions.
     * @param solution The loosely encoded solution for which 1's have to be counted.
     * @param k The value of k in the k-trap problem.
     * @param m The size of the blocks that have to be evaluated.
     * @param i The position in the blocks of the solution for which the 1's have to be counted.
     * @return
     */
    private static int getLooseEncondingKTrapScore(boolean[] solution, int k, int m, int i){
        int u = 0;
        for(int j = 0; j < k; j++){
            u+= solution[i+m*j] ? 1 : 0;
        }
        return u;
    }
    
    /**
     * This function evaluates the given solution against the given NK-Landscapes problem. This is done by summing
     * the scores of all sub-functions.
     * @param problemConfig The ProblemConfiguration file containing the sub-functions.
     * @param solution The solution that should be evaluated against the known sub-functions.
     * @return The sum of the sub-functions.
     */
    private static double UniformProblemEvaluation(InstancesConfiguration problemConfig, boolean[] solution) {
        double total = 0;
        
        for(EvaluationFunction function : problemConfig.SUB_FUNCTIONS){
        	total += function.evaluate(solution);
        }
        
        return total;
    }
    
    /**
     * This function calculates the score of a given solution that represents a set of ParameterSets which should be 
     * used as Learning Model when executing an LTGA with the problem configuration contained by the given problemConfig parameter.
     * The score of the solution is defined as the average amount of evaluations when executing the LTGA with the given Learning Model
     * for the minimum required population size. In order to determine this, the minimal required population size first has to be
     * determined
     * @param problemConfig The OFF configuration object containing information for executing the LTGA with the given Learning Model.
     * @param solution Bitstring representing which of the parameterSets in the OFFConfig should be used when executing the LTGA.
     * @return The average amount of evaluations that were needed when running the LTGA with the minimal required population size.
     */
    private static double OptimalFixedFOSFunctionProblemEvaluation(OptimalFixedFOSConfiguration problemConfig, boolean[] solution){
        //Select ParameterSets that should be used as a learning model.
        ArrayList<ParameterSet> learningModelSets = new ArrayList<ParameterSet>();
        for(int i = 0; i < solution.length; i++){
            if(solution[i]){
                learningModelSets.add(problemConfig.LINKAGE_SETS.get(i));
            }
        }
        LearningModel learningModel = new LearningModel(learningModelSets);
        return OptimalFixedFOSFunctionProblemEvaluation(problemConfig, learningModel);
    }
    
    /**
     * This function calculates the score of a given set of ParameterSets which should be 
     * used as Learning Model when executing an LTGA with the problem configuration contained by the given problemConfig parameter.
     * The score of the solution is defined as the average amount of evaluations when executing the LTGA with the given Learning Model
     * for the minimum required population size when running a DynamicRunner that stops as soon as the valueToReach was hit first.
     * Note that if the given learning model requires more evaluations than allowed denoted as x, then the amount of evaluations is
     * calculated as x + x*(distanceToOptimum)^2.
     * @param problemConfig The OFF configuration object containing information for executing the LTGA with the given Learning Model.
     * @param learningModel The learning model that should be used as a fixed FOS by the LTGA.
     * @return The average amount of evaluations that were needed when running the LTGA with the minimal required population size.
     */
    public static double OptimalFixedFOSFunctionProblemEvaluation(OptimalFixedFOSConfiguration problemConfig, LearningModel learningModel){
        int evaluationSum = 0;
        
        int i = 0;
        while(i < OptimalFixedFOSConfiguration.RUNS){
            DynamicJobRunner dynamicRunner;
            if(problemConfig.SEEDS != null){
                dynamicRunner = new DynamicJobRunner(problemConfig.EVALUATION_CONFIG, false, learningModel, problemConfig.SEEDS[i/100][i%100]);
            } else {
                dynamicRunner = new DynamicJobRunner(problemConfig.EVALUATION_CONFIG, false, learningModel);
            }
            dynamicRunner.run();
            
            double distanceToOptimum = problemConfig.EVALUATION_CONFIG.EXECUTION_CONFIG.VALUE_TO_REACH - dynamicRunner.getBestFound().getObjectiveValue();
            int numberOfEvaluations = dynamicRunner.getJobState().getNumberOfEvaluations();
            if((problemConfig.EVALUATION_CONFIG.EXECUTION_CONFIG.MAX_NUMBER_OF_EVALUATIONS < 0 || dynamicRunner.getJobState().getNumberOfEvaluations() < problemConfig.EVALUATION_CONFIG.EXECUTION_CONFIG.MAX_NUMBER_OF_EVALUATIONS) 
                    && distanceToOptimum < .0001){
                evaluationSum += numberOfEvaluations;
            } else {
                //System.out.println("--- Alternate fitness calculation! ObjectiveValue: " + dynamicRunner.getBestFound().getObjectiveValue() + " Fitness: " + (numberOfEvaluations + numberOfEvaluations * distanceToOptimum * distanceToOptimum));
                evaluationSum += numberOfEvaluations + numberOfEvaluations * distanceToOptimum * distanceToOptimum;
            }
            i++;
        }
        return -1 * evaluationSum / (1.0 * OptimalFixedFOSConfiguration.RUNS);
    }
    
    /**
     * This function determines the minimum required population size when executing the LTGA with the given jobConfiguration
     * and the given fixed Learning Model.
     * In order for a population size to be sufficient, at least 99 out of 100 runs must return the optimal solution.
     * In order to be able to compare results, fixed seeds are used for these 100 runs, which are to be supplied.
     * @param jobConfig The JobConfiguration object with which the LTGA has to be run.
     * @param learningModel The fixed Learning Model that has to be used by the LTGA.
     * @param seeds The fixed seeds for the 100 runs.
     * @return The minimum required population size.
     */
    private static int determinePopSize(JobConfiguration jobConfig, LearningModel learningModel, long[] seeds){
        boolean popSizeFound = false;
        popSizeFound = tryPopSize(jobConfig, learningModel, seeds);
        int newPopSize = jobConfig.GENETIC_CONFIG.POPULATION_SIZE; 
        
        while(!popSizeFound && newPopSize < MAX_POPSIZE){
        	jobConfig = copyJobConfigForPopSize(jobConfig, newPopSize);
            popSizeFound = tryPopSize(jobConfig, learningModel, seeds);
            newPopSize = jobConfig.GENETIC_CONFIG.POPULATION_SIZE * 2;
        }
        
        int result = newPopSize;
        if(newPopSize < MAX_POPSIZE){
        	//do bisection search
        	result = doBinSearch(jobConfig.GENETIC_CONFIG.POPULATION_SIZE / 2, jobConfig.GENETIC_CONFIG.POPULATION_SIZE, jobConfig, learningModel, seeds);
        }
        return result; 
    }
    
    /**
     * This function returns a copy of the given jobConfiguration object with the given population size.
     * @param jobConfig The jobConfiguration object for which all properties have to be copied except for the population size.
     * @param newPopSize The population size that should be used for the new copy.
     * @return A copy of the given jobConfiguration object with the given population size.
     */
    private static JobConfiguration copyJobConfigForPopSize(JobConfiguration jobConfig, int newPopSize){
    	JobConfiguration result = null;
    	if(jobConfig != null){
    		result = jobConfig.copyForPopSize(newPopSize);
    	}
    	return result;
    }
    
    /**
     * This function check if at least 99 out of 100 runs with the LTGA with the given JobConfiguration and Learning Model are successfull.
     * A run is considered successful if it can find the optimal value.
     * For these runs, fixed seeds are used to simulate the randomness, while at the same time being able to compare results.
     * @param jobConfig The Job Configuration object with which the LTGA should be executed.
     * @param learningModel The Learning Model for which the LTGA should be executed.
     * @param seeds 100 seeds that are to be used for the 100 runs.
     * @return True if at least 99 out of 100 runs are successful with the given Job Configuration and Learning Model.
     */
    private static boolean tryPopSize(JobConfiguration jobConfig, LearningModel learningModel, long[] seeds){
        int fails = 0;
        int runs = 0;
        int maxRuns = OptimalFixedFOSConfiguration.RUNS;
        
        while(runs < maxRuns && fails < 2){
            SequentialJobRunner runner = new SequentialJobRunner(jobConfig, false, learningModel, seeds[runs]);
            runner.run();
            Solution best = runner.getBestFound();
            
            if(jobConfig.PROBLEM_CONFIG.PROBLEM == Problem.NK_LANDSCAPES && !Arrays.equals(best.getSolution(), ((InstancesConfiguration) jobConfig.PROBLEM_CONFIG).OPTIMAL_SOLUTION)
                    || jobConfig.PROBLEM_CONFIG.PROBLEM != Problem.NK_LANDSCAPES && Math.abs(best.getObjectiveValue() - jobConfig.EXECUTION_CONFIG.VALUE_TO_REACH) > .0000001){
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
     * @param learningModel The learningModel that should be used for executing the JobRunners.
     * @param seeds 100 Seeds that are to be used for the LTGA runs.
     * @return The determined minimum required population size within the given window.
     */
    private static int doBinSearch(int start, int end, JobConfiguration jobConfig, LearningModel learningModel, long[] seeds){
        int result = -1;
        if(end - start <= 1){
            result = end;
        } else {
            int middle = Math.floorDiv(start + end, 2);
            jobConfig = copyJobConfigForPopSize(jobConfig, middle);
            
            //Do recursive bin search
            int newStart;
            int newEnd;
            if(tryPopSize(jobConfig, learningModel, seeds)){
                newStart = start;
                newEnd = middle;
            } else {
                newStart = middle;
                newEnd = end;
            }
            result = doBinSearch(newStart, newEnd, jobConfig, learningModel, seeds);
        }
        return result;
    }
    
    /**
     * This function returns the average amount of evaluations needed over 10 runs when the LTGA is executed with the given parameters.
     * @param evalConfig The JobConfiguration that should be used when running the LTGA. The population size has to be set to the minimum required population size.
     * @param learningModel The fixed Learning Model that has to be used by the LTGA.
     * @param seeds The matrix with seeds that were used for determining the minimum required population size.
     * @return The average amount of evaluations needed.
     */
    private static double getEvaluationsAverage(JobConfiguration evalConfig, LearningModel learningModel, long[][] seeds){
        double sum = 0;
        for(int i = 0; i < 10; i++){
            SequentialJobRunner runner = new SequentialJobRunner(evalConfig, false, learningModel, seeds[i][0]);
            runner.run();
            sum += runner.getJobState().getNumberOfEvaluations();
        }
        if(sum == 0){
            System.out.println("Evaluations Sum: " + sum + " for learning model " + learningModel + " and evalConfig: " + evalConfig);
        }
        return sum / 10.0;
    }
}
