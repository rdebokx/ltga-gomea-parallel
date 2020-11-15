package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.ProblemEvaluator;
import com.rdebokx.ltga.shared.Randomizer;

public class ROFFGenerator {
	
	private static final double THRESHOLD = 0.9;
	private static volatile HashMap<Set<ParameterSet>, Double> scores = new HashMap<Set<ParameterSet>, Double>();
	private static Randomizer randomizer = new Randomizer();
	

	/**
	 * This is the main entry point for the Recombinative Optimal Fixed FOS Generator. 
	 * This executable is aimed at generating the combinations of parameters that could be interesting for constructing an 
	 * Optimal Fixed FOS. the found combinations are written to file.
	 * @param args problem | params | valueToReach or problem | params | inputBasis | inputFile
	 */
	public static void main(String[] args) {
		OptimalFixedFOSConfiguration config = OFFParseConfig(args);
		
		int numberOfParameters = config.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS;
		
		Collection<ParameterSet> combinations = getCombinations(config, numberOfParameters);
		writeToFile(config, combinations);
		System.out.println("Combinations saved to file");
	}
	
	/**
	 * This function constructs an OptimalFixedFOSConfiguration by parsing the provided arguments.
	 * @param args problem | params | valueToReach or problem | params | inputBasis | inputFile
	 * @return The parsed configuration.
	 */
	public static OptimalFixedFOSConfiguration OFFParseConfig(String[] args){
		//Format: Problem | params | valueToReach
        OptimalFixedFOSConfiguration result = null;
        try{
        	Problem problem = Problem.valueOf(args[0]);
        	int params = Integer.parseInt(args[1]);
            
            switch(problem){
            case MAXCUT:
                result = OFFDeterminator.constructMaxCutOFFConfig(problem, params, args[2], args[3], OFFDeterminator.SEEDS_FILE);
                break;
            case NK_LANDSCAPES:
                result = OFFDeterminator.constructNKOFFConfig(problem, params, args[2], args[3], OFFDeterminator.SEEDS_FILE);
                break;
            default:
                double valueToReach = Double.parseDouble(args[2]);
                result = OFFDeterminator.constructOFFConfig(problem, params, valueToReach, OFFDeterminator.SEEDS_FILE);
                break;
            }
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Please provide problem | params | valueToReach [|threads] or problem | params | inputBasis | inputFile [|threads]");
        }
        //return result;
        return ROFFDeterminator.makeOFFConfig(result);
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
		ListSet<ParameterSet> result = new ListSet<ParameterSet>(getUnivariateFOS(numberOfParameters));
		int oldSize = 0;
		while(oldSize < result.size()){
			System.out.println("--Starting new iteration");
		    oldSize = result.size();
		    //Iterator<ParameterSet> iterator1 = result.iterator();
		    ArrayList<ParameterSet> newSets = new ArrayList<ParameterSet>();
		    //TODO: randomize
		    //while(iterator1.hasNext()){
		    int[] order1 = randomizer.getRandomOrder(result.size());
		    for(int i = 0; i < result.size(); i++){
		    	System.out.println("i=" + i + ", result.size=" + result.size());
		    	//ParameterSet set1 = iterator1.next();
		    	ParameterSet set1 = result.get(order1[i]);
		    	//Iterator<ParameterSet> iterator2 = result.iterator();
		    	int[] order2 = randomizer.getRandomOrder(result.size());
		    	for(int j = i; j < result.size(); j++){
		    	//while(iterator2.hasNext()){
		    		//ParameterSet set2 = iterator2.next();
		    		//TODO: adjust this to changing array size
		    		ParameterSet set2 = result.get(order2[j]);
		    		ParameterSet combinedSet = new ParameterSet(set1, set2);
		    		
		    		//new FOS, without original sets
		    		Set<ParameterSet> newFOS = new HashSet<ParameterSet>(result);
		    		newFOS.remove(set1);
		    		newFOS.remove(set2);
		    		newFOS.add(combinedSet);
		    		
		    		if(getScore(config, newFOS) < THRESHOLD * getScore(config, new HashSet<ParameterSet>(result)) && !newSets.contains(combinedSet) && !result.contains(combinedSet)){
	        			System.out.println("Added new set: " + combinedSet + " old score: " + getScore(config, result) + " combined: " + getScore(config, newFOS));
	        			//result = newFOS;
	        			newSets.add(combinedSet);
	        		} else {
	        			//System.out.println("Not added new set: " + newSet + " old score: " + getScore(config, result) + " combined: " + getScore(config, newFOS));
	        		}
		    		while(j + 1 < order2.length && order2[j+1] >= result.size()){
			    		j++;
			    	}
		    	}
		    	while(i + 1 < order1.length && order1[i+1] >= result.size()){
		    		i++;
		    	}
		    	//make combination
		    }
		    result.addAll(newSets);
		    
		    
		    /**
		    ArrayList<ParameterSet> possibleNewCombinations = makeAllNewCombinations(result);
        	int[] order = randomizer.getRandomOrder(possibleNewCombinations.size());
        	ArrayList<ParameterSet> newSets = new ArrayList<ParameterSet>();
        	for(int i = 0; i < possibleNewCombinations.size(); i++){
        		System.out.println("i = " + i + ", ListSize is now " + possibleNewCombinations.size());
        		
        		HashSet<ParameterSet> newFOS = new HashSet<ParameterSet>(result);
        		ParameterSet newSet = possibleNewCombinations.get(order[i]);
        		newFOS.add(newSet);
        		
        		if(getScore(config, newFOS) < THRESHOLD * getScore(config, result)){
        			System.out.println("Added new set: " + newSet + " old score: " + getScore(config, result) + " combined: " + getScore(config, newFOS));
        			//result = newFOS;
        			newSets.add(newSet);
        		} else {
        			//System.out.println("Not added new set: " + newSet + " old score: " + getScore(config, result) + " combined: " + getScore(config, newFOS));
        		}
        	}
        	result.addAll(newSets);
        	*/
    	}
		return result;
	}
	
	public static ArrayList<ParameterSet> makeAllNewCombinations(HashSet<ParameterSet> set) {
        HashSet<ParameterSet> combinations = new HashSet<ParameterSet>();
        Iterator<ParameterSet> iterator1 = set.iterator();
        while(iterator1.hasNext()){
        	ParameterSet set1 = iterator1.next();
            Iterator<ParameterSet> iterator2 = set.iterator();
            while(iterator2.hasNext()){
                ParameterSet newSet = new ParameterSet(set1, iterator2.next());
                if(!set.contains(newSet) && !combinations.contains(newSet)){
                    combinations.add(newSet);
                }
            }
            
        }
        return new ArrayList<ParameterSet>(combinations);
    }

    /**
	 * This function generates the Univariate FOS.
	 * @param numberOfParameters The number of parameters for which the Univ. FOS has to be generated.
	 * @return The Univariate FOS.
	 */
	public static HashSet<ParameterSet> getUnivariateFOS(int numberOfParameters){
		HashSet<ParameterSet> result = new HashSet<ParameterSet>(numberOfParameters);
		for(int i = 0; i < numberOfParameters; i++){
			result.add(new ParameterSet(i));
		}
		return result;
	}
	
	/**
	 * This function returns the score of the given set when it would be used as a fixed FOS.
	 * If this set was already evaluated, the calculated value will be retrieved from cache.
	 * @param config The configuration for which the set has to be evaluated.
	 * @param set The set that should be used as a fixed FOS.
	 * @return The amount of evaluations needed in order to find the optimal solution when the given set is used as a fixed FOS.
	 */
	public static double getScore(OptimalFixedFOSConfiguration config, Set<ParameterSet> set){
		double result = 0;
		if(scores.get(set) == null){
			result = -1 * ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(config, new LearningModel(new ArrayList<ParameterSet>(set)));
			synchronized(ROFFGenerator.class) {
				scores.put(set, result);
			}
		} else {
			result = scores.get(set);
		}
		return result;
	}
	
	/**
	 * This function writes the given list of combinations to file that were generated for the given configuration.
	 * @param config The configuration for which the combinations have to be saved.
	 * @param combinations The combinations that were generated.
	 */
	public static void writeToFile(OptimalFixedFOSConfiguration config, Collection<ParameterSet> combinations) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(
					getFileName(config.EVALUATION_CONFIG.PROBLEM_CONFIG.PROBLEM, config.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS), "UTF-8");
			Iterator<ParameterSet> iterator = combinations.iterator();
			while(iterator.hasNext()){
				writer.print(iterator.next());
				if(iterator.hasNext()){
					writer.print(" - ");
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This function is a wrapper function for determining the filename based on the problem and number of paramters.
	 * @param problem The problem for which the filename has to be returned.
	 * @param numberOfParameters The amount of parameters for which the filename has to be returned.
	 * @return The filename for the given problem and amount of parameters.
	 */
	public static String getFileName(Problem problem, int numberOfParameters){
		return "../data/ROFFsets/" + problem + "-" + numberOfParameters + ".txt";
	}

}
