package com.rdebokx.ltga.config.problems;

import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.Solution;
import com.rdebokx.ltga.shared.UniformEvaluationFunction;

public class InstancesConfiguration extends ProblemConfiguration {

    public final String FILE_NAME;
    public final int K;
    public final int NUMBER_OF_SUBFUNCTIONS;
    public final boolean[] OPTIMAL_SOLUTION;
    public final double OPTIMAL_VALUE;
    public final UniformEvaluationFunction[] SUB_FUNCTIONS;
    
    /**
     * Constructor, constructing an NKLandscapeConfiguration object containing the configuration for an NK_LANDSCAPE problem.
     * @param k The amount of parameters that were considered by the sub-functions.
     * @param blockTransition The block transition of the sub-functions.
     * @param numberOfSubfunctions The number of sub-functions.
     * @param optimalSolution The optimal solution for this problem.
     * @param optimalValue The objective value of the optimal solution for this problem.
     * @param subFunctions The lookup table that represents the sub-functions for this problem.
     */
    public InstancesConfiguration(Problem problem, String fileName, int k, int numberOfSubfunctions, boolean[] optimalSolution, double optimalValue, UniformEvaluationFunction[] subFunctions){
        super(problem);
        FILE_NAME = fileName;
        K = k;
        NUMBER_OF_SUBFUNCTIONS = numberOfSubfunctions;
        OPTIMAL_SOLUTION = optimalSolution;
        OPTIMAL_VALUE = optimalValue;
        SUB_FUNCTIONS = subFunctions;
    }
    
    @Override
    public String toString(){
        return super.toString() + "\nfileName: " + FILE_NAME + "\nk: " + K + "\nOptimal Solution: " + Solution.getStringRepresentation(OPTIMAL_SOLUTION) +
                "\nOptimal Value: " + OPTIMAL_VALUE + "\nSub-functions size: " + SUB_FUNCTIONS.length;
    }
}
