package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.util.Comparator;

import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;

/**
 * ParamterSet Pair Comparator
 */
public class SplitComparator implements Comparator<Split> {

    private OptimalFixedFOSConfiguration config;
    
    /**
     * Constructor, constructing a new SplitComparator that can compare two splits for the given configuration.
     * Split A is considered smaller than Split B iff the score of the children of A, when put together in a FOS,
     * is smaller than that of the children of B.
     * @param config The OptimalFixedFOSConfiguration against which children of splits have to be compared.
     */
    public SplitComparator(OptimalFixedFOSConfiguration config){
        this.config = config;
    }
    
    @Override
    public int compare(Split a, Split b){
        double scoreA = ROFFGenerator.getScore(config, a.getChildren());
        double scoreB = ROFFGenerator.getScore(config, b.getChildren());
        
        return scoreA < scoreB ? -1 : scoreA == scoreB ? 0 : 1;
    }
    
}
