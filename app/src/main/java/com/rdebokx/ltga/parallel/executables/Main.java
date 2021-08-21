package com.rdebokx.ltga.parallel.executables;

import java.util.Arrays;

import com.rdebokx.ltga.parallel.ParallelJobRunner;
import com.rdebokx.ltga.config.JobConfiguration;
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
public class Main {

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
            ParallelJobRunner runner = new ParallelJobRunner(config, true);
            
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
        final int threads = Integer.parseInt(args[args.length - 1]);
        args = Arrays.copyOf(args, args.length - 1);
        switch(problem){
        case NK_LANDSCAPES:
            result = com.rdebokx.ltga.sequential.executables.Main.loadNKConfig(args, threads);
            break;
        case MAXCUT:
            result = com.rdebokx.ltga.sequential.executables.Main.loadMaxCutConfig(args, threads);
            break;
        default:
            result = com.rdebokx.ltga.sequential.executables.Main.loadSimpleConfig(args, threads);
            break;
        }
        return result;
    }
}
