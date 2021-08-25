package com.rdebokx.ltga.parallel;

import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.shared.JobRunner;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.shared.Randomizer;

/**
 * 
 * @author Rdebokx
 *
 */
public class ParallelJobRunner extends JobRunner {

    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     * @param printResuls Boolean indicating whether or not the resuls should be printed after finishing this JobRunner.
     * @param randomizer The Randomizer object that should be used.
     */
    public ParallelJobRunner(JobConfiguration config, boolean printResults) {
        super(config, new JobState(), printResults, new Randomizer());
    }
    
    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards or not.
     * @param fixedFOS the FOS that should be used for as a fixed Learning Model for this JobRunner
     */
    public ParallelJobRunner(JobConfiguration config, boolean printResults, LearningModel fixedFOS) {
        super(config, new JobState(), printResults, fixedFOS, new Randomizer());
    }
    
    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards or not.
     * @param fixedFOS the FOS that should be used for as a fixed Learning Model for this JobRunner
     * @param fixedSeed The fixed seed that should be used by the Randomizer.
     */
    public ParallelJobRunner(JobConfiguration config, boolean printResults, LearningModel fixedFOS, long fixedSeed) {
        super(config, new JobState(), printResults, fixedFOS, new Randomizer(fixedSeed));
    }
    
    /**
     * Constructor, constructing a JobRunner with the given JobConfiguration and the given Population as the first 
     * generation. Note that this is only used for debugging purposes.
     * @param config The JobConfiguration object for the job to be run.
     * @param firstGeneration The first generation for this job.
     */
    public ParallelJobRunner(JobConfiguration config, boolean[][] firstGeneration, boolean printResults) {
        this(config, printResults);
        this.population = new Population(firstGeneration, new double[config.GENETIC_CONFIG.POPULATION_SIZE], new double[config.GENETIC_CONFIG.POPULATION_SIZE], this.jobState);
        this.population.evaluate(config.PROBLEM_CONFIG);
    }
    
    /**
     * Starts the job of this jobRunner.
     */
    @Override
    public void run() {
        if(printResults){
            Profiler.setProgramStart();
        }
        
        if(population == null){
            population = new Population(CONFIG.GENETIC_CONFIG.POPULATION_SIZE, CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS, jobState);
            population.initialize(CONFIG, this.randomizer);
        }
        
        bestSoFar = population.determineBestSoFar(null);
        
        while (!checkTerminationCondition()) {
            if(printResults){
                System.out.println("Running ParallelRunner generation " + jobState.getNumberOfGenerations());
            }
            
            //Create offspring. New objective and constraint values are already saved in inputted arrays.
            LearningModel linkageModel = fixedFOS == null ? new LinkageTree((Population) population, CONFIG, this.randomizer, CONFIG.EXECUTION_CONFIG) : fixedFOS; 
            
            population = ((Population) population).makeOffspring(bestSoFar, CONFIG, linkageModel, this.randomizer);

            jobState.incrementNumberOfGenerations();

            bestSoFar = population.determineBestSoFar(bestSoFar);
        }

        
        //Print results
        if(printResults){
            Profiler.setProgramEnd();
            System.out.println("Jobrunner finished. " + jobState.getNumberOfGenerations() + " generations needed." );
            //Profiler.printResults();
            System.out.println("Best found solution: ");
            System.out.println(bestSoFar);
        }
    }

	@Override
	public JobRunner getNewRunnerWithConfig(JobConfiguration config) {
		return new ParallelJobRunner(config, false);
	}
}
