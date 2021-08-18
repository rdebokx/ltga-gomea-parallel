package com.rdebokx.ltga.sequential.executables;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import com.rdebokx.ltga.sequential.Population;
import com.rdebokx.ltga.sequential.SequentialJobRunner;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.MaxCutConfiguration;
import com.rdebokx.ltga.config.problems.InstancesConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.shared.MaxCutEvaluationFunction;
import com.rdebokx.ltga.shared.UniformEvaluationFunction;
import com.rdebokx.ltga.shared.Problem;
import com.rdebokx.ltga.shared.ProblemEvaluationException;
import com.rdebokx.ltga.shared.Solution;

/**
 * /**
 * Copyright (c) 2014 Roy de Bokx
 *
 * The software in this file is the proprietary information of Roy de Bokx
 *
 * IN NO EVENT WILL THE AUTHOR OF THIS SOFTWARE BE LIABLE TO YOU FOR ANY
 * DAMAGES, INCLUDING BUT NOT LIMITED TO LOST PROFITS, LOST SAVINGS, OR OTHER
 * INCIDENTIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR THE INABILITY
 * TO USE SUCH PROGRAM, EVEN IF THE AUTHOR HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES, OR FOR ANY CLAIM BY ANY OTHER PARTY. THE AUTHOR MAKES NO
 * REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE
 * AUTHOR SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY ANYONE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * Linkage Tree Genetic Algorithm
 *
 * In this implementation, maximization is assumed.
 *
 * The software in this file is the result of (ongoing) scientific research.
 * The following people have been actively involved in this research over
 * the years:
 * - Peter A.N. Bosman
 * - Dirk Thierens
 * - Roy de Bokx
 * 
 * @author Rdebokx
 *
 */
public class Main {

    /**
     * Main entry point for the LTGA. Use either no arguments, if the arguments should be loaded from the database, or provide
     * the following arguments:
     * - The problem: one of the constants defined in com.rdebokx.ltga.shared.Problem
     * - l: the number of parameters
     * - n: the population size
     * - maxEvaluations, use -1 when not applicable
     * - useValueToReach, boolean indicating whether or not to use the entered valueToReach
     * - valueToReach
     * - fitnessVarianceTolerance, use -1 if not applicable
     * - noImprovementStretch, use -1 if not applicable
     * - path to fil from which the first generation has to be read (optional)
     * The tournament size is hardcoded to 2.
     * @param args The arguments, these are being parsed as described above.
     */
    public static void main(String[] args) {
        JobConfiguration config = loadJobConfiguration(args);
        if(config != null){
            SequentialJobRunner runner = null;
            if(args.length == 9 && config.PROBLEM_CONFIG.PROBLEM != Problem.NK_LANDSCAPES){
                //Run with population from file.
                boolean[][] population = new boolean[config.GENETIC_CONFIG.POPULATION_SIZE][config.GENETIC_CONFIG.NUMBER_OF_PARAMETERS];
                readStartGeneration(args[8], config, population);
                runner = new SequentialJobRunner(config, population, true);
            } else {
                runner = new SequentialJobRunner(config, true);
            }
            runner.run();
        } else {
            System.out.println("LTGA Terminated.");
        }
    }

    /**
     * This function constructs a JobConfiguration object based on parsing the arguments that were passed to this program.
     * @param args The arguments when executing this program.
     * @return The JobConfiguration object that was based on parsing the arguments of this program.
     */
    public static JobConfiguration loadJobConfiguration(String[] args) {
        JobConfiguration result = null;
        Problem problem = null;
        try {
            problem = Problem.valueOf(args[0]);
        } catch(Exception e) {
            System.out.println("The first argument should be one of the following problems: " + Arrays.toString(Problem.values()));
            e.printStackTrace();
        }
        final int threads = 1;
        switch(problem){
        case NK_LANDSCAPES:
            result = loadNKConfig(args, threads);
            break;
        case MAXCUT:
            result = loadMaxCutConfig(args, threads);
            break;
        default:
            result = loadSimpleConfig(args, threads);
            break;
        }
        return result;
    }
    
    /**
     * This function parses a simple JobConfiguration object from the provided arguments. These arguments are parsed respectively to:
     * - The Problem.
     * - Number of parameters.
     * - Population size.
     * - Max evaluations.
     * - Use Value to Reach.
     * - Value to Reach.
     * - Fitness variance tolerance.
     * - No improvement stretch.
     * The tournament size is hardcoded to 2.
     * @param args The program arguments from which the configuration should be parsed.
     * @return The parsed JobConfiguration object.
     */
    public static JobConfiguration loadSimpleConfig(String[] args, int threads){
        JobConfiguration result = null;
        if(args.length == 8 || args.length == 9){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = Integer.parseInt(args[2]);
            final int tournamentSize = 2;
            final int maxEvaluations = Integer.parseInt(args[3]);
            final boolean useValueToReach = Boolean.parseBoolean(args[4]);
            final double valueToReach = Double.parseDouble(args[5]);
            final double fitnessVarianceTolerance = Double.parseDouble(args[6]);
            final int noImprovementStretch = Integer.parseInt(args[7]);
            
            ProblemConfiguration problemConfig = new ProblemConfiguration(problem);
            
            GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
            ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
            result = new JobConfiguration(genConfig, execConfig, problemConfig);
        } else {
            System.out.println("Arguments could not be parsed. Please use the following format:" +
                    "<problem> <numberOfParameters> <populationSize> <maxEvaluations> <useValueToReach> <valueToReach> <fitnessVarianceTolerance> <maxNoImprovementStretch>");
        }
        return result;
    }
    
    /**
     * This function loads the JobConfiguration for an NK_LANDSCAPE execution based on the given program arguments. The following parameters are parsed:
     * PROBLEM: NK_LANDSCAPES
     * NUMBER_OF_PARAMETERS: parsed from args[1].
     * POPULATION_SIZE: parsed from args[2].
     * TOURNAMENT_SIZE: set to 2.
     * MAX_EVALUATIONS: parsed from args[3].
     * USE_VALUE_TO_REACH: parsed from args[4].
     * VALUE_TO_REACH: parsed from args[5].
     * FITNESS_VARIANCE_TOLERANCE: parsed from args[6].
     * NO_IMPROVEMENT_STRETCH: parsed from args[7].
     * NK_LANDSCAPE config: read from file specified from args[8]
     * @param args
     * @return
     */
    public static JobConfiguration loadNKConfig(String[] args, int threads){
    	JobConfiguration result = null;
        if(args.length == 9){
        	final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = Integer.parseInt(args[2]);
            final int tournamentSize = 2;
            final int maxEvaluations = Integer.parseInt(args[3]);
            final boolean useValueToReach = Boolean.parseBoolean(args[4]);
            final double valueToReach = Double.parseDouble(args[5]);
            final double fitnessVarianceTolerance = Double.parseDouble(args[6]);
            final int noImprovementStretch = Integer.parseInt(args[7]);
            
            ProblemConfiguration problemConfig = readInstanceConfig(problem, numberOfParameters, args[8]);
            
            GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
            ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
            result = new JobConfiguration(genConfig, execConfig, problemConfig);
        } else {
            System.out.println("Arguments could not be parsed. Please use the following format:" +
                    "<problem> <numberOfParameters> <populationSize> <maxEvaluations> <useValueToReach> <valueToReach> <fitnessVarianceTolerance> <maxNoImprovementStretch> <inputFile>");
        }
        return result;
    }
    
    /**
     * This function loads the JobConfiguration for an NK_LANDSCAPE execution based on the given program arguments. The following parameters are parsed:
     * PROBLEM: NK_LANDSCAPES
     * NUMBER_OF_PARAMETERS: parsed from args[1].
     * POPULATION_SIZE: parsed from args[2].
     * TOURNAMENT_SIZE: set to 2.
     * MAX_EVALUATIONS: parsed from args[3].
     * USE_VALUE_TO_REACH: parsed from args[4].
     * VALUE_TO_REACH: parsed from args[5].
     * FITNESS_VARIANCE_TOLERANCE: parsed from args[6].
     * NO_IMPROVEMENT_STRETCH: parsed from args[7].
     * NK_LANDSCAPE config: read from file specified from args[8]
     * @param args
     * @return
     */
    public static JobConfiguration loadMaxCutConfig(String[] args, int threads){
    	JobConfiguration result = null;
        if(args.length == 10){
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = Integer.parseInt(args[2]);
            final int tournamentSize = 2;
            final int maxEvaluations = Integer.parseInt(args[3]);
            final boolean useValueToReach = Boolean.parseBoolean(args[4]);
            final double valueToReach = Double.parseDouble(args[5]);
            final double fitnessVarianceTolerance = Double.parseDouble(args[6]);
            final int noImprovementStretch = Integer.parseInt(args[7]);
            
            ProblemConfiguration problemConfig = readMaxCutConfiguration(numberOfParameters, args[8], args[9]);
            
            GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
            ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance);
            result = new JobConfiguration(genConfig, execConfig, problemConfig);
        } else {
            System.out.println("Arguments could not be parsed. Please use the following format: " +
                    "<problem> <numberOfParameters> <populationSize> <maxEvaluations> <useValueToReach> <valueToReach> <fitnessVarianceTolerance> <maxNoImprovementStretch> <inputBase> <inputFile>");
        }
        return result;
    }
    
    /**
     * This function reads the MaxCutConfiguration from the given input file.
     * @param numberOfParameters The number of parameters for this problem, used to check if we are using a correct instance.
     * @param base The base directory for the file.
     * @param fileName The filename that should be parsed, without extension.
     * @return The parsed MaxCutConfiguration.
     */
    public static MaxCutConfiguration readMaxCutConfiguration(int numberOfParameters, String base, String fileName){
    	MaxCutConfiguration config = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(base + fileName + ".txt"));
            
            //Parse first line
            String line = br.readLine();
            String[] params = line.split(" ");
            int numberOfVertices = Integer.parseInt(params[0]);
            if(numberOfVertices != numberOfParameters) {
                throw new ProblemEvaluationException("The amount of parameters in the read file does not match the one provided in the arguments. Please check your configuration or input file.");
            }
            int numberOfEdges = Integer.parseInt(params[1]);
            
            int[][] weights = readMaxCutWeights(br, numberOfVertices, numberOfEdges);
            MaxCutEvaluationFunction weightsFunction = new MaxCutEvaluationFunction(numberOfVertices, weights);
            int bestKnownValue = readBestKnownValue(base, fileName);
            int averageRandomValue = readAverageRandomValue(base, fileName);
            
            
            config = new MaxCutConfiguration(fileName, bestKnownValue, averageRandomValue, weightsFunction);
            br.close();
        } catch (IOException | ProblemEvaluationException e) {
            e.printStackTrace();
        } finally{
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return config;
    }
    
    /**
     * This function reads the weights that are stored in the given file and stores these in a matrix.
     * @param br The buffered reader that is set to the correct position for reading the weights from the applicable file.
     * @param numberOfVertices The number of vertices in the weights table.
     * @param numberOfVertices The number of edge weights that should be parsed.
     * @return The parsed weights table.
     */
    public static int[][] readMaxCutWeights(BufferedReader br, int numberOfVertices, int numberOfEdges){
        int[][] result = new int[numberOfVertices][numberOfVertices];
        try{
            for(int f = 0; f < numberOfEdges; f++){
                String[] weight = br.readLine().split(" ");
                int v1 = Integer.parseInt(weight[0]) - 1;
                int v2 = Integer.parseInt(weight[1]) - 1;
                result[v1][v2] = Integer.parseInt(weight[2]);
                result[v2][v1] = result[v1][v2];
                
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * This function reads the best known value from file given a base directory and a input file name (without extension)
     * @param base The base folder for the file.
     * @param fileName The filename, without extension.
     * @return The read best known value.
     */
    public static int readBestKnownValue(String base, String fileName){
    	int result = 0;
    	BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(base + "BKV/" + fileName + ".bkv"));
            
            //Parse first line
            String line = br.readLine();
            result = (int) Double.parseDouble(line);
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
        return result;
    }
    
    /**
     * This function reads the average random value from file given a base directory and a input file name (without extension)
     * @param base The base folder for the file.
     * @param fileName The filename, without extension.
     * @return The average random value.
     */
    public static int readAverageRandomValue(String base, String fileName){
    	int result = 0;
    	BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(base + "ARV/" + fileName + ".arv"));
            
            //Parse first line
            String line = br.readLine();
            result = (int) Double.parseDouble(line);
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
        return result;
    }
    
    
    /**
     * This function reads a population from an input file to use as first generation in a problem. This is used for debugging purposes only.
     * @param inputFile The input file containint the population as bitstrings.
     * @param jobConfig The JobConfiguration for the problem. Used for the number of parameters.
     * @param population The population in which the read population should be stored.
     * @return The Population object that contains the read population.
     */
    private static Population readStartGeneration(String inputFile, JobConfiguration jobConfig, boolean[][] population){
        Population result = null;
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line = br.readLine();

            int i = 0;
            while (line != null) {
                //parse String
                population[i] = Solution.parseSolution(line.substring(0, jobConfig.GENETIC_CONFIG.NUMBER_OF_PARAMETERS));
                
                line = br.readLine();
                i++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * This function reads the ProblemConfiguration from the given file and returns this.
     * @param numberOfParameters The number of parameters for this problem, as provided by the arguments of the program.
     * @param fileName The file from which the configuration should be read.
     * @return The ProblemConfiguration that was parsed from the given file.
     */
    public static InstancesConfiguration readInstanceConfig(Problem problem, int numberOfParameters, String fileName){
    	System.out.println("Reading instance config from " + fileName);
        InstancesConfiguration config = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            
            //Parse first line
            String line = br.readLine();
            String[] params = line.split(" ");
            int readParameters = Integer.parseInt(params[0]);
            if(readParameters != numberOfParameters) {
                throw new ProblemEvaluationException("The amount of parameters in the read file does not match the one provided in the arguments. Please check your configuration or input file.");
            }
            int k = Integer.parseInt(params[1]);
            //int blockTransition = Integer.parseInt(params[2]);
            int numberOfFunctions = Integer.parseInt(params[params.length - 1]);
            
            //Parse optimal solution
            line = br.readLine();
            boolean[] optimalSolution = null;
            line = line.replace("\"", "").trim();
            if(line.length() > 0){
            	optimalSolution = Solution.parseSolution(line);
            }
            
            //Parse optimal value
            line = br.readLine();
            double optimalValue = Double.parseDouble(line);

            UniformEvaluationFunction[] functions = readFunctions(br, numberOfFunctions, k);
            config = new InstancesConfiguration(problem, fileName, k, numberOfFunctions, optimalSolution, optimalValue, functions);
            br.close();
        } catch (IOException | ProblemEvaluationException e) {
            e.printStackTrace();
        } finally{
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return config;
    }
    
    /**
     * This function reads an instance of the given file into a sub-functions lookup table and returns this.
     * @param br The buffered reader that is set to the correct position for reading the lookup tables from the applicable file.
     * @param numberOfFunctions The number of sub-functions that should be read.
     * @param k The amount of parameters that the sub-functions are based on.
     * @return The parsed sub-functions lookup table.
     */
    public static UniformEvaluationFunction[] readFunctions(BufferedReader br, int numberOfFunctions, int k){
        UniformEvaluationFunction[] result = new UniformEvaluationFunction[numberOfFunctions];
        try{
            for(int f = 0; f < numberOfFunctions; f++){
                double[] functionMap = new double[(int) Math.pow(2, k)];
                String[] paramKey = br.readLine().split(" ");
                int[] params = new int[k];
                for(int i = 0; i < k; i++){
                	params[i] = Integer.parseInt(paramKey[i]);
                }
                
                for(int i = 0; i < Math.pow(2, k); i++){
                    String[] functionLine = br.readLine().split(" ");
                	String bitString = functionLine[0].replace("\"", "");
                	int bitId = Integer.parseInt(bitString, 2);
                	functionMap[bitId] = Double.parseDouble(functionLine[1]);
                }
                result[f] = new UniformEvaluationFunction(params, functionMap);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        
        return result;
    }
}
