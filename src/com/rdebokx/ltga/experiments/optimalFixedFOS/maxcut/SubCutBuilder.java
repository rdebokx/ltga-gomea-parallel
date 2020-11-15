package com.rdebokx.ltga.experiments.optimalFixedFOS.maxcut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rdebokx.ltga.experiments.optimalFixedFOS.FOSEvaluator;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.sequential.LinkageTree;
import com.rdebokx.ltga.shared.Pair;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.ProblemEvaluator;
import com.rdebokx.ltga.shared.Randomizer;

public class SubCutBuilder {
    
    private static HashMap<ParameterSet, Pair<ParameterSet>> merges = new HashMap<ParameterSet, Pair<ParameterSet>>();

    /**
     * Main entry point for SubCutBuilder. This script will build a tree based on the optimal subcuts that can be found.
     * For this instance, an x amount of runs will be executed, resulting in x trees that will be saved to file.
     * The amount of runs is determined by the given start and end.
     * @param args The arguments for this script. Please provide: MAXCUT | params | inputBase | inputFile | runsStart | runsEnd
     */
    public static void main(String[] args) {
        /*
        for(int i = 0; i < 10; i++){
            String[] hackedArgs = Arrays.copyOf(args, args.length);
            hackedArgs[args.length - 1] += "0" + i;
            processInstance(hackedArgs);
        }
        */
        processInstance(args);
    }
    
    /**
     * This function processes one single instance 10 times.
     * @param args The arguments with which the program was called.
     */
    private static void processInstance(String[] args){
        OptimalFixedFOSConfiguration problemConfig = FOSEvaluator.parseConfig(args, false);
        if(problemConfig != null){
            //double[] maxCutScores;
            //double LTGATotal = 0;
            String instance = args[args.length - 3];
            int start = Integer.parseInt(args[args.length - 2]);
            int end = Integer.parseInt(args[args.length - 1]);
            
            ExecutorService executor = Executors.newFixedThreadPool(end-start);
            
            for(int i = start; i < end; i++){
                int run = i;
                executor.submit(() -> {
                
                    
                    
                    ArrayList<ParameterSet> treeList = buildLT(problemConfig);
                    LinkageTree lt = new LinkageTree(treeList);
                    
                    double maxcutScore = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, lt) * -1;
                    //maxCutTotal += maxcutScore;
                    double LTGAScore = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, null) * -1;
                    //LTGATotal += LTGAScore;
                
                    System.out.println(instance + "-" + run + "\t" + maxcutScore + "\t" + LTGAScore);
                    
                    MaxcutLTBuilder.writeToFile(lt, instance + "-" + run);
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.DAYS);
                System.out.println("===SubCutBuilder done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //maxCutTotal /= 10;
            //LTGATotal /= 10;
            //System.out.println(instance + "\t" + maxCutTotal + "\t" + LTGATotal);
        }
    }
    
    /**
     * This function builds an LT based on the merging best subcuts.
     * @param problemConfig The OptimalFixedFOSConfiguration for which the best subcuts have to be 
     * @return The Linkage Tree that was built, consisting of optimal subcuts
     */
    private static ArrayList<ParameterSet> buildLT(OptimalFixedFOSConfiguration problemConfig){
        Randomizer randomizer = new Randomizer();
        List<ParameterSet> mpm = new ArrayList<ParameterSet>(Arrays.asList(MaxcutLTBuilder.initMPM(problemConfig.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS)));
        ArrayList<ParameterSet> lt = new ArrayList<ParameterSet>(mpm);
        MaxCutConfiguration maxcutConfig = (MaxCutConfiguration) problemConfig.EVALUATION_CONFIG.PROBLEM_CONFIG;
        
        
        while(mpm.size() > 1){
            ParameterSet set = mpm.get(randomizer.generator.nextInt(mpm.size()));
            //System.out.println("Searching best merge for set: " + set + " in " + mpm);
            int bestIndex = -1;
            Pair<ParameterSet> bestCut = null;
            int bestCutValue = -1;
            int[] order = randomizer.getRandomOrder(mpm.size());
            for(int i = 0; i < mpm.size(); i++){
                if(!mpm.get(order[i]).equals(set)){
                    //System.out.println("Investigating merge with " + mpm.get(order[i]));
                    Pair<ParameterSet> foundBestCut = getBestCut(set, mpm.get(order[i]), maxcutConfig);
                    int newCutValue = calculateNormalizedCut(foundBestCut.getVal1(), null, foundBestCut.getVal2(), null, maxcutConfig);
                    if(newCutValue > bestCutValue){
                        bestIndex = order[i];
                        bestCut = foundBestCut;
                        bestCutValue = newCutValue;
                        //System.out.println("--Found new overal bestCut with " + set + ": " + bestCut);
                    }
                }
            }
            
            //Save merge
            ParameterSet merge = new ParameterSet(bestCut.getVal1(), bestCut.getVal2());
            lt.add(merge);
            //System.out.println("Best cut: " + bestCut);
            //System.out.println("Merge: " + merge);
            
            //store children of cut in merges.
            merges.put(merge, bestCut);
            
            //remove sets (not children) from mpm and add new merge
            //System.out.println("Replacing " + set + "and " + mpm.get(bestIndex) + " by " + merge);
            mpm.remove(bestIndex);
            mpm.remove(set);
            mpm.add(merge);
            
            //System.out.println("New MPM: " + mpm);
        }
        
        //System.out.println("Resulting LT: " + lt);
        return lt;
    }
    
    /**
     * This function returns the best cut possible between the parameters in the given two sets, given that they have to
     * be merged, which means that they have to be combined in some way. This is done by making the possible 
     * combinations between the optimal subcuts of the given sets an returning the best combination of these.
     * @param set1 The first set of which the optimal subcuts have to be combined with the optimal subcuts of the second set
     * @param set2 The second set of which the subcuts have to be combined with the optimal subcuts of the first set.
     * @param config The configuration used to evaluate the value of the subcuts.
     * @return The optimal subcut, found by recombining the optimal subcuts of the given parameter sets.
     */
    private static Pair<ParameterSet> getBestCut(ParameterSet set1, ParameterSet set2, MaxCutConfiguration config){
        Pair<ParameterSet> result = null;
        int bestCut = -1;
        
        if(merges.containsKey(set1) && merges.containsKey(set2)){
            //set1 and set2 are combinations
            Pair<ParameterSet> subsets1 = merges.get(set1);
            Pair<ParameterSet> subsets2 = merges.get(set2);
            if(calculateNormalizedCut(subsets1.getVal1(), subsets2.getVal1(), subsets1.getVal2(), subsets2.getVal2(), config) > bestCut){
                ParameterSet cut1 = new ParameterSet(subsets1.getVal1(), subsets2.getVal1());
                ParameterSet cut2 = new ParameterSet(subsets1.getVal2(), subsets2.getVal2());
                result = new Pair<ParameterSet>(cut1, cut2);
            }
            if(calculateNormalizedCut(subsets1.getVal1(), subsets2.getVal2(), subsets1.getVal2(), subsets2.getVal1(), config) > bestCut){
                ParameterSet cut1 = new ParameterSet(subsets1.getVal1(), subsets2.getVal2());
                ParameterSet cut2 = new ParameterSet(subsets1.getVal2(), subsets2.getVal1());
                result = new Pair<ParameterSet>(cut1, cut2);
            }
        } else if(merges.containsKey(set1)){
            //set1 is a combination, set2 is a singleton
            Pair<ParameterSet> subsets1 = merges.get(set1);
            if(calculateNormalizedCut(subsets1.getVal1(), set2, subsets1.getVal2(), null, config) > bestCut){
                ParameterSet cut1 = new ParameterSet(subsets1.getVal1(), set2);
                ParameterSet cut2 = new ParameterSet(subsets1.getVal2());
                result = new Pair<ParameterSet>(cut1, cut2);
            }
            if(calculateNormalizedCut(subsets1.getVal2(), set2, subsets1.getVal1(), null, config) > bestCut){
                ParameterSet cut1 = new ParameterSet(subsets1.getVal2(), set2);
                ParameterSet cut2 = new ParameterSet(subsets1.getVal1());
                result = new Pair<ParameterSet>(cut1, cut2);
            }
        } else if(merges.containsKey(set2)) {
            //set1 is a singleton, set2 is a combination
            Pair<ParameterSet> subsets2 = merges.get(set2);
            if(calculateNormalizedCut(set1, subsets2.getVal1(), subsets2.getVal2(), null, config) > bestCut){
                ParameterSet cut1 = new ParameterSet(set1, subsets2.getVal1());
                ParameterSet cut2 = new ParameterSet(subsets2.getVal2());
                result = new Pair<ParameterSet>(cut1, cut2);
            }
            if(calculateNormalizedCut(set1, subsets2.getVal2(), subsets2.getVal1(), null, config) > bestCut){
                ParameterSet cut1 = new ParameterSet(set1, subsets2.getVal2());
                ParameterSet cut2 = new ParameterSet(subsets2.getVal1());
                result = new Pair<ParameterSet>(cut1, cut2);
            }
        } else {
            //set1 and set2 are both singletons
            if(calculateNormalizedCut(set1, null, set2, null, config) > bestCut){
                ParameterSet cut1 = new ParameterSet(set1);
                ParameterSet cut2 = new ParameterSet(set2);
                result = new Pair<ParameterSet>(cut1, cut2);
            }
        }
        
        //System.out.println("Best cut for " + set1 + " and " + set2 + ": " + result);
        return result;
    }
    
    /**
     * Function calculates the normalized cut value between the given two cuts. It is normalized because it is divided by
     * the amount of edges that run between the two cuts.
     * @param cut1set1 The first set in cut1.
     * @param cut1set2 The second set in cut1.
     * @param cut2set1 The first set in cut2.
     * @param cut2set2 The second set in cut2.
     * @param config The ProblemConfiguration used to evaluate the cut value.
     * @return The normalized value of the cut.
     */
    private static int calculateNormalizedCut(ParameterSet cut1set1, ParameterSet cut1set2, ParameterSet cut2set1, ParameterSet cut2set2, MaxCutConfiguration config){
        int result = 0;
        ParameterSet cut1 = new ParameterSet(cut1set1, cut1set2);
        ParameterSet cut2 = new ParameterSet(cut2set1, cut2set2);
        for(Integer vertex1 : cut1){
            for(Integer vertex2 : cut2){
                result += config.WEIGHTS_FUNCTION.getWeights()[vertex1][vertex2];
            }
        }
        
        result /= (cut1.size() * cut2.size() * 1.0);
        //System.out.println("Cut tussen " + cut1 + " " + cut2 + ": " + result);
        
        return result;
    }

}
