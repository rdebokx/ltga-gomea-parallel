package com.rdebokx.ltga.shared;

public interface EvaluationFunction {

    /**
     * This function evaluates the given solution agains a specific function.
     * @param solution The solution that should be evaluated.
     * @return The fitness value of the provided solution.
     */
    public double evaluate(boolean[] solution);

}
