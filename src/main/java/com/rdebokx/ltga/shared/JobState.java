package com.rdebokx.ltga.shared;

public class JobState {

    private int noImprovementStretch;
    private int numberOfGenerations;
    private int numberOfEvaluations;
    
    /**
     * Set the noImprovementStretch for this jobState.
     * @param noImprovementStretch The noImprovementStretch for this jobState.
     */
    public void setNoImprovementStretch(int noImprovementStretch){
        this.noImprovementStretch = noImprovementStretch;
    }
    
    /**
     * This method increments the current noImprovementStretch.
     */
    public void incrementNoImprovementStretch(){
        noImprovementStretch++;
    }
    
    /**
     * This function increments the amount of generations that are tracked by this jobState.
     */
    public void incrementNumberOfGenerations(){
        numberOfGenerations++;
    }
    
    /**
     * This function increments the amount of evaluations that are tracked by this jobState.
     */
    public synchronized void incrementNumberOfEvaluations(){
        numberOfEvaluations++;
    }
    
    /**
     * This function increments the amount of evaluations that are tracked by this jobState with the given amount.
     * @param evaluations The amount of evaluations that the numberOfEvaluations should be incremented with.
     */
    public synchronized void incrementNumberOfEvaluations(int evaluations){
        numberOfEvaluations += evaluations;
    }
    
    /**
     * @return The current noImprovementStretch.
     */
    public int getNoImprovementStretch(){
        return noImprovementStretch;
    }
    
    /**
     * @return The current number of generations.
     */
    public int getNumberOfGenerations(){
        return numberOfGenerations;
    }
    
    /**
     * @return The current number of evaluations.
     */
    public int getNumberOfEvaluations(){
        return numberOfEvaluations;
    }
}
