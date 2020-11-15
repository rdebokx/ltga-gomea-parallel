package com.rdebokx.ltga.shared;

import java.util.Arrays;

public abstract class MIMatrix {
    
    protected final double[][]matrix;
    
    /**
     * Private constructor, used to clone this MIMatrix.
     * @param numberOfParameters The number of parameters for this MIMatrix
     */
    public MIMatrix(int numberOfParameters){
        this.matrix = new double[numberOfParameters][numberOfParameters];
    }
    
    /**
     * Return the value in the matrix at position i,j.
     * @param i Row
     * @param j Column
     * @return The value at the specified location.
     */
    public double get(int i, int j){
        return matrix[i][j];
    }
    
    /**
     * This function returns row i from the matrix.
     * @param i The index of the row that has to be returned.
     * @return Row i.
     */
    public double[] get(int i){
        return matrix[i];
    }
    
    /**
     * This function sets a value at the given position. Note that other values in the matrix are not updated.
     * @param i Row
     * @param j Column
     * @param value The new value for the specified location.
     */
    public void set(int i, int j, double value){
        matrix[i][j] = value;
    }
    
    /**
     * @return A string representation of this Mutual Information Matrix.
     */
    public String toString(){
        String result = "";
        for(double[] row : matrix){
            result += Arrays.toString(row) + "\n";
        }
        return result;
    }
    
    /**
     * This function returns true iff the given object is a MIMatrix with the same contents as this MIMatrix.
     * @param obj The object that should be checked for equality to this MIMatrix.
     * @return true iff the given object is an MIMatrix with the same contents as this MIMatrix.
     */
    @Override
    public boolean equals(Object obj){
        boolean result = true;
        if(obj instanceof MIMatrix){
            MIMatrix that = (MIMatrix) obj;
            result = this.matrix.length == that.matrix.length;
            int i = 0;
            while(result && i < matrix.length){
                result &= Arrays.equals(this.matrix[i], that.matrix[i]);
                i++;
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * This function returns a new MIMatrix with the same contents as this MIMatrix.
     * @return A copy of this MIMatrix.
     */
    @Override
    public abstract MIMatrix clone();
    
}
