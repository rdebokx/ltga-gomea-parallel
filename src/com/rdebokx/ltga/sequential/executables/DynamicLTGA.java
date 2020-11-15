package com.rdebokx.ltga.sequential.executables;

import com.rdebokx.ltga.sequential.DynamicJobRunner;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.GeneticConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.config.problems.ProblemConfiguration;
import com.rdebokx.ltga.shared.Problem;

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
public class DynamicLTGA {

    /**
     * Main entry point for the LTGA. Use either no arguments, if the arguments should be loaded from the database, or provide
     * the following arguments:
     * - The problem
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
            DynamicJobRunner runner = new DynamicJobRunner(config, true);
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
        final Problem problem = Problem.valueOf(args[0]);
        final int threads = 1;
        switch(problem){
        case NK_LANDSCAPES:
            result = loadInstanceConfig(args, threads);
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
     * - The Problem, parsed from the first argument.
     * - Number of parameters, parsed from the second argument.
     * - Population size, starts at 1.
     * - Tournament size, set to 2.
     * - Max evaluations, set to -1
     * - Use Value to Reach, set to false
     * - Value to Reach, set to -1
     * - Fitness variance tolerance, set to default fitness variance tolerance.
     * - No improvement stretch, set to -1.
     * - Timeout, parsed from the third argument. Value is in seconds.
     * 
     * @param args The program arguments from which the configuration should be parsed.
     * @return The parsed JobConfiguration object.
     */
    public static JobConfiguration loadSimpleConfig(String[] args, int threads){
        JobConfiguration result = null;
        if(args.length == 3){
            final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double valueToReach = -1;
            final double fitnessVarianceTolerance = ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
            final int noImprovementStretch = -1;
            final long timeout = System.currentTimeMillis() + Long.parseLong(args[2]) * 1000;
            
            ProblemConfiguration problemConfig = new ProblemConfiguration(problem);
            
            GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
            ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance, timeout);
            result = new JobConfiguration(genConfig, execConfig, problemConfig);
        } else {
            System.out.println("Arguments could not be parsed. Please use the following format:" +
                    "problem | numberOfParameters | timeout");
        }
        return result;
    }
    
    /**
     * This function loads the JobConfiguration for an NK_LANDSCAPE execution based on the given program arguments. The following parameters are parsed:
     * PROBLEM: NK_LANDSCAPES
     * NUMBER_OF_PARAMETERS: parsed from args[1].
     * POPULATION_SIZE: starts at 1.
     * TOURNAMENT_SIZE: set to 2.
     * MAX_EVALUATIONS: set to -1
     * USE_VALUE_TO_REACH: set to false.
     * VALUE_TO_REACH: set to -1.
     * FITNESS_VARIANCE_TOLERANCE: set to default fitness variance tolerance.
     * NO_IMPROVEMENT_STRETCH: set to -1.
     * TIMEOUT: parsed from args[3]. Value is in seconds
     * NK_LANDSCAPE config: read from file specified from args[2]
     * @param args
     * @return
     */
    public static JobConfiguration loadInstanceConfig(String[] args, int threads){
    	JobConfiguration result = null;
        if(args.length == 4){
        	final Problem problem = Problem.valueOf(args[0]);
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double valueToReach = -1;
            final double fitnessVarianceTolerance = ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
            final int noImprovementStretch = -1;
            final long timeout = System.currentTimeMillis() + Long.parseLong(args[3]) * 1000;
            ProblemConfiguration problemConfig = Main.readInstanceConfig(problem, numberOfParameters, args[2]);
            
            GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
            ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance, timeout);
            result = new JobConfiguration(genConfig, execConfig, problemConfig);
        } else {
            System.out.println("Arguments could not be parsed. Please use the following format:" +
                    "problem | numberOfParameters | inputFile | timeout(s)");
        }
        return result;
    }
    
    /**
     * This function loads the JobConfiguration for an NK_LANDSCAPE execution based on the given program arguments. The following parameters are parsed:
     * PROBLEM: NK_LANDSCAPES
     * NUMBER_OF_PARAMETERS: parsed from args[1].
     * POPULATION_SIZE: starts at 1.
     * TOURNAMENT_SIZE: set to 2.
     * MAX_EVALUATIONS: set to -1.
     * USE_VALUE_TO_REACH: set to false.
     * VALUE_TO_REACH: set to -1.
     * FITNESS_VARIANCE_TOLERANCE: set to default fitness variance tolerance.
     * NO_IMPROVEMENT_STRETCH: set to -1.
     * TIMEOUT: parsed from args[4]. Value is in seconds.
     * NK_LANDSCAPE config: read from file specified from args[2] and args[3]
     * @param args
     * @return
     */
    public static JobConfiguration loadMaxCutConfig(String[] args, int threads){
    	JobConfiguration result = null;
        if(args.length == 5){
            final int numberOfParameters = Integer.parseInt(args[1]);
            final int populationSize = 1;
            final int tournamentSize = 2;
            final int maxEvaluations = -1;
            final boolean useValueToReach = false;
            final double valueToReach = -1;
            final double fitnessVarianceTolerance = ExecutionConfiguration.DEFAULT_FITNESS_VARIANCE_TOLERANCE;
            final int noImprovementStretch = -1;
            final long timeout = System.currentTimeMillis() + Long.parseLong(args[4]) * 1000;
            
            ProblemConfiguration problemConfig = Main.readMaxCutConfiguration(numberOfParameters, args[2], args[3]);
            
            GeneticConfiguration genConfig = new GeneticConfiguration(populationSize, tournamentSize, populationSize, numberOfParameters);
            ExecutionConfiguration execConfig = new ExecutionConfiguration(threads, noImprovementStretch, maxEvaluations, useValueToReach, valueToReach, fitnessVarianceTolerance, timeout);
            result = new JobConfiguration(genConfig, execConfig, problemConfig);
        } else {
            System.out.println("Arguments could not be parsed. Please use the following format: " +
                    "problem | numberOfParameters | inputBase | inputFile | timeout");
        }
        return result;
    }
    
}
