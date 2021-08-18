package com.rdebokx.ltga.experiments.popSizeDeterminators;

import com.rdebokx.ltga.config.JobConfiguration;

public class PopSizeDeterminator {
    
    protected final static int SEARCHES = 10;
    protected final static int RUNS = 100;
    
    /**
     * This function prints the statistics of the current PopSizeDeterminator in the format Pop avg | Pop var | Pop worst | Pop 10% | Pop 50% | Pop 90% | Pop best |
     * Eval avg | Eval var | Eval worst | Eval 10% | Eval 50% | Eval 90% | Eval best
     * @param determinator The type of PopSizeDeterminator that was executed.
     * @param config The configuration of the job that was executed, used for outputting the amount of parameters that were considered.
     * @param popSizes Sorted array containing the found population sizes.
     * @param evaluations Sorted array containing the found evaluation statistics.
     */
    public static void printStatistics(String determinator, JobConfiguration config, int[] popSizes, int[] evaluations){
        System.out.println(determinator + " finished for " + config.PROBLEM_CONFIG.PROBLEM + " with l=" + config.GENETIC_CONFIG.NUMBER_OF_PARAMETERS);
        System.out.println("Printing: Pop avg | Pop var | Pop worst | Pop 10% | Pop 50% | Pop 90% | Pop best | "
                + "Eval avg | Eval var | Eval worst | Eval 10% | Eval 50% | Eval 90% | Eval best");
        
        //Pop size statistics
        int popSum = 0;
        for(int value : popSizes){
            popSum += value;
        }
        double popAverage = popSum / (popSizes.length * 1.0);
        
        double popVarSum = 0;
        for(int value : popSizes){
            popVarSum += (value - popAverage) * (value - popAverage);
        }
        double popVar = (popVarSum / (popSizes.length * 1.0));
        
        //Evaluations statistics
        double evalSum = 0;
        for(double value : evaluations){
            evalSum += value;
        }
        double evalAverage = evalSum / (evaluations.length * 1.0);
        double evalVarSum = 0;
        for(double value : evaluations){
            evalVarSum += (value - evalAverage) * (value - evalAverage);
        }
        double evalVar = evalVarSum / (evaluations.length * 1.0);
        
        double evalMin = evaluations[0];
        double eval10 = evaluations.length == 10 ? evaluations[1] : evaluations[9];
        double eval50 = evaluations.length == 10 ? evaluations[4] : evaluations[49];
        double eval90 = evaluations.length == 10 ? evaluations[8] : evaluations[89];
        double evalMax = evaluations.length == 10 ? evaluations[9] : evaluations[99];
        
        System.out.println(popAverage + "\t" + popVar + "\t" + popSizes[0] + "\t" + popSizes[1] + "\t" + popSizes[4] + "\t" + popSizes[8] + "\t" + popSizes[9] +
                "\t" + evalAverage + "\t" + evalVar + "\t" + evalMin + "\t" + eval10 + "\t" + eval50 +
                "\t" + eval90 + "\t" + evalMax);
        
    }
}
