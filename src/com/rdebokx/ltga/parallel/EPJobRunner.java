package com.rdebokx.ltga.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rdebokx.ltga.sequential.SequentialJobRunner;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.shared.JobRunner;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.shared.Randomizer;
import com.rdebokx.ltga.shared.Solution;

public class EPJobRunner extends JobRunner {
    
    private ExecutorService executorService;
    private SequentialJobRunner[] runners;
    private Solution[] solutions;
    
    /**
     * Constructor, constructing a new Embarrassingly Parallel JobRunner. This jobRunner will
     * start x Sequential JobRunners which will run in parallel.
     * @param config The configuration to be used for this JobRunner.
     * @param printResults Boolean indicating whether or not profiling should be enabled and results should be printed to console.
     * @param randomizer The Randomizer object that should be used.
     */
    public EPJobRunner(JobConfiguration config, boolean printResults, Randomizer randomizer){
    	super(config, new JobState(), printResults, randomizer);
        this.executorService = Executors.newFixedThreadPool(config.EXECUTION_CONFIG.THREADS);
        this.runners = new SequentialJobRunner[config.EXECUTION_CONFIG.THREADS];
        this.solutions = new Solution[config.EXECUTION_CONFIG.THREADS];
    }
    
    /**
     * Constructor, constructing a new Embarrassingly Parallel JobRunner. This jobRunner will
     * start x Sequential JobRunners which will run in parallel.
     * @param config The configuration to be used for this JobRunner.
     * @param printResults Boolean indicating whether or not profiling should be enabled and results should be printed to console.
     */
    public EPJobRunner(JobConfiguration config, boolean printResults){
        super(config, new JobState(), printResults, new Randomizer());
        this.executorService = Executors.newFixedThreadPool(config.EXECUTION_CONFIG.THREADS);
        this.runners = new SequentialJobRunner[config.EXECUTION_CONFIG.THREADS];
        this.solutions = new Solution[config.EXECUTION_CONFIG.THREADS];
    }
    
    /**
     * Constructor, constructing a new Embarrassingly Parallel JobRunner. This jobRunner will
     * start x Sequential JobRunners which will run in parallel.
     * @param config The configuration to be used for this JobRunner.
     * @param printResults Boolean indicating whether or not profiling should be enabled and results should be printed to console.
     * @param fixedFOS the FOS that should be used for as a fixed Learning Model for this JobRunner
     */
    public EPJobRunner(JobConfiguration config, boolean printResults, LearningModel fixedFOS){
        this(config, printResults);
        this.fixedFOS = fixedFOS;
    }
    
    /**
     * Constructor, constructing a new Embarrassingly Parallel JobRunner. This jobRunner will
     * start x Sequential JobRunners which will run in parallel.
     * @param config The configuration to be used for this JobRunner.
     * @param printResults Boolean indicating whether or not profiling should be enabled and results should be printed to console.
     * @param fixedFOS the FOS that should be used for as a fixed Learning Model for this JobRunner
     * @param fixedSeed The fixed seed that should be used for the Randomizer. 
     */
    public EPJobRunner(JobConfiguration config, boolean printResults, LearningModel fixedFOS, long fixedSeed){
        this(config, printResults, new Randomizer(fixedSeed));
        this.fixedFOS = fixedFOS;
    }
    
    @Override
    public void run(){
        if(printResults){
            Profiler.setProgramStart();
        }
        
        for(int i = 0; i < CONFIG.EXECUTION_CONFIG.THREADS; i++){
            runners[i] = new SequentialJobRunner(CONFIG, false, fixedFOS);
            executorService.submit(runners[i]);
        }
        
        executorService.shutdown();
        try {
            executorService.awaitTermination(365, TimeUnit.DAYS);
            for(int i = 0; i < CONFIG.EXECUTION_CONFIG.THREADS; i++){
                Solution sol = runners[i].getBestFound();
                solutions[i] = sol;
                if(bestSoFar == null || bestSoFar.getObjectiveValue() < sol.getObjectiveValue()){
                    bestSoFar = sol;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(printResults){
            Profiler.setProgramEnd();
            //Profiler.printResults();
        }
    }
    
    /**
     * @return The total number of evaluations performed by all executed Sequential JobRunners.
     */
    public int getNumberOfEvaluations(){
        int result = 0;
        for(int i = 0; i < CONFIG.EXECUTION_CONFIG.THREADS; i++){
            result += runners[i].getJobState().getNumberOfEvaluations();
        }
        return result;
    }
    
    /**
     * @return The total number of generations that were traversed by all executed Sequential JobRunners.
     */
    public int getNumberOfGenerations(){
        int result = 0;
        for(int i = 0; i < CONFIG.EXECUTION_CONFIG.THREADS; i++){
            result += runners[i].getJobState().getNumberOfGenerations();
        }
        return result;
    }

	@Override
	public JobRunner getNewRunnerWithConfig(JobConfiguration config) {
		return new EPJobRunner(config, printResults);
	}
    
}
