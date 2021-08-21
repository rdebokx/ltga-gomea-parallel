package com.rdebokx.ltga.config.problems;

import com.rdebokx.ltga.shared.MaxCutEvaluationFunction;
import com.rdebokx.ltga.shared.Problem;

public class MaxCutConfiguration extends ProblemConfiguration {

    public final String FILE_NAME;
    public final double BEST_KNOWN_VALUE;
    public final double AVERAGE_RANDOM_VALUE;
    public final MaxCutEvaluationFunction WEIGHTS_FUNCTION;
    
    /**
     * Constructor, constructing an NKLandscapeConfiguration object containing the configuration for an NK_LANDSCAPE problem.
     * @param k The amount of parameters that were considered by the sub-functions.
     * @param blockTransition The block transition of the sub-functions.
     * @param numberOfSubfunctions The number of sub-functions.
     * @param optimalSolution The optimal solution for this problem.
     * @param optimalValue The objective value of the optimal solution for this problem.
     * @param subFunctions The lookup table that represents the sub-functions for this problem.
     */
    public MaxCutConfiguration(String fileName, double bestKnownValue, double averageRandomValue, MaxCutEvaluationFunction weightsFunction){
        super(Problem.MAXCUT);
        FILE_NAME = fileName;
        BEST_KNOWN_VALUE = bestKnownValue;
        AVERAGE_RANDOM_VALUE = averageRandomValue;
        WEIGHTS_FUNCTION = weightsFunction;
    }
    
    @Override
    public String toString(){
        return super.toString() + "\nfileName: " + FILE_NAME + "\nBKV: " + BEST_KNOWN_VALUE + "\nART: " + AVERAGE_RANDOM_VALUE + "\nWEIGHTS: " + WEIGHTS_FUNCTION;
    }
}
