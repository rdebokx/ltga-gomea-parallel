package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.util.ArrayList;

import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.ProblemEvaluator;

public class FOSEvaluator {
    
    public static final String SEEDS_FILE = "../data/seeds2.txt";

    /**
     * This is the main entry point for the FOSEvalutor. This executable is aimed at evaluating a given FOS gainst the given
     * configuration, using the seeds2.text file as for seeds.
     * @param args problem | params | valueToReach [| FOS] or problem | params | inputBasis | inputFile [| FOS]
     */
    public static void main(String[] args) {
        OptimalFixedFOSConfiguration problemConfig = parseConfig(args, true);
        if(problemConfig != null){
            LearningModel lm = null;
            System.out.println(problemConfig.EVALUATION_CONFIG.PROBLEM_CONFIG);
            if((problemConfig.EVALUATION_CONFIG.PROBLEM_CONFIG.PROBLEM == Problem.MAXCUT || problemConfig.EVALUATION_CONFIG.PROBLEM_CONFIG.PROBLEM == Problem.NK_LANDSCAPES) 
                    && args.length == 5){
                lm = new LearningModel(parseLearningModel(args[4]));
            } else if(problemConfig.EVALUATION_CONFIG.PROBLEM_CONFIG.PROBLEM != Problem.MAXCUT && problemConfig.EVALUATION_CONFIG.PROBLEM_CONFIG.PROBLEM != Problem.NK_LANDSCAPES 
                    && args.length == 4){
                lm = new LearningModel(parseLearningModel(args[3]));
            }
            
            if(lm!= null) {
                problemConfig = ROFFDeterminator.makeOFFConfig(problemConfig);
            }
            System.out.println("Evaluating for problemConfig: " + problemConfig);
            System.out.println("Evaluating for LearningModel: " + lm);
            
            double score = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, lm);
            System.out.println("Calculated score on seeds2 file: " + score);
        }
    }
    
    /**
     * This function parses the provided parameters to an OptimalFixedFOSConfiguration.
     * @param args problem | params | valueToReach [| FOS] or problem | params | inputBasis | inputFile [| FOS]
     * @return The parsed OptimalFixedFOSConfiguration that can be used for evaluating a FOS. The FOS itself is not parsed.
     */
    public static OptimalFixedFOSConfiguration parseConfig(String[] args, boolean useSeed){
        OptimalFixedFOSConfiguration result = null;
        String seedFile = useSeed ? SEEDS_FILE : null;
        try{
            Problem problem = Problem.valueOf(args[0]);
            int params = Integer.parseInt(args[1]);
            switch(problem){
            case MAXCUT:
                result = OFFDeterminator.constructMaxCutOFFConfig(problem, params, args[2], args[3], seedFile);
                break;
            case NK_LANDSCAPES:
                result = OFFDeterminator.constructNKOFFConfig(problem, params, args[2], args[3], seedFile);
                break;
            default:
                double valueToReach = Double.parseDouble(args[2]);
                result = OFFDeterminator.constructOFFConfig(problem, params, valueToReach, seedFile);
                break;
            }
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Please provide problem | params | valueToReach [| FOS] or problem | params | inputBasis | inputFile [| FOS]");
        }
        
        return result;
    }
    
    /**
     * This function parses the FOS that was provided into a LearningModel object.
     * @param blob The string representation of the LearningModel that should be parsed.
     * @return The parsed LearningModel.
     */
    public static ArrayList<ParameterSet> parseLearningModel(String blob){
        //21=[2, 4] - 50=[7, 8] - 53=[8, 9] -
        //Also accept comma separated output
        blob = blob.replace("], [", "] - [");
        ArrayList<ParameterSet> linkageSets = new ArrayList<ParameterSet>();
        String[] sets = blob.split(" - ");
        for(String set : sets){
            //System.out.println("Parsing set: " + set);
            //21=[2,4]
            int index = -1;
            String[] params;
            if(set.contains("=")){
                String[] parts = set.split("=");
                index = Integer.parseInt(parts[0]);
                params = parts[1].replace("[", "").replace("]", "").split(", ");
            } else {
                params = set.replace("[", "").replace("]", "").split(", ");
            }
            if(params.length > 0){
                ParameterSet paramSet = new ParameterSet(index, Integer.parseInt(params[0]));
                for(int i = 1; i < params.length; i++){
                    paramSet.add(Integer.parseInt(params[i]));
                }
                linkageSets.add(paramSet);
            }
        }
        
        return linkageSets;
    }

}
