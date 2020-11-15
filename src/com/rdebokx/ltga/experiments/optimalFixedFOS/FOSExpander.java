package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Problem;

public class FOSExpander {
	
	private static int threads = 1;
	
	private static volatile ListSet<ParameterSet> result = new ListSet<ParameterSet>();
	private static volatile ListSet<Split> splits;
	private static volatile int currentPointer = 0;

	/**
	 * This is the main entry point for the Recombinative Optimal Fixed FOS Generator. 
	 * This executable is aimed at generating the combinations of parameters that could be interesting for constructing an 
	 * Optimal Fixed FOS. the found combinations are written to file.
	 * @param args problem | params | valueToReach | threads or problem | params | inputBasis | inputFile | threads
	 */
	public static void main(String[] args) {
		OptimalFixedFOSConfiguration config = ROFFGenerator.OFFParseConfig(args);
		threads = Integer.parseInt(args[args.length - 1]);
		
		int numberOfParameters = config.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS;
		
		Collection<ParameterSet> combinations = getCombinations(config, numberOfParameters);
		//combinations.addAll(ROFFGenerator.getUnivariateFOS(numberOfParameters));
		System.out.println("Total collection of combinations (" + combinations.size() + "): " + combinations);
		ROFFGenerator.writeToFile(config, combinations);
		System.out.println("Combinations saved to file");
	}
	
	/**
	 * This function generates all combinations that could be interesting for constructing an Optimal Fixed FOS. This is done
	 * by splitting sets iff the combination of the split sets exceeds a specific threshold in fitness when used solely as a Fixed FOS.
	 * @param config The configuration for which the combinations have to be found.
	 * @param numberOfParameters The number of parameters
	 * @return A list of possible interesting FOS elements to run the LTGA on.
	 */
	public static HashSet<ParameterSet> getCombinations(OptimalFixedFOSConfiguration config, int numberOfParameters){
	    //System.out.println("Result at start of getCombinations: " + result);
		HashSet<ParameterSet> removableParents = new HashSet<ParameterSet>();
		
		result = readBaseFOS(config);
		System.out.println("Base FOS: " + result);
		
		System.out.println("Starting new iteration. " + " base: " + currentPointer + " result.size=" + result.size());
		splits = new ListSet<Split>();
		
		ExecutorService executor = Executors.newFixedThreadPool(threads);
    	for(int t = 0; t < threads; t++){
    		executor.submit(() -> {
    			ParameterSet processableSet = getNextElement();
    			while(processableSet != null){
    				splits.addAll(SROFFGenerator.generateSplitsForElems(config, processableSet));
    				processableSet = getNextElement();
    			}
    		});
    	}
		
    	executor.shutdown();
    	try {
			executor.awaitTermination(365, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    	
    	SROFFGenerator.updateResult(result, splits, removableParents, config, false);
		return result;
	}
	
	private static ListSet<ParameterSet> readBaseFOS(OptimalFixedFOSConfiguration config) {
		ListSet<ParameterSet> result = new ListSet<ParameterSet>();
		File f = new File(getBaseFOSFileName(config.EVALUATION_CONFIG.PROBLEM_CONFIG.PROBLEM, config.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS));
		if(f.exists() && f.isFile()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				reader.lines().forEach((String line) -> {
					result.add(ParameterSet.parse(line.replace(" ", ", ")));
				});
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private static String getBaseFOSFileName(Problem problem, int numberOfParameters) {
		return "../data/AMaLGaM/" + problem + "-" + numberOfParameters + ".txt";
	}

	/**
     * This function returns the first processable element in the resultList for this iteration and returns it so a thread can process it.
     * If all processable elements for this iteration have already been returned, this function will return null.
     * @return The next processable element in the resultList for this iteration iff avaialble. Null otherwise.
     */
    private static synchronized ParameterSet getNextElement(){
        ParameterSet nextElement = null;
        if(currentPointer < result.size()){
            if(currentPointer % 100 == 0){
                System.out.println(new Date() + " - Returned element " + currentPointer + " of " + result.size());
            }
            nextElement = result.get(currentPointer);
            currentPointer++;
        }
        return nextElement;
    }
}
