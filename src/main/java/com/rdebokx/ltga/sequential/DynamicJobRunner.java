package com.rdebokx.ltga.sequential;

import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.shared.FitnessComparator;
import com.rdebokx.ltga.shared.JobRunner;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.shared.Randomizer;
import com.rdebokx.ltga.shared.Solution;

/**
 * 
 * @author Rdebokx
 *
 */
public class DynamicJobRunner extends JobRunner {
    
    private JobConfiguration currentConfig;

    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     */
    public DynamicJobRunner(JobConfiguration config, boolean printResults) {
        super(config, new JobState(), printResults, new Randomizer());
        this.currentConfig = this.CONFIG;
    }
    
    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     */
    public DynamicJobRunner(JobConfiguration config, boolean printResults, long seed) {
        super(config, new JobState(), printResults, new Randomizer(seed));
        this.currentConfig = this.CONFIG;
    }
    
    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     */
    public DynamicJobRunner(JobConfiguration config, boolean printResults, LearningModel learningModel, long seed) {
        super(config, new JobState(), printResults, learningModel, new Randomizer(seed));
        this.currentConfig = this.CONFIG;
    }
    
    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     */
    public DynamicJobRunner(JobConfiguration config, boolean printResults, LearningModel learningModel) {
        super(config, new JobState(), printResults, learningModel, new Randomizer());
        this.currentConfig = this.CONFIG;
    }
    
    /**
     * Starts the job of this jobRunner.
     */
    @Override
    public void run() {
        long programStart = System.currentTimeMillis();
        if(printResults){
            System.out.println("Jobrunner started");
        }
        
        boolean stopRunner = false;
        while(!stopRunner){
            //Initialize SequentialJobRunner
            SequentialJobRunner runner = new SequentialJobRunner(currentConfig, false, this.fixedFOS, this.randomizer.generator.nextLong());
            runner.run();
            if(printResults){
                System.out.println("JobRunner finished for n=" + currentConfig.GENETIC_CONFIG.POPULATION_SIZE + " objectiveValue: " + runner.getBestFound().getObjectiveValue());
            }
            //Store result if founda
            if(runner.getBestFound() != null){
                updateBestFound(runner.getBestFound());
            } else {
                stopRunner = true;
            }
        
            this.jobState.incrementNumberOfEvaluations(runner.getJobState().getNumberOfEvaluations());
            stopRunner = this.checkTerminationCondition() || runner.getBestFound() == null;
            currentConfig = currentConfig.copyForPopSize(currentConfig.GENETIC_CONFIG.POPULATION_SIZE * 2);
        }
        
        //Print results
        if(printResults){
            System.out.println("DynamicLTGA finished. Executed in " + ((System.currentTimeMillis() - programStart) / (1000.0)) + " seconds.");
            System.out.println("Objective value of best solution: " + bestSoFar.getObjectiveValue());
            //System.out.println(bestSoFar);
        }
    }
    
    /**
     * This function updates the BestFound solution so far iff the given solution is better than the currently known best solution.
     * @param runnerBestFound The best solution found.
     */
    private void updateBestFound(Solution runnerBestFound){
        if(bestSoFar == null || FitnessComparator.betterFitness(runnerBestFound.getObjectiveValue(), runnerBestFound.getConstraintValue(), this.bestSoFar.getObjectiveValue(), this.bestSoFar.getConstraintValue())){
            this.bestSoFar = runnerBestFound;
        }
    }
    
    @Override 
    protected boolean checkFitnessVarianceLargerThan(double threshold){
        return true;
    }
    
	@Override
	public JobRunner getNewRunnerWithConfig(JobConfiguration config) {
		return new DynamicJobRunner(config, false);
	}
	
	@Override
	public JobConfiguration getConfig(){
	    return this.currentConfig;
	}
}
