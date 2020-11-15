package com.rdebokx.ltga.experiments.optimalFixedFOS.maxcut;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.rdebokx.ltga.experiments.optimalFixedFOS.FOSEvaluator;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.sequential.LinkageTree;
import com.rdebokx.ltga.sequential.MIMatrix;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.ProblemEvaluator;
import com.rdebokx.ltga.shared.Randomizer;

public class MaxcutLTBuilder {

    /**
     * Main entry point for the MaxcutLTBuilder.
     * @param args Please provide MAXCUT | params | inputBasis | inputFile
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
     * This method processes an instance of MAXCUT, provided the given arguments.
     * This means it will learn a LT based on the weights table for this MAXCUT instance, evaluates it and writes it to file.
     * @param args The arguments for processing a MAXCUT instance.
     */
    private static void processInstance(String[] args){
        OptimalFixedFOSConfiguration problemConfig = FOSEvaluator.parseConfig(args, false);
        if(problemConfig != null){
        	//TODO: run this 10 times for 10 fixed seeds?
            int numberOfParameters = problemConfig.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS;
            MIMatrix weightsMatrix = getWeights((MaxCutConfiguration) problemConfig.EVALUATION_CONFIG.PROBLEM_CONFIG, numberOfParameters);
            Randomizer randomizer = new Randomizer();
            ParameterSet[] mpm = initMPM(numberOfParameters);
            
            LinkageTree lt = new LinkageTree(null);
            lt.learnStructureWithMatrix(numberOfParameters, weightsMatrix, mpm, randomizer, false);
            
            double maxcutLTscore = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, lt) * -1;
            double LTGAscore = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, null) * -1;
            
            String instance = args[args.length - 1];
            System.out.println(instance + "\t" + maxcutLTscore + "\t" + LTGAscore);
            
            writeToFile(lt, instance);
        }
    }
    
    /**
     * This function returns the weights from the given MaxCutConfiguration in the form of an MIMatrix such that it can be used for learning an LT
     * based on this weights table.
     * @param problemConfig The MaxCutConfiguration from which the weights table has to be retrieved.
     * @param numberOfParameters The number of parameters for the problem at hand.
     * @return The retrieved weights, in the form of an MIMatrix.
     */
    private static MIMatrix getWeights(MaxCutConfiguration problemConfig, int numberOfParameters){
        int[][] weights = problemConfig.WEIGHTS_FUNCTION.getWeights();
        
        MIMatrix weightsMatrix = new MIMatrix(numberOfParameters);
        for(int i = 0; i < numberOfParameters; i++){
            for(int j = 0; j < numberOfParameters; j++){
                weightsMatrix.set(i, j, weights[i][j]);
            }
        }
        return weightsMatrix;
    }
    
    /**
     * This function is a wrapper function for constructing an MPM that only contains the singleton sets.
     * @param numberOfParameters The number of parameters for the problem at hand.
     * @return The initialized MPM.
     */
    public static ParameterSet[] initMPM(int numberOfParameters){
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        return mpm;
    }
    
    /**
     * This method writes the given LT for the given maxcut problem to file in ../data/maxcut/set0/FIXED-LT/
     * @param lt The Linkage Tree that should be written to file.
     * @param instance The instance for which the Linkage tree has to be written to file.
     */
    public static void writeToFile(LinkageTree lt, String instance) {
        PrintWriter writer;
        try {
            writer = new PrintWriter("../data/maxcut/set0/FIXED-LT/" + instance + ".txt", "UTF-8");
            writer.print(lt);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
