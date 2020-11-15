package com.rdebokx.ltga.shared;

public class FitnessComparator {

    /**
     * This function returns true iff x is better than y.
     * x is not better than y unless:
     * - x and y are both infeasible and x has a smaller sum of constraint violations, or
     * - x is feasible and y is not, or
     * - x and y are both feasible and x has a larger objective value than y
     * 
     * @param objectiveX The objective value of x
     * @param constraintX The constraint value of x
     * @param objectiveY The objective value of y
     * @param constraintY The constraint value of y
     * @return
     */
    public static boolean betterFitness(double objectiveX, double constraintX, double objectiveY, double constraintY){
        boolean result = false;
        
        if(constraintX > 0) {
            //x if infeasible
            result = constraintY > 0 && constraintX <= constraintY; //both are infeasible and x has a smaller sum of constraint violations
        } else {
            //x is feasible
            result = constraintY > 0 || objectiveX > objectiveY; //y is not feasible, or x has a larger objective value
        }
        return result;
    }
    
    /**
     * This function returns true iff the fitness for the given input is equal.
     * In other words: this is a wrapper function for constraintX == constraintY && objectiveX == objectiveY; 
     * @param objectiveX The objective value of X.
     * @param constraintX The constraint value of X.
     * @param objectiveY The objective value of Y.
     * @param constraintY The constraint value of Y.
     * @return True iff the given objective values and constraint values are equal.
     */
    public static boolean equalFitness(double objectiveX, double constraintX, double objectiveY, double constraintY){
        return constraintX == constraintY && objectiveX == objectiveY;
    }
}
