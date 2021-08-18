package com.rdebokx.ltga.shared;

import com.rdebokx.ltga.sequential.Population;

public class Distribution {

    private final int populationSize;
    private final int[][] frequencies = new int[2][2];
    private final int param1;
    private final int param2;
    
    /**
     * Constructor, constructing a new Distribution for the given population, with regard to the given parameters.
     * Frequencies for these parameters will be established, which give the basis for calculating the joint entropy.
     * @param dataset The population for which a distribution has to be constructed.
     * @param param1 The index of the first parameter of this distribution.
     * @param param2 The index of the second parameter of this distribution.
     */
    public Distribution(Population dataset, int param1, int param2){
        this.param1 = param1;
        this.param2 = param2;
        
        populationSize = dataset.getPopulationSize();
        for(boolean[] solution : dataset.getPopulation()){
            updateFrequencies(solution);
        }
    }
    
    /**
     * Constructor, constructing a new Distribution for the given population, with regard to the given parameters.
     * Frequencies for these parameters will be established, which give the basis for calculating the joint entropy.
     * @param dataset The population for which a distribution has to be constructed.
     * @param param1 The index of the first parameter of this distribution.
     * @param param2 The index of the second parameter of this distribution.
     */
    public Distribution(com.rdebokx.ltga.parallel.Population dataset, int param1, int param2){
        this.param1 = param1;
        this.param2 = param2;
        
        populationSize = dataset.getPopulationSize();
        for(boolean[] solution : dataset.getPopulation()){
            updateFrequencies(solution);
        }
    }
    
    /**
     * This method updates the frequency records for the given solution, considering the 2 parameters for this distribution.
     * @param solution The solution for which the frequencies for this distribution have to be updated.
     */
    private void updateFrequencies(boolean[] solution){
        int val1 = solution[param1] ? 1 : 0;
        int val2 = solution[param2] ? 1 : 0;
        
        frequencies[val1][val2]++;
    }
    
    /**
     * This function returns the joint entropy of the frequencies table for the parameters in this distribution.
     * @return The joint entropy for the parameters of this distribution.
     */
    public double getJointEntropy(){
        return getEntropy(0, 0) + getEntropy(0, 1) +
            getEntropy(1, 0) + getEntropy(1, 1);
    }
    
    /**
     * This function calculates the entropy for the given values of the parameters of this distribution.
     * @param val1 The value of param1 for calculating the entropy.
     * @param val2 The value of param2 for calculating the entropy.
     * @return The entropy for the given values of the parameters of this distribution.
     */
    private double getEntropy(int val1, int val2){
        double result = 0;
        if(frequencies[val1][val2] > 0){
            double p = frequencies[val1][val2] / (populationSize * 1.0);
            result = -p * Math.log(p) / Math.log(2); 
        }
        return result;
    }
    
    /**
     * @return The raw frequencies table. For testing purposes only.
     */
    public int[][] getFrequencies(){
        return frequencies;
    }
}
