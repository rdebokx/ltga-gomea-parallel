package com.rdebokx.ltga.experiments.timers;

import com.rdebokx.ltga.config.JobConfiguration;

public class Timer {
    
    protected static int RUNS = 100;
    
    /**
     * This function prints the timing statistics that were found by the given Timer. It prints statistics
     * about the total time, Matrix time, Solution time and the amount of generations needed.
     * @param determinator The type of timer that was executed.
     * @param config The configuration for which the determinator was run.
     * @param times Sorted array of total execution times that were recorded.
     * @param constructMatrixTimes Sorted array of execution times for constructing the MIMatrix that were recorded.
     * @param newSolutionTimes Sorted array of execution times for the construction of new solutions that were recorded.
     * @param generations Sorted array of the amount of generations that were required.
     */
    public static void printStatistics(String determinator, JobConfiguration config, long[] times, long[] constructMatrixTimes, long[] newSolutionTimes, long[] generations){
        System.out.println(determinator + " finished for " + config.PROBLEM_CONFIG.PROBLEM + " with l=" + config.GENETIC_CONFIG.NUMBER_OF_PARAMETERS);
        System.out.println("Printing: Time avg | Time var | Time worst | Time 10% | Time 50% | Time 90% | Time best | "
                + "MatrixTime avg | MatrixTime var | MatrixTime worst | MatrixTime 10% | MatrixTime 50% | MatrixTime 90% | MatrixTime best | "
                + "SolTime avg | SolTime var | SolTime worst | SolTime 10% | SolTime 50% | SolTime 90% | SolTime best | ");
        System.out.println("generations avg | generations var | generations min | generations 10% | generations 50% | generations 90% | generations max");
        
        printArrayStatistics(times, " ");
        printArrayStatistics(constructMatrixTimes, " ");
        printArrayStatistics(newSolutionTimes, " ");
        printArrayStatistics(generations, " ");
        System.out.println();
		
		        
    }
    
    /**
     * Wrapper method that prints the given values with a tab as delimiter.
     * @param values The values that should be printed to console.
     */
    protected static void printArrayStatistics(long[] values){
        printArrayStatistics(values, "\t");
    }
    
    /**
     * This method prints the given values, separated with the given delimiter.
     * @param values The values that should be printed.
     * @param delimiter The delimiter that should separate the values.
     */
    protected static void printArrayStatistics(long[] values, String delimiter){
    	double average = getAverage(values);
        double var = getVar(average, values);  
        
        System.out.print(average + delimiter + var + delimiter + values[0] + delimiter + values[9] + delimiter + values[49] + delimiter + values[89] + delimiter + values[99] + delimiter);
    }
    
    /**
     * Calculates the average of the given values.
     * @param values The values for which the average has to be calculated.
     * @return the average of the given values.
     */
    private static double getAverage(long[] values){
    	long sum = 0;
    	for(double value : values){
    		sum += value;
    	}
    	return (sum / (RUNS * 1.0));
    }
    
    /**
     * Calculates the variance of the given values compaged to the given average.
     * @param average The average of the values provided.
     * @param values The values for which the variance has to be calculated.
     * @return The variance of the values given.
     */
    private static double getVar(double average, long[] values){
    	long varSum = 0;
    	for(double value : values){
            varSum += (value - average) * (value - average);
        }
    	return (varSum / (RUNS * 1.0));
    }
}
