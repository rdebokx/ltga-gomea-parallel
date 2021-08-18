package com.rdebokx.ltga.config.problems;

import com.rdebokx.ltga.shared.Problem;

/**
 * 
 * @author Rdebokx
 *
 */
public class ProblemConfiguration {

    public final Problem PROBLEM;

    /**
     * Constructor, constructing a simple ProblemConfiguration object based on the given problem.
     * In this case, this class will only be a wrapper for the given problem, however it also serves as a superclass
     * for more enhanced problem configurations.
     * @param problem The problem for this ProblemConfiguration.
     */
    public ProblemConfiguration(Problem problem) {
        PROBLEM = problem;
    }
    
    @Override
    public String toString(){
        return PROBLEM.name();
    }
}
