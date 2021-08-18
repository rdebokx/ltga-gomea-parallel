package com.rdebokx.ltga.experiments.optimalFixedFOS.maxcut;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.rdebokx.ltga.sequential.executables.Main;

public class DistanceToAverageGenerator {

    private static int numberOfVertices;
    private static int numberOfEdges;
    private static int[][] weights;
    
    /**
     * Main entry point of the DistanceToAverageGenerator. This scripts reads the weights of the given instance and 
     * saves the manipulated value, being the squate of the difference between the weights and the average of all weights,
     * to file in the ../data/maxcut/set0/DistanceWeights directory.
     * @param args Please provide the input mask of the input files for which the distances have to be manipulated.
     */
    public static void main(String[] args) {
        if(args.length == 1){
            for(int k = 0; k < 10; k++){
                String instance = args[0] + "0" + k;
                readWeights("../data/maxcut/set0/" + instance + ".txt");
                
                //calculate average
                double average = 0;
                for(int i = 0; i < numberOfVertices; i++){
                    for(int j = i+1; j < numberOfVertices; j++){
                        average += weights[i][j];
                    }
                }
                average = average / (numberOfEdges * 1.0);
                System.out.println("Average weight: " + average);
                
                writeManipulatedWeights(weights, average, instance);
            }
        } else {
            System.out.println("Please provide the instance that should be transformed.");
        }
    }
    
    /**
     * This function reads the edge weights from the file specified by the given path. This will be saved in the static
     * weights matrix.
     * @param path The path to the file of which the weights have to be read.
     */
    private static void readWeights(String path){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            
            //Parse first line
            String line = br.readLine();
            String[] params = line.split(" ");
            numberOfVertices = Integer.parseInt(params[0]);
            numberOfEdges = Integer.parseInt(params[1]);
            
            weights = Main.readMaxCutWeights(br, numberOfVertices, numberOfEdges);
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
    }
    
    /**
     * This function writes the manipulated weights in the same format to a new file in the ../data/maxcut/set0/DistanceWeights folder.
     * @param weights2 The weights of which the manipulated value has to be written to file, being the square of the difference between the weight and the average of all weights.
     * @param average The average of all weights, used to determine the manipulated value that has to be saved to file.
     * @param instance The instance that corresponds to the previous parameters.
     */
    private static void writeManipulatedWeights(int[][] weights2, double average, String instance) {
        PrintWriter writer;
        try {
            writer = new PrintWriter("../data/maxcut/set0/DistanceWeights/" + instance + ".txt", "UTF-8");
            writer.println(numberOfVertices + " " + numberOfEdges);
            for(int i = 0; i < numberOfVertices; i++){
                for(int j = i+1; j < numberOfVertices; j++){
                    double adjustedWeight = (weights[i][j] - average) * (weights[i][j] - average);
                    writer.println((i+1) + " " + (j+1) + " " + adjustedWeight);
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
