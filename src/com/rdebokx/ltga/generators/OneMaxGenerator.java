package com.rdebokx.ltga.generators;

import java.io.PrintWriter;

public class OneMaxGenerator {
	
	private final static int[] sizes = {25, 50, 100, 200, 400, 800, 1600};
	
	/**
	 * Main entry point for the OneMaxGenerator. This program will write files in the Uniform format for ONEMAX problems
	 * of sizes 25, 50, 100, 200, 400, 800 and 1600.
	 * @param args
	 */
	public static void main(String[] args){
		for(int size : sizes){
			writeFile(size);
		}
		System.out.println("Ok.");
	}
	
	/**
	 * This file writes a new OneMax lookup file for the given size.
	 * In this file the format will be used:
	 * numberOfParameters parametersPerFunction numberOfFunctions
	 * optimalSolution
	 * optimalValue
	 * f1
	 * f2
	 * ...
	 * @param size The proble size for which a ONEMAX file has to be created.
	 */
	private static void writeFile(int size){
		try{
		    PrintWriter printWriter = new PrintWriter ("out/onemax/onemax" + size + ".txt");
		    //Write numberOfParameters, k, numberOfFunctions;
		    printWriter.println(size + " 1 " + size);
		    
		    //Write optimalSolution
		    String optimalSolution = "\"";
		    for(int i = 0; i < size; i++){
		    	optimalSolution += "1";
		    }
		    optimalSolution += "\"";
		    printWriter.println(optimalSolution);
		    //Write optimal value
		    printWriter.println(size);
		    
		    //Write lookup tables
		    for(int i = 0; i < size; i++){
		    	printWriter.println(i);
		    	printWriter.println("\"0\" 0");
		    	if(i == size - 1){
		    		printWriter.print("\"1\" 1");
		    	} else {
		    		printWriter.println("\"1\" 1");
		    	}
		    }
		    
		    printWriter.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
