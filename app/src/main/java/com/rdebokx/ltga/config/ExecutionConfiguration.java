package com.rdebokx.ltga.config;

/**
 * 
 * @author Rdebokx
 *
 */
public class ExecutionConfiguration {

    public static final double DEFAULT_FITNESS_VARIANCE_TOLERANCE = .000001;
    
    public final int THREADS;
    public final int MAX_NO_IMPROVEMENT_STRETCH;
    public final int MAX_NUMBER_OF_EVALUATIONS;
    public final boolean USE_VALUE_TO_REACH;
    public final double VALUE_TO_REACH;
    public final double FITNESS_VARIANCE_TOLERANCE;
    public final long TERMINATION_TIME;
    public final boolean USE_LOCAL_SEARCH;

    /**
     * Constructor for config that terminates the execution on one of the given properties.
     * @param cores The amount of cores available.
     * @param maxNoImprovementStretch The maximum no improvement stretch.
     * @param maxNumberOfEvaluations The maximum allowed number of evaluations.
     * @param useValueToReach Indicator for whether or not the valueToReach should be used in the termination condition.
     * @param valueToReach The value to reach.
     * @param fitnessVarianceTolerance The tolerance of the fitness variance.
     * @param terminationTime The timestamp after which the program has to be stopped. Only used by the dynamic implementations.
     * @param useLocalSearch True iff we want to use local search at the end of the initialization and at the end of every generation.
     */
    public ExecutionConfiguration(int cores, int maxNoImprovementStretch, int maxNumberOfEvaluations, boolean useValueToReach, 
            double valueToReach, double fitnessVarianceTolerance, long terminationTime) {
        THREADS = cores;
        MAX_NO_IMPROVEMENT_STRETCH = maxNoImprovementStretch;
        MAX_NUMBER_OF_EVALUATIONS = maxNumberOfEvaluations;
        USE_VALUE_TO_REACH = useValueToReach;
        VALUE_TO_REACH = valueToReach;
        FITNESS_VARIANCE_TOLERANCE = fitnessVarianceTolerance;
        TERMINATION_TIME = terminationTime;
        USE_LOCAL_SEARCH = false;
    }
    
    /**
     * Constructor for config that terminates the execution on one of the given properties.
     * @param cores The amount of cores available.
     * @param maxNoImprovementStretch The maximum no improvement stretch.
     * @param maxNumberOfEvaluations The maximum allowed number of evaluations.
     * @param useValueToReach Indicator for whether or not the valueToReach should be used in the termination condition.
     * @param valueToReach The value to reach.
     * @param fitnessVarianceTolerance The tolerance of the fitness variance.
     * @param useLocalSearch True iff we want to use local search at the end of the initialization and at the end of every generation.
     */
    public ExecutionConfiguration(int cores, int maxNoImprovementStretch, int maxNumberOfEvaluations, boolean useValueToReach, double valueToReach, double fitnessVarianceTolerance){
        this(cores, maxNoImprovementStretch, maxNumberOfEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance, -1);
    }
    
    @Override
    public String toString(){
        return "Threads: " + THREADS + "\nMaxNoImprovementStretch: " + MAX_NO_IMPROVEMENT_STRETCH + "\nMaxNumberOfEvaluations: " + MAX_NUMBER_OF_EVALUATIONS +
                "\nUseValueToReach: " + USE_VALUE_TO_REACH + "\nValueToReach: " + VALUE_TO_REACH + "\nFitnessVarianceTolerance: " + FITNESS_VARIANCE_TOLERANCE + 
                "\nUseLocalSearch: " + USE_LOCAL_SEARCH;
    }
}
