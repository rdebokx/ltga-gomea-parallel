package com.rdebokx.ltga.experiments.optimalFixedFOS.maxcut;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.rdebokx.ltga.experiments.optimalFixedFOS.FOSEvaluator;
import com.rdebokx.ltga.config.problems.OptimalFixedFOSConfiguration;
import com.rdebokx.ltga.sequential.LinkageTree;
import com.rdebokx.ltga.sequential.MIMatrix;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.ProblemEvaluator;
import com.rdebokx.ltga.shared.Randomizer;

public class MaxcutDistanceLTBuilder {

    /**
     * Main entry point for the MaxcutDistanceLTBuilder. It builds an LT based on the distance matrix that contains the distances
     * of the weights compared to the average weight of all edges.
     * @param args Please provide MAXCUT | params | inputBasis | inputFile
     */
    public static void main(String[] args) {
        for(int i = 0; i < 10; i++){
            String[] hackedArgs = Arrays.copyOf(args, args.length);
            hackedArgs[args.length - 1] += "0" + i;
            processInstance(hackedArgs);
        }
        //processInstance(args);
    }
    
    /**
     * This method processes an instance of MAXCUT, provided the given arguments.
     * This means it will learn a LT based on the weights table for this MAXCUT instance, evaluates it and writes it to file.
     * @param args The arguments for processing a MAXCUT instance.
     */
    private static void processInstance(String[] args){
        OptimalFixedFOSConfiguration problemConfig = FOSEvaluator.parseConfig(args, false);
        if(problemConfig != null){
            int numberOfParameters = problemConfig.EVALUATION_CONFIG.GENETIC_CONFIG.NUMBER_OF_PARAMETERS;
            MIMatrix weightsMatrix = readWeights("../data/maxcut/set0/DistanceWeights/" + args[3] + ".txt");
            Randomizer randomizer = new Randomizer();
            ParameterSet[] mpm = MaxcutLTBuilder.initMPM(numberOfParameters);
            
            LinkageTree lt = new LinkageTree(null);
            lt.learnStructureWithMatrix(numberOfParameters, weightsMatrix, mpm, randomizer, false);
            
            double maxcutLTscore = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, lt) * -1;
            double LTGAscore = ProblemEvaluator.OptimalFixedFOSFunctionProblemEvaluation(problemConfig, null) * -1;
            
            String instance = args[args.length - 1];
            System.out.println(instance + "\t" + maxcutLTscore + "\t" + LTGAscore);
            
            writeToFile(lt, instance);
        }
    }
    
    private static MIMatrix readWeights(String path){
        MIMatrix weightsMatrix = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            
            //Parse first line
            String line = br.readLine();
            String[] params = line.split(" ");
            int numberOfVertices = Integer.parseInt(params[0]);
            int numberOfEdges = Integer.parseInt(params[1]);
            weightsMatrix = new MIMatrix(numberOfVertices);
            
            for(int i = 0; i < numberOfEdges; i++){
                String[] elements = br.readLine().split(" ");
                int x = Integer.parseInt(elements[0]) - 1;
                int y = Integer.parseInt(elements[1]) - 1;
                weightsMatrix.set(x, y, Double.parseDouble(elements[2]));
                weightsMatrix.set(y, x, Double.parseDouble(elements[2]));
            }
            
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
        try {
            writer = new PrintWriter("../data/maxcut/set0/FIXED-Distance-LT/" + instance + ".txt", "UTF-8");
            writer.print(lt);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
