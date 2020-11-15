package com.rdebokx.ltga.parallel.workers;

import java.util.ArrayList;

import com.rdebokx.ltga.parallel.Population;
import com.rdebokx.ltga.shared.Distribution;
import com.rdebokx.ltga.shared.Pair;
import com.rdebokx.ltga.shared.ParameterSet;

public class EntropyWorker implements Runnable{
    
    private double[][] matrix;
    private Population population;
    private ArrayList<Pair<ParameterSet>> tasks;
    
    
    /**
     * Constructor, constructing an EntropyWorker that calculates the entropy values for the given matrix in the given range. 
     * @param matrix The matrix into which the entropy values should be inserted.
     * @param mpm The Marginal Product Model with parameterSets;
     * @param population The population which should be used to calculate the entropy values.
     * @param iStart The starting value of i (inclusive).
     * @param jStart The starting value of j (inclusive).
     * @param iEnd The end value of i (inclusive).
     * @param jEnd The end value of j (inclusive).
     */
    public EntropyWorker(double[][] matrix, Population population, ArrayList<Pair<ParameterSet>> tasks){
        this.matrix = matrix;
        this.population = population;
        this.tasks = tasks;
    }
    
    @Override
    public void run(){
        for(Pair<ParameterSet> task : tasks){
            ParameterSet paramSet1 = task.getVal1();
            ParameterSet paramSet2 = task.getVal2();
            Distribution dist = new Distribution(population, paramSet1.iterator().next(), paramSet2.iterator().next());
            matrix[paramSet1.getIndex()][paramSet2.getIndex()] = dist.getJointEntropy();
            matrix[paramSet2.getIndex()][paramSet1.getIndex()] = matrix[paramSet1.getIndex()][paramSet2.getIndex()];
        }
    }
    
}
