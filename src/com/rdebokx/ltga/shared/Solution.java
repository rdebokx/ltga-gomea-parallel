package com.rdebokx.ltga.shared;

public class Solution {
    
    private final boolean[] solution;
    private double objectiveValue;
    private double constraintValue;
    
    /**
     * Constructor, constructing a new Solution based on the given values.
     * @param solution The solution as a Boolean array.
     * @param objectiveValue The objective value of this solution.
     * @param constraintValue The constraint value of this solution.
     */
    public Solution(boolean[] solution, double objectiveValue, double constraintValue) {
        this.solution = solution;
        this.objectiveValue = objectiveValue;
        this.constraintValue = constraintValue;
    }

    /**
     * @return The solution as a boolean array.
     */
    public boolean[] getSolution() {
        return solution;
    }

    /**
     * @return The objective value of this solution.
     */
    public double getObjectiveValue() {
        return objectiveValue;
    }

    /**
     * @return The constraint value of this solution.
     */
    public double getConstraintValue() {
        return constraintValue;
    }

    /**
     * Update the objective value of this solution.
     * @param objectiveValue The objective value to be set.
     */
    public void setObjectiveValue(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    /**
     * Update the constraint value of this solution.
     * @param constraintValue The constraint value to be set.
     */
    public void setConstraintValue(double constraintValue) {
        this.constraintValue = constraintValue;
    }
    
    @Override
    public String toString(){
        String result = "Solution:\n\tobjectiveValue: " + objectiveValue + "\n\tconstraintValue: " + constraintValue + "\n\tsolution: {";
        result += getStringRepresentation(solution) + "}";
        return result;
    }
    
    /**
     * This function returns a String representation of a given solution, basically transforming booleans to zeroes and ones.
     * @param solution The solution that has to be tranformed.
     * @return The string representation of the given solution.
     */
    public static String getStringRepresentation(boolean[] solution){
        String result = "";
        if(solution == null){
        	result = "null";
        } else {
	        for(boolean value : solution){
	            result += (value ? "1" : "0");
	        }
        }
        return result;
    }
    
    /**
     * This function returns a boolean array that was parsed from the given bitstring.
     * @param solution The bitstring that has to be transformed to a boolean array
     * @return The parsed boolean array.
     */
    public static boolean[] parseSolution(String solution){
        boolean[] result = new boolean[solution.length()];
        for(int i = 0; i < solution.length(); i++){
            result[i] = Integer.parseInt(solution.substring(i, i+1)) == 1;
        }
        return result;
    }
}
