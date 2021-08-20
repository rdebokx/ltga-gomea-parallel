package com.rdebokx.ltga.config.problems;

import java.util.List;

import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Problem;

public class OptimalFixedFOSConfiguration extends ProblemConfiguration {

    public final List<ParameterSet> LINKAGE_SETS;
    public final JobConfiguration EVALUATION_CONFIG;
    public static final int RUNS = 1000;
    public final long[][] SEEDS;
    
    /**
     * Constructor, constructing a new OptimalFixedFOS Configuration object with the given list of Linkage Sets.
     * @param linkageSets A list of Linkage Sets of which the most effective combination should be determined.
     * @param evaluationConfig A JobConfiguration object used for evaluating scores of a solution by running an LTGA with this configuration.
     * @param seeds for running the simulations, if applicable.
     */
    public OptimalFixedFOSConfiguration(List<ParameterSet> linkageSets, JobConfiguration evaluationConfig, long[][] seeds) {
        super(Problem.OPTIMAL_FIXED_FOS);
        this.LINKAGE_SETS = linkageSets;
        this.EVALUATION_CONFIG = evaluationConfig;
        this.SEEDS = seeds;
    }
    
    @Override
    public String toString(){
    	return Problem.OPTIMAL_FIXED_FOS + "\n\tLinkageSets: " + this.LINKAGE_SETS + "\n\tEvalutionConfig: {\n" + this.EVALUATION_CONFIG + "\n}";
    }
}
