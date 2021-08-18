package com.rdebokx.ltga.experiments.optimalFixedFOS.nkLandscapes;

import com.rdebokx.ltga.experiments.optimalFixedFOS.OFFDeterminator;
import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.ProblemEvaluator;

public class BitVarianceDeterminator {

    private static final int PARAMS = 15;
    private static final JobState JOB_STATE = new JobState();
    
    /**
     * Main entry point fo the BitVarianceDeterminator. This script detemines the variance in the sums of all NK subfunctions
     * for all possible combinations of 2 parameters for the N15K5S1M11_4 instance.
     * @param args No arguments needed.
     */
    public static void main(String[] args) {
        OptimalFixedFOSConfiguration config = OFFDeterminator.constructNKOFFConfig(Problem.NK_LANDSCAPES, PARAMS, "../data/nk-s1/", "N15K5S1M11_4.txt", OFFDeterminator.SEEDS_FILE);
        
        boolean[][] solutions = constructSolutions(PARAMS);
        
        for(int i = 0; i < PARAMS; i++){
            for(int j = i+1; j < PARAMS; j++){
                double variance = determineVariance(solutions, i, j, config.EVALUATION_CONFIG.PROBLEM_CONFIG);
                System.out.println(i + "\t" + j + "\t" + variance);
            }
        }

    }

    /**
     * This function constructs a matrix of all possible solutions for the given amount of parameters.
     * @param params2 The amount of parameters for the given problem.
     * @return A matrix with all possible solutions for the given amount of parameters.
     */
    private static boolean[][] constructSolutions(int params2) {
        boolean[][] result = new boolean[(int) Math.pow(2, PARAMS)][PARAMS];
        for(int i = 0; i < result.length; i++){
            result[i] = constructSolution(i);
        }
        System.out.println(result.length + " possible solutions initialized");
        return result;
    }

    /**
     * Construct a solution that corresponds to the bit string of the given integer.
     * @param sol Integer that has to be transformed to a boolean array according to its bit string representation.
     * @return the solution as a boolean array.
     */
    private static boolean[] constructSolution(int sol) {
        boolean[] result = new boolean[PARAMS];
        String bitString = Integer.toBinaryString(sol);
        for(int i = 0; i < bitString.length(); i++){
            result[PARAMS - bitString.length() + i] = bitString.charAt(i) == '1' ? true : false;
        }
        
        return result;
    }
    
    /**
     * This function determines the variance between the sums of the NK subfunctions for all possible combinations
     * of i an j. This variance is an indicator for the linkage between i an j, as a high variance indicates that a 
     * particular combination of i an j as a high contribution to the total fitness of a solution.
     * @param solutions The matrix wit all possible solutions for this problem.
     * @param i The index of the first parameter.
     * @param j The index of the second parameter.
     * @param problemConfig The Problem Configuration containing the NK subfunctions.
     * @return The variance of the sums of the NK subfunctions for all possible combinations of i an j.
     */
    private static double determineVariance(boolean[][] solutions, int i, int j, ProblemConfiguration problemConfig){
        double sum00 = getSum(solutions, i, false, j, false, problemConfig);
        double sum01 = getSum(solutions, i, false, j, true, problemConfig);
        double sum10 = getSum(solutions, i, false, j, false, problemConfig);
        double sum11 = getSum(solutions, i, true, j, true, problemConfig);
        
        double average = (sum00 + sum01 + sum10 + sum11) / 4;
        double variance = (
                (sum00 - average) * (sum00 - average) + 
                (sum01 - average) * (sum01 - average) + 
                (sum10 - average) * (sum10 - average) + 
                (sum11 - average) * (sum11 - average)
                ) / 4;
        return variance;
    }
    
    /**
     * The sum of all NK subfunctions with the given values for the given parameters.
     * @param solutions The total matrix with all possible solutions of which has to be enumerated.
     * @param i The index of the first fixed-value parameter.
     * @param iValue The value of the first fixed-value parameter.
     * @param j The index of the second fixed-value parameter.
     * @param jValue The value of the second fixed-value parameter.
     * @param problemConfig The problemConfig, containing the NK subfunctions.
     * @return The sum of all NK subfunctions for the given fixed value for the given parameters.
     */
    private static double getSum(boolean[][] solutions, int i, boolean iValue, int j, boolean jValue, ProblemConfiguration problemConfig){
        double result = 0;
        for(int s = 0; s < solutions.length; s++){
            boolean[] solution = solutions[s];
            if(solution[i] == iValue && solution[j] == jValue){
                result += ProblemEvaluator.installedProblemEvaluation(problemConfig, solution, JOB_STATE);
            }
        }
        return result;
    }

}
