package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.util.Collection;
import java.util.HashMap;

import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Randomizer;

public class ROFFGeneratorIncrRepl {
	
	private static final double THRESHOLD = 1;
	private static HashMap<Collection<ParameterSet>, Double> scores = new HashMap<Collection<ParameterSet>, Double>();
	private static Randomizer randomizer = new Randomizer();

	/**
	 * This is the main entry point for the Recombinative Optimal Fixed FOS Generator. 
	 * This executable is aimed at generating the combinations of parameters that could be interesting for constructing an 
	 * Optimal Fixed FOS. the found combinations are written to file.
	 * @param args problem | params | valueToReach or problem | params | inputBasis | inputFile
	 */
	public static void main(String[] args) {
		OptimalFixedFOSConfiguration config = ROFFGenerator.OFFParseConfig(args);
		
		int numberOfParameters = config.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS;
		
		Collection<ParameterSet> combinations = getCombinations(config, numberOfParameters);
		ROFFGenerator.writeToFile(config, combinations);
		System.out.println("Combinations saved to file");
	}
	
	/**
	 * This function generates all combinations that could be interesting for constructing an Optimal Fixed FOS. This is done
	 * by combining sets iff the combined set exceeds a specific threshold in fitness when used solely as a Fixed FOS.
	 * @param config The configuration for which the combinations have to be found.
	 * @param numberOfParameters The number of parameters
	 * @return A list of possible interesting FOS elements to run the LTGA on.
	 */
	public static Collection<ParameterSet> getCombinations(OptimalFixedFOSConfiguration config, int numberOfParameters){
		//TODO: simplify
		ListSet<ParameterSet> result = new ListSet<ParameterSet>(ROFFGenerator.getUnivariateFOS(numberOfParameters));
		int oldSize = 0;
		while(oldSize < result.size()){
			System.out.println("--Starting new iteration");
		    oldSize = result.size();
		    int[] order1 = randomizer.getRandomOrder(result.size());
		    for(int i = 0; i < oldSize; i++){
		    	System.out.println("i=" + i + " of " + oldSize + ", result.size=" + result.size());
		    	ParameterSet set1 = result.get(order1[i]);
		    	int[] order2 = randomizer.getRandomOrder(result.size());
		    	for(int j = i; j < oldSize; j++){
		    		ParameterSet set2 = result.get(order2[j]);
		    		ParameterSet combinedSet = new ParameterSet(set1, set2);
		    		
		    		//new FOS, without original sets
		    		ListSet<ParameterSet> newFOS = new ListSet<ParameterSet>(result);
		    		newFOS.remove(set1);
		    		newFOS.remove(set2);
		    		newFOS.add(combinedSet);
		    		
		    		if(ROFFGenerator.getScore(config, newFOS) < THRESHOLD * ROFFGenerator.getScore(config, result) && !result.contains(combinedSet)){
	        			System.out.println("Added new set: " + combinedSet + " old score: " + ROFFGenerator.getScore(config, result) + " combined: " + ROFFGenerator.getScore(config, newFOS));
	        			result.add(combinedSet);
	        		} else {
	        			//System.out.println("Not added new set: " + newSet + " old score: " + getScore(config, result) + " combined: " + getScore(config, newFOS));
	        		}
		    	}
		    }
    	}
		return result;
	}
}
