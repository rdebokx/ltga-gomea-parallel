package com.rdebokx.ltga.config;

import com.rdebokx.ltga.config.problems.ProblemConfiguration;

/**
 * 
 * @author Rdebokx
 *
 */
public class JobConfiguration {

    public final GeneticConfiguration GENETIC_CONFIG;
    public final ExecutionConfiguration EXECUTION_CONFIG;
    public final ProblemConfiguration PROBLEM_CONFIG;

    /**
     * Constructor for the JobConfiguration object, being a wrapper object for a Genetic, Execution and Problem configuration object.
     * @param genConfig The generic configuration for the job.
     * @param execConfig The execution configuration for the job.
     * @param problemConfig The problem configuration for the job.
     */
    public JobConfiguration(GeneticConfiguration genConfig, ExecutionConfiguration execConfig, ProblemConfiguration problemConfig){
        GENETIC_CONFIG = genConfig;
        EXECUTION_CONFIG = execConfig;
        PROBLEM_CONFIG = problemConfig;
    }
    
    /**
     * This function returns a new JobConfiguration object which is a copy of this configuration object, however
     * it has a new GeneticConfiguration that has a population size that was set to the given value.
     * @param popSize The value to which the population size in the new JobConfig object has to be set.
     * @return The new JobConfiguration object.
     */
    public JobConfiguration copyForPopSize(int popSize){
        GeneticConfiguration newGenConfig = new GeneticConfiguration(popSize, 
                GENETIC_CONFIG.TOURNAMENT_SIZE, popSize, GENETIC_CONFIG.NUMBER_OF_PARAMETERS);
        return new JobConfiguration(newGenConfig, EXECUTION_CONFIG, PROBLEM_CONFIG);
    }
    
    @Override
    public String toString(){
        String genString = GENETIC_CONFIG.toString().replace("\n", "\n\t");
        String execString = EXECUTION_CONFIG.toString().replace("\n", "\n\t");
        String problemString = PROBLEM_CONFIG.toString().replace("\n", "\n\t");
        return "Genetic Config:\n\t" + genString + "\nExecution Config:\n\t" + execString + "\nProblem Config:\n\t" + problemString;
    }
}
