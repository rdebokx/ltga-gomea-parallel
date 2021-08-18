package com.rdebokx.ltga.generators;

import java.io.PrintWriter;

public class DeceptiveTrapGenerator {
	
	private final static int k = 5;
	private final static int[] sizes = {25, 50, 100, 200, 400, 800, 1600};
	
	public static void main(String[] args){
		for(int size : sizes){
			writeTightFile(size, true);
		}
		
		for(int size : sizes){
			writeTightFile(size, false);
		}
		System.out.println("Ok.");
	}
	
	/**
	 * This file writes a new DECEPTIVE_TRAP file for the given size and for either a tight or loose encoding.
	 * In this file the format will be used:
	 * numberOfParameters parametersPerFunction numberOfFunctions
	 * optimalSolution
	 * optimalValue
	 * f1
	 * f2
	 * ...
	 * @param size The proble size for which a DECEPTIVE_TRAP file has to be created.
	 * @param tightEncoding Boolean indicating whether it should be written for a tight or loose encoding.
	 */
	private static void writeTightFile(int size, boolean tightEncoding){
		try{
			String fileName = "";
			if(tightEncoding){
				fileName = "out/deceptive_trap_" + k + "_tight_encoding/deceptive_trap_" + k + "_tight_encoding" + size + ".txt";
			} else {
				fileName = "out/deceptive_trap_" + k + "_loose_encoding/deceptive_trap_" + k + "_loose_encoding" + size + ".txt";
			}
		    PrintWriter printWriter = new PrintWriter (fileName);
		    //Write numberOfParameters, k, numberOfFunctions;
		    printWriter.println(size + " " + k + " " + (size / k));
		    
		    //Write optimalSolution
		    String optimalSolution = "\"";
		    for(int i = 0; i < size; i++){
		    	optimalSolution += "1";
		    }
		    optimalSolution += "\"";
		    printWriter.println(optimalSolution);
		    //Write optimal value
		    printWriter.println(size / k);
		    
		    //Write lookup tables
		    for(int i = 0; i < size / k; i++){
		    	printBlock(printWriter, i, tightEncoding, size);
		    }
		    
		    printWriter.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This function prints a function block for a block of 5 parameters for the Deceptive Trap problem.
	 * @param printWriter The PrintWriter that should be used to write to the file.
	 * @param i Integer indicating the iteration we're currently at. Used to determine what parameters the function block should be written for.
	 * @param tightEncoding Boolean indicating whether or not the parameters are tightly or loosely encoded.
	 * @param size The problem size for which the file has to be written.
	 */
	private static void printBlock(PrintWriter printWriter, int i, boolean tightEncoding, int size){
		//Print line with parameters.
		if(tightEncoding){
			printTightParamLine(printWriter, i, size);
		} else {
			printLooseParamLine(printWriter, i, size);
		}
		
		for(int j = 0; j < Math.pow(2, k); j++){
			String binarySequence = Integer.toBinaryString(j);
			while(binarySequence.length() < k){
				binarySequence = "0" + binarySequence;
			}
			if(i == size / k - 1 && j == Math.pow(2, k) - 1){
				printWriter.print("\"" + binarySequence + "\" " + getScore(binarySequence));
			} else {
				printWriter.println("\"" + binarySequence + "\" " + getScore(binarySequence));
			}
		}
	}
	
	/**
	 * This method prints a line with parameters for a DECEPTIVE_TRAP_5_TIGHT_ENCODING function block.
	 * @param printWriter The PrintWriter that should be used to write to file.
	 * @param i The current iteration, used to determine what parameters should be printed.
	 * @param size The problem size.
	 */
	private static void printTightParamLine(PrintWriter printWriter, int i, int size){
		String paramLine = "";
		for(int j = 0; j < k; j++){
			paramLine += (i*k + j) + " ";
		}
		paramLine.substring(0, paramLine.length() - 1);
		printWriter.println(paramLine);
	}
	
	/**
	 * This method prints a line with parameters for a DECEPTIVE_TRAP_5_LOOSE_ENCODING function block.
	 * @param printWriter The PrintWriter that should be used to write to file.
	 * @param i The current iteration, used to determine what parameters should be printed.
	 * @param size The problem size.
	 */
	private static void printLooseParamLine(PrintWriter printWriter, int i, int size){
		int m = size / k;
		String paramLine = "";
		for(int j = 0; j < k; j++){
			paramLine += (i + m*j) + " ";
		}
		paramLine.substring(0, paramLine.length() - 1);
		printWriter.println(paramLine);
	}
	
	/**
	 * This function calculates the Deceptive Trap score of a bitsequence of size k.
	 * @param bitSequence The bitSequence that should be evaluated.
	 * @return The score of this bitsequence.
	 */
	private static double getScore(String bitSequence){
		double result = 0;
		int u = 0;
		for(int i = 0; i < k; i++){
			u += bitSequence.charAt(i) == '1' ? 1 : 0;
		}
		
		if(u == k){
            result = 1;
        } else {
            result = (k-1-u) / (k * 1.0);
        }
		
		return result;
	}

}
