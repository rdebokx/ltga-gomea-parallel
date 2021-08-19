package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.parallel.ParallelJobRunner;
import com.rdebokx.ltga.sequential.executables.Main;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.Solution;

public class OFFDeterminator {
    
    public static final String SEEDS_FILE = "src/main/resources/problemdata/seeds.txt";
	
    /**
     * This is the main entry point of the OFFDeterminator. This executable is aimed at determining the Optimal Fixed FOS for a given 
     * configuration. This is done by, starting with the Univariate FOS, making combinations and then running the LTGA on this FOS
     * to determine what sets might be beneficial in a fixed strucure. These found sets are then recombinated again, after which the LTGA
     * is run on this new FOS. This is repated untill now improvement could be found.
     * @param args minPopSize | evalProblem | evalParams | evalValueToReach | threads OR minPopSize | evalProblem | evalParams | inputBasis | inputMask | threads for MAXCUT or NK_LANDSCAPES
     */
    public static void main(String[] args) {
        JobConfiguration jobConfig = loadJobConfig(args);
        if(jobConfig != null){
        	dynamicallyFindOptimalFOS(jobConfig);
        } else {
            System.out.println("LTGA could not load the job configuration");
        }
    }
    
    /**
     * This method dynamically tries to find the Optimal Fixed FOS. Starting with a set population size, it will run the LTGA on the 
     * given set of FOS elements with an increasing population size.
     * @param jobConfig The jobConfiguration for which the Optimal Fixed FOS has to be determined.
     */
    public static void dynamicallyFindOptimalFOS(JobConfiguration jobConfig){
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Calendar cal = Calendar.getInstance();
    	
        while(true){
        	System.out.println("Starting with new popSize at " + dateFormat.format(cal.getTime()));
        	findOptimalFOS(jobConfig);
        	jobConfig = jobConfig.copyForPopSize(jobConfig.GENETIC_CONFIG.POPULATION_SIZE * 2);
        }
    }
    
    /**
     * This method is running the LTGA with the given JobConfiguration object for a fixed population size.
     * @param jobConfig The JobConfiguration for which the LTGA has to be executed.
     */
    private static void findOptimalFOS(JobConfiguration jobConfig){
    	System.out.println("==Running OFFDeterminator for popSize " + jobConfig.GENETIC_CONFIG.POPULATION_SIZE);
    	
    	Solution bestFound = null;
        OptimalFixedFOSConfiguration bestConfig = null;
        OptimalFixedFOSConfiguration newOFFConfig = null;
        boolean improvement = true;
        while(improvement){
            //Construct new jobConfig with new combination of parameterSets
            OptimalFixedFOSConfiguration OFFConfig = (OptimalFixedFOSConfiguration) jobConfig.PROBLEM_CONFIG;
            List<ParameterSet> linkageSets = generateCombinations(OFFConfig.LINKAGE_SETS);
            newOFFConfig = new OptimalFixedFOSConfiguration(linkageSets, OFFConfig.EVALUATION_CONFIG, OFFConfig.SEEDS);
            
            jobConfig = new JobConfiguration(jobConfig.GENETIC_CONFIG, jobConfig.EXECUTION_CONFIG, newOFFConfig);
            System.out.println("Starting new ParallelRunner");
            ParallelJobRunner runner = new ParallelJobRunner(jobConfig, false);
            runner.run();
            if(bestFound == null || runner.getBestFound().getObjectiveValue() > bestFound.getObjectiveValue()){
                bestFound = runner.getBestFound();
                bestConfig = newOFFConfig;
                System.out.println("New best solution found: " + bestFound);
            } else {
                improvement = false;
                System.out.println("No improvement encountered.");
            }
        }
        parseFoundResults(bestFound, bestConfig);
    }
    
    /**
     * This method prints the found solution in a human readable format.
     * @param bestFound The best found solution.
     * @param newOFFConfig The configuration for which a best solution was found.
     */
    public static void parseFoundResults(Solution bestFound, OptimalFixedFOSConfiguration newOFFConfig) {
        System.out.println("Optimal Fixed FOS Found for " + newOFFConfig.EVALUATION_CONFIG.PROBLEM_CONFIG.PROBLEM + 
                ", l=" + newOFFConfig.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS);
        
        System.out.println(bestFound);
        String result = "";
        boolean[] solution = bestFound.getSolution();
        for(int i = 0; i < solution.length; i++){
            if(solution[i]){
                result += newOFFConfig.LINKAGE_SETS.get(i) + " - ";
            }
        }
        System.out.println(result.length() >= 3 ? result.substring(0, result.length() - 3) : "<empty FOS>");
        
    }

    /**
     * This function parses the given arguments for the format popSize | evalProblem | evalParams | evalValueToReach | threads.
     * @param args The arguments that should be parsed.
     * @return The jobConfiguration object based on the arguments iff these could be parsed. Else null.
     */
    private static JobConfiguration loadJobConfig(String[] args){
        JobConfiguration jobConfig = null;
        if(args.length == 5 || args.length == 6){
            final int popSize = Integer.parseInt(args[0]);
            final Problem evalProblem = Problem.valueOf(args[1]);
            final int evalParams = Integer.parseInt(args[2]);
            final int threads = Integer.parseInt(args[args.length - 1]);
            
            ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, -1, -1, false, -1, ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE);
            GeneticConfiguration geneticConfig = new GeneticConfiguration(popSize, popSize, popSize, evalParams * (evalParams - 1) / 2 + evalParams);
            OptimalFixedFOSConfiguration problemConfig = null;
            switch(evalProblem){
            case NK_LANDSCAPES:
            	problemConfig = constructNKOFFConfig(evalProblem, evalParams, args[3], args[4], SEEDS_FILE);
            	break;
            case MAXCUT:
            	problemConfig = constructMaxCutOFFConfig(evalProblem, evalParams, args[3], args[4], SEEDS_FILE);
            	break;
            default:
            	problemConfig = constructOFFConfig(evalProblem, evalParams, Integer.parseInt(args[3]), SEEDS_FILE);
            }
            
            jobConfig = new JobConfiguration(geneticConfig, execConfig, problemConfig);
        } else {
            System.out.println("Arguments could not be parsed. Pleas provide minPopSize | evalProblem | evalParams | "
                    + "evalValueToReach | threads OR minPopSize | evalProblem | evalParams | inputBasis | inputMask | threads for MAXCUT or NK_LANDSCAPES");
        }
        
        return jobConfig;
    }
    
    /**
     * This function is a wrapper function for constructing a new OFF configuration object based on the given parameters. 
     * @param evalProblem The problem for evaluating the simulated LTGAs.
     * @param evalParams The parameters for the simulated LTGA.
     * @param evalValueToReach The value to reach for the simulated LTGAs.
     * @return The OFF configuration object.
     */
    public static OptimalFixedFOSConfiguration constructOFFConfig(Problem evalProblem, int evalParams, double evalValueToReach, String seedsFile){
        //Construct univeratie FOS for Linkage Sets.
        ArrayList<ParameterSet> linkageSets = new ArrayList<ParameterSet>();
        for(int i = 0; i < evalParams; i++){
            linkageSets.add(new ParameterSet(i, i));
        }
        
        //Construct evaluationConfig
        ProblemConfiguration evalProblemConfig = new ProblemConfiguration(evalProblem);
        //ExecutionConfiguration evalExecConfig = new ExecutionConfiguration(1, -1, -1, false, evalValueToReach, ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE);
        ExecutionConfiguration evalExecConfig = new ExecutionConfiguration(1, -1, -1, true, evalValueToReach, ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE);
        GeneticConfiguration evalGeneticConfig = new GeneticConfiguration(1, 1, 1, evalParams);
        JobConfiguration evaluationConfig = new JobConfiguration(evalGeneticConfig, evalExecConfig, evalProblemConfig);
        
        //Read seeds
        long[][] seeds = readSeeds(seedsFile);
        
        return new OptimalFixedFOSConfiguration(linkageSets, evaluationConfig, seeds);
    }
    
    /**
     * This function is a wrapper function for constructing a new OFF configuration object based on the given parameters. 
     * @param evalProblem The problem for evaluating the simulated LTGAs.
     * @param evalParams The parameters for the simulated LTGA.
     * @param fileBase The fileBase for the inputMask from which instances have to be read.
     * @param inputMask The mask for the filenames from which the instances have to be read.
     * @return The OFF configuration object.
     */
    public static OptimalFixedFOSConfiguration constructNKOFFConfig(Problem evalProblem, int evalParams, String fileBase, String inputMask, String seedsFile){
        //Construct univeratie FOS for Linkage Sets.
        ArrayList<ParameterSet> linkageSets = new ArrayList<ParameterSet>();
        for(int i = 0; i < evalParams; i++){
            linkageSets.add(new ParameterSet(i, i));
        }
        
        //Construct evaluationConfig
        InstancesConfiguration evalProblemConfig = Main.readInstanceConfig(evalProblem, evalParams, fileBase + inputMask);
        GeneticConfiguration evalGenConfig = new GeneticConfiguration(1, 1, 1, evalParams);
        //ExecutionConfiguration evalExecConfig = new ExecutionConfiguration(1, -1, -1, false, evalProblemConfig.OPTIMAL_VALUE, ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE);
        ExecutionConfiguration evalExecConfig = new ExecutionConfiguration(1, -1, -1, true, evalProblemConfig.OPTIMAL_VALUE, ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE);
        JobConfiguration evaluationConfig = new JobConfiguration(evalGenConfig, evalExecConfig, evalProblemConfig);
        
        //Read seeds
        long[][] seeds = null;
        if(seedsFile != null){
        	seeds = readSeeds(seedsFile);
        }
        
        return new OptimalFixedFOSConfiguration(linkageSets, evaluationConfig, seeds);
    }
    
    /**
     * This function is a wrapper function for constructing a new OFF configuration object based on the given parameters. 
     * @param evalProblem The problem for evaluating the simulated LTGAs.
     * @param evalParams The parameters for the simulated LTGA.
     * @param fileBase The base for the input mask from which an instance must be read.
     * @param inputMask The mask for the input file that has to be read.
     * @return The OFF configuration object.
     */
    public static OptimalFixedFOSConfiguration constructMaxCutOFFConfig(Problem evalProblem, int evalParams, String fileBase, String inputMask, String seedsFile){
        //Construct univeratie FOS for Linkage Sets.
        ArrayList<ParameterSet> linkageSets = new ArrayList<ParameterSet>();
        for(int i = 0; i < evalParams; i++){
            linkageSets.add(new ParameterSet(i, i));
        }
        
        //Construct evaluationConfig
        MaxCutConfiguration evalProblemConfig = Main.readMaxCutConfiguration(evalParams, fileBase, inputMask);
        //ExecutionConfiguration evalExecConfig = new ExecutionConfiguration(1, -1, -1, false, evalProblemConfig.BEST_KNOWN_VALUE, ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE);
        ExecutionConfiguration evalExecConfig = new ExecutionConfiguration(1, -1, -1, true, evalProblemConfig.BEST_KNOWN_VALUE, ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE);
        GeneticConfiguration evalGeneticConfig = new GeneticConfiguration(1, 1, 1, evalParams);
        JobConfiguration evaluationConfig = new JobConfiguration(evalGeneticConfig, evalExecConfig, evalProblemConfig);
        
        //Read seeds
        long[][] seeds = null;
        if(seedsFile != null){
        	seeds = readSeeds(seedsFile);
        }
        
        return new OptimalFixedFOSConfiguration(linkageSets, evaluationConfig, seeds);
    }

    /**
     * This function generates all possible combinations of the parameterSets in the given model.
     * @param model The model for which all combinations of elements should be generated.
     * @return The new list with ParameterSets with all possible combinations of the previous model.
     */
    private static List<ParameterSet> generateCombinations(List<ParameterSet> model) {
        ArrayList<ParameterSet> result = new ArrayList<ParameterSet>();
        int index = 0;
        for(int i = 0; i < model.size(); i++){
            for(int j = i; j < model.size(); j++){
                ParameterSet set = new ParameterSet(index, model.get(i), model.get(j));
                result.add(set);
                index++;
            }
        }
        return result;
    }

    /**
     * This function reads the seeds from the data/seeds.txt file and returns these as a 10 by 100 array of longs.
     * @return The seeds that were read from the data/seeds.txt file.
     */
    public static long[][] readSeeds(String file){
        long[][] result = new long[10][100];
        try{
            Scanner scanner = new Scanner(new File(file));
            for(int i = 0; i < 1000; i++){
                result[i / 100][i % 100] = scanner.nextLong();
            }
            scanner.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * This methods generates a new seeds file with 1000 random seeds.
     */
    public static void writeSeeds(){
        try {
            Random generator = new Random();
            FileWriter writer = new FileWriter(new File(""));
            for(int i = 0; i < 10*100; i++){
                writer.write(Long.toString(generator.nextLong()) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
