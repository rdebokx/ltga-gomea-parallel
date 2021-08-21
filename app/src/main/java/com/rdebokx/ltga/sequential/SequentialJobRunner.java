package com.rdebokx.ltga.sequential;

import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.shared.JobRunner;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.shared.Randomizer;

/**
 * 
 * @author Rdebokx
 *
 */
public class SequentialJobRunner extends JobRunner {

    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards.
     */
    public SequentialJobRunner(JobConfiguration config, boolean printResults) {
        super(config, new JobState(), printResults, new Randomizer());
    }
    
    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards.
     * @param fixedSeed The fixed seed for the random generator.
     */
    public SequentialJobRunner(JobConfiguration config, boolean printResults, long fixedSeed) {
        super(config, new JobState(), printResults, new Randomizer(fixedSeed));
    }
    
    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards.
     * @param fixedFOS the FOS that should be used for as a fixed Learning Model for this JobRunner.
     */
    public SequentialJobRunner(JobConfiguration config, boolean printResults, LearningModel learningModel) {
        super(config, new JobState(), printResults, learningModel, new Randomizer());
    }
    
    /**
     * Constructor, initializing this job runner with the given configuration and a new JobState object.
     * @param config The configuration for the job to be run.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards.
     * @param fixedFOS the FOS that should be used for as a fixed Learning Model for this JobRunner.
     * @param fixedSeed The fixed seed for the random generator.
     */
    public SequentialJobRunner(JobConfiguration config, boolean printResults, LearningModel learningModel, long fixedSeed) {
        super(config, new JobState(), printResults, learningModel, new Randomizer(fixedSeed));
    }
    
    /**
     * Constructor, constructing a JobRunner with the given JobConfiguration and the given Population as the first 
     * generation. Note that this is only used for debugging purposes.
     * @param config The JobConfiguration object for the job to be run.
     * @param firstGeneration The first generation for this job.
     * @param printResults Boolean indicating whether or not the results should be printed afterwards.
     */
    public SequentialJobRunner(JobConfiguration config, boolean[][] firstGeneration, boolean printResults) {
        this(config, printResults);
        this.population = new Population(firstGeneration, new double[config.GENETIC_CONFIG.POPULATION_SIZE], new double[config.GENETIC_CONFIG.POPULATION_SIZE], this.jobState);
        this.population.evaluate(config.PROBLEM_CONFIG);
    }
    
    /**
     * Starts the job of this jobRunner.
     */
    @Override
    public void run() {
        Profiler.setProgramStart();
        /*
        if(printResults){
            System.out.println("Jobrunner started");
        }
        */

        if(population == null){
            population = new Population(CONFIG.GENETIC_CONFIG.POPULATION_SIZE, CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS, jobState);
            population.initialize(CONFIG, this.randomizer);
        }
        
        bestSoFar = population.determineBestSoFar(null);
        
        while (!checkTerminationCondition()) {
            System.out.println("Generation " +jobState.getNumberOfGenerations());
            //Create offspring. New objective and constraint values are already saved in inputted arrays.
            
            LearningModel linkageModel = fixedFOS == null ? new LinkageTree((Population) population, this.randomizer, CONFIG.EXECUTION_CONFIG) : fixedFOS;
            
            population = ((Population) population).makeOffspring(bestSoFar, CONFIG, linkageModel, this.randomizer);

            jobState.incrementNumberOfGenerations();

            bestSoFar = population.determineBestSoFar(bestSoFar);
        }

        Profiler.setProgramEnd();
        //Print results
        if(printResults){
            System.out.println("Jobrunner finished. " + jobState.getNumberOfGenerations() + " generations needed." );
            Profiler.printResults();
            System.out.println("Best found solution: ");
            System.out.println(bestSoFar);
        }
    }

	@Override
	public JobRunner getNewRunnerWithConfig(JobConfiguration config) {
		return new SequentialJobRunner(config, false);
	}
}
