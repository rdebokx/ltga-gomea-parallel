package com.rdebokx.ltga.shared;

public class MaxCutEvaluationFunction implements EvaluationFunction {

    private final int numberOfVertices;
    private final int[][] weights;
    
    /**
     * Constructor, constructing a new MaxCutEvaluationFunction.
     * @param numberOfVertices The amount of vertices for the given problem.
     * @param weights The lookup table with weights. Not that this is 0-based.
     */
    public MaxCutEvaluationFunction(int numberOfVertices, int[][] weights) {
        this.numberOfVertices = numberOfVertices;
        this.weights = weights;
    }
    
    @Override
    public double evaluate(boolean[] solution) {
        double total = 0;
        for(int i = 0; i < numberOfVertices; i++){
            total += calculateForEdgesFor(i, solution);
        }
        
        return total;
    }
    
    /**
     * @return The weights table represented by this MaxCutEvaluationFunction
     */
    public int[][] getWeights(){
        return weights;
    }

    /**
     * This function calculates the total weight of cut edges connected to i.
     * @param i The index of the vertex that is considered.
     * @param solution The solution for which the cut edges for the given i are to be determined.
     * @return The sum of the cut edges of i.
     */
    private int calculateForEdgesFor(int i, boolean[] solution){
        int result = 0;
        for(int j = i + 1; j < numberOfVertices; j++){
            if(solution[i] != solution [j]){
                result += weights[i][j];
            }
        }
        return result;
    }
}
