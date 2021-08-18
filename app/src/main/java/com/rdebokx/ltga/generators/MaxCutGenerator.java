package com.rdebokx.ltga.generators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.rdebokx.ltga.sequential.executables.Main;

public class MaxCutGenerator {
	
	private final static String[] fileNames = {
		"n0000006i", 
		"n0000012i",
		"n0000050i",
		"n0000025i",
		"n0000100i"
	};
	
	/**
	 * Main entry point for the MaxCutGenerator. This program will convert the present MaxCut files to the Uniform format.
	 * @param args
	 */
	public static void main(String[] args){
		for(int i = 0; i < fileNames.length; i++){
			convertFile(fileNames[i]);
		}
		System.out.println("Ok.");
	}
	
	/**
	 * This method writes a new MaxCut file, based on the given file.
	 * @param fileName The filename of the file of which the contents should be converted to the new format.
	 */
	private static void convertFile(String fileName){
		for(int i = 0; i < 10; i++){
            BufferedReader br = null;
            try {
            	br = new BufferedReader(new FileReader("data/maxcut/set0/" + fileName + "0" + i + ".txt"));
                String[] firstLine = br.readLine().split(" ");
                int numberOfParameters = Integer.parseInt(firstLine[0]);
                int numberOfEdges = Integer.parseInt(firstLine[1]);
                
                PrintWriter printWriter = new PrintWriter("out/maxcut/maxcut" + numberOfParameters + "_" + i + ".txt");
                //Print numberOfParameters, k and numberOfFunctions.
                printWriter.println(numberOfParameters + " 2 " + numberOfEdges);
                
                //Print optimal solution (unknown)
                printWriter.println("\"\"");
                //Print optimal value
                printWriter.println(Main.readBestKnownValue("data/maxcut/set0/", fileName + "0" + i));

                for(int j = 0; j < numberOfEdges; j++){
                	String[] params = br.readLine().split(" ");
                	printWriter.println((Integer.parseInt(params[0]) - 1) + " " + (Integer.parseInt(params[1]) - 1));
                	printWriter.println("\"00\" 0");
                	printWriter.println("\"01\" " + params[2]);
                	printWriter.println("\"10\" " + params[2]);
                	if(j == numberOfEdges - 1){
                		printWriter.print("\"11\" 0");
                	} else {
                		printWriter.println("\"11\" 0");
                	}
                }
                
                br.close();
                printWriter.close();
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
	}
}
