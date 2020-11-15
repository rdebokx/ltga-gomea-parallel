package com.rdebokx.ltga.experiments.optimalFixedFOS.nkLandscapes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.rdebokx.ltga.experiments.optimalFixedFOS.FOSEvaluator;
import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.experiments.optimalFixedFOS.maxcut.MaxcutLTBuilder;
import com.rdebokx.ltga.sequential.LinkageTree;
import com.rdebokx.ltga.sequential.MIMatrix;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.ProblemEvaluator;
import com.rdebokx.ltga.shared.Randomizer;

public class VarianceLTBuilder {

	private static final int NUMBER_OF_PARAMETERS = 15;
	/**
     * Main entry point for the MaxcutLTBuilder.
     * @param args Please provide MAXCUT | params | inputBasis | inputFile
     */
    public static void main(String[] args) {
        processInstance(args);
    }
    
    private static void processInstance(String[] args){
    	OptimalFixedFOSConfiguration problemConfig = FOSEvaluator.parseConfig(args, false);
        if(problemConfig != null){
	    	//TODO: run this 10 times for 10 fixed seeds?
	    	
	        MIMatrix weightsMatrix = getWeights();
	        //System.out.println("WeightsMatrix: \n" + weightsMatrix);
	        Randomizer randomizer = new Randomizer();
	        ParameterSet[] mpm = MaxcutLTBuilder.initMPM(NUMBER_OF_PARAMETERS);
	        
	        LinkageTree lt = new LinkageTree(null);
	        lt.learnStructureWithMatrix(NUMBER_OF_PARAMETERS, weightsMatrix, mpm, randomizer, false);
	        
	        System.out.println("LT: " + lt);
	        
	        double maxcutLTscore = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, lt) * -1;
	        double LTGAscore = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, null) * -1;
	        
	        String instance = args[args.length - 1];
	        System.out.println(instance + "\t" + maxcutLTscore + "\t" + LTGAscore);
	        
	        writeToFile(lt, instance);
        }
    }
    
    private static MIMatrix getWeights(){
    	MIMatrix weightsMatrix = new MIMatrix(NUMBER_OF_PARAMETERS);
    	BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("../data/NK15_4.txt"));
            
            br.lines().forEach((String line) -> {
            	String[] elements = line.split("\t");
            	int i = Integer.parseInt(elements[0]);
            	int j = Integer.parseInt(elements[1]);
            	double value = Double.parseDouble(elements[2]);
            	weightsMatrix.set(i, j, value);
            	weightsMatrix.set(j, i, value);
            });
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return weightsMatrix;
    }
    
    /**
     * This method writes the given LT for the given maxcut problem to file in ../data/maxcut/set0/FIXED-LT/
     * @param lt The Linkage Tree that should be written to file.
     * @param instance The instance for which the Linkage tree has to be written to file.
     */
    public static void writeToFile(LinkageTree lt, String instance) {
        PrintWriter writer;
        //TODO
        /*
        try {
            writer = new PrintWriter("../data/maxcut/set0/FIXED-LT/" + instance + ".txt", "UTF-8");
            writer.print(lt);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */
    }

}
