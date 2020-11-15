package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Randomizer;

public class SROFFGenerator {
	
	private static final double THRESHOLD = 1;
	private static final double EXPANSION_RATE = .75;
	private static Randomizer randomizer = new Randomizer();
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
		boolean stopCycle = false;
		HashSet<ParameterSet> removableParents = new HashSet<ParameterSet>();
		
		result.addAll(getUnionFOS(numberOfParameters));
		while(!stopCycle){
			System.out.println("Starting new iteration. " + " base: " + currentPointer + " result.size=" + result.size());
			splits = new ListSet<Split>();
			
			ExecutorService executor = Executors.newFixedThreadPool(threads);
	    	for(int t = 0; t < threads; t++){
	    		executor.submit(() -> {
	    			ParameterSet processableSet = getNextElement();
	    			while(processableSet != null){
	    				splits.addAll(generateSplitsForElems(config, processableSet));
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
	    	
	    	stopCycle = updateResult(result, splits, removableParents, config, true);
    	}
	    //Copy to set to check if no duplicates are added, for faster removal of removableParents and to sort naturally.
		return result;
	}
	
	/**
	 * This function updates the result ListSet based on the given new splits and the removable parents.
	 * Eventually, the best EXPANSION_RATE*splits.size() new elements out of splits will be added, excluding
	 * elements that are in removableParents, also considering newly added elements in removableParents.
	 * @param result The result ListSet that should be updated.
	 * @param splits The splits should be added, excluding removable parents and excluding the parents of the added splits.
	 * @param removableParents The set of ParameterSets that should be excluded from the result ListSet because it has been replaced by some split.
	 * @param config The configuration used for determining the best splits.
	 * @param removeParents True iff the parents of the splits should be excluded from the result set.
	 * @return Returns true iff new elements were added to result.
	 */
	public static boolean updateResult(ListSet<ParameterSet> result, ListSet<Split> splits, 
			HashSet<ParameterSet> removableParents, OptimalFixedFOSConfiguration config, boolean removeParents) {
		//Only add best x% of splits
    	
	    System.out.println(new Date() + " Sorting " + splits.size() + " splits.");
    	SplitComparator splitComparator = new SplitComparator(config);
    	int addSets = (int) Math.ceil(splits.size() * EXPANSION_RATE);
    	splits.sort(splitComparator);
    	
    	System.out.println(new Date() + " Determining which splits should be added to the result.");
    	HashSet<ParameterSet> newSets = new HashSet<ParameterSet>(addSets);
    	int i = 0;
    	while(i < splits.size() && newSets.size() < addSets){
    	    Split split = splits.get(i);
    	    removableParents.add(split.getParent());
    	    newSets.remove(split.getParent());
    	    
    	    //Only add sets that are relevant.
    	    Iterator<ParameterSet> children = split.getChildren().iterator();
    	    ParameterSet child1 = children.next();
    	    ParameterSet child2 = children.next();
    	    if(!removeParents || !removableParents.contains(child1)){
    	    	newSets.add(child1);
    	    }
    	    if(!removeParents || !removableParents.contains(child2)){
    	    	newSets.add(child2);
    	    }
    	    i++;
    	}
    	
    	if(removeParents){
	    	//determine pointer
	    	System.out.println(new Date() + " Remove all removable parents.");
	    	int oldResultSize = result.size();
	    	result.removeAll(removableParents);
	    	currentPointer -= oldResultSize - result.size();
    	}
    	
    	//add new sets and determine stopCycle
    	System.out.println(new Date() + " Adding all new generated splits.");
    	return !result.addAll(newSets);
	}

	/**
	 * This function generated splits for the given oldUnionSet. 
	 * @param config The configuration used for evaluating whether a split is beneficial or not.
	 * @param oldUnionSet The ParameterSet that should be split.
	 * @return A collection of splits that can replace the given oldUnionSet.
	 */
	public static Collection<Split> generateSplitsForElems(OptimalFixedFOSConfiguration config, ParameterSet oldUnionSet) {
		HashSet<Split> newSplits = new HashSet<Split>();
		
    	ArrayList<Integer> set = new ArrayList<Integer>(oldUnionSet);

    	for(int j = 0; j < set.size(); j++){
    		newSplits.addAll(makeSetSplits(config, set, oldUnionSet));
    	}
		
		return newSplits;
	}

	/**
	 * This function generates the union FOS for a given l.
	 * @param numberOfParameters The number of parameters in the UnionFOS.
	 * @return The unionFOS with teh required number of parameters.
	 */
	public static ArrayList<ParameterSet> getUnionFOS(int numberOfParameters){
		ArrayList<Integer> unionSet = new ArrayList<Integer>(numberOfParameters);
		for(int i = 0; i < numberOfParameters; i++){
			unionSet.add(i);
		}
		ArrayList<ParameterSet> result = new ArrayList<ParameterSet>();
		result.add(new ParameterSet(-1, unionSet));
		return result;
	}
	
	/**
	 * This function generates splits for the given set of which the children can replace the given set.
	 * @param config The configuration used to evaluate whether a split is beneficial or not.
	 * @param set The set of parameters, as an ArrayList of Integers, which will be repeatedly split.
	 * @param unionSet The original unionSet, used for creating new Split objects.
	 * @return A set of Splits of which the FOS of the children perform better than the given unionSet
	 */
	private static HashSet<Split> makeSetSplits(OptimalFixedFOSConfiguration config, ArrayList<Integer> set, ParameterSet unionSet){
		HashSet<Split> result = new HashSet<Split>();
		Set<ParameterSet> unionFOS = new HashSet<ParameterSet>();
		unionFOS.add(unionSet);
		for(int j = 1; j < set.size(); j++){
    		randomizer.shuffle(set);
    		ParameterSet set1 = new ParameterSet(set.subList(0, j));
    		ParameterSet set2 = new ParameterSet(set.subList(j, set.size()));
    		
    		//new FOS, without original sets
    		HashSet<ParameterSet> combFOS = new HashSet<ParameterSet>();
    		combFOS.add(set1);
    		combFOS.add(set2);
    		
    		double combScore = ROFFGenerator.getScore(config, combFOS);
    		double unionScore = ROFFGenerator.getScore(config, unionFOS);
    		if(set1.size() > 0 && set2.size() > 0 && 
    				 combScore < THRESHOLD * unionScore){
    			//System.out.println("Found new split sets: " + combFOS + " old score: " + ROFFGenerator.getScore(config, unionFOS) + " combined: " + ROFFGenerator.getScore(config, combFOS));
    		    result.add(new Split(unionSet, set1, set2));
    		} else {
    			//System.out.println("Not added new set: " + newSet + " old score: " + getScore(config, result) + " combined: " + getScore(config, newFOS));
    		}
    	}
		
		return result;
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
