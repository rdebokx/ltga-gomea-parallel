package com.rdebokx.ltga.sequential;

import java.util.Arrays;

import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.shared.Distribution;
import com.rdebokx.ltga.shared.ParameterSet;

public class MIMatrix extends com.rdebokx.ltga.shared.MIMatrix {
    
    /**
     * Constructor, construcitng an Mutual Information Matrix based on the given Marginal Product Model and the given population.
     * @param population The population for which the Mutual Information Matrix has to be constructed.
     */
    public MIMatrix(ParameterSet[] mpm, Population population){
        super(population.getNumberOfParameters());
        constructMIMatrix(mpm, population);
    }
    
    /**
     * Private constructor, used to clone this MIMatrix.
     * @param numberOfParameters The number of parameters for this MIMatrix
     */
    public MIMatrix(int numberOfParameters){
        super(numberOfParameters);
    }
    
    /**
     * This is an internal method that calculates the values of the matrix, based on the given population
     * @param population The population for which the Mutual Information Matrix has to be constructed.
     */
    private void constructMIMatrix(ParameterSet[] mpm, Population population){
        long timeStart = System.currentTimeMillis();

        /*
         * Fill with entropy values.
         * Note that this seems to differ from the implementation in c, where the cumulative chances are calculated and after that
         * transformed back to individual chances, which are then used to calculate the joint entropy. This is equal to our implementation
         * of calculating the joint entropy in our Distribution class.
         */
        
        //long entropyTimeStart = System.currentTimeMillis();
        for(int i = 0; i < population.getNumberOfParameters(); i++){
            ParameterSet paramSet1 = mpm[i];
            int param1 = paramSet1.iterator().next();
            for(int j = i + 1; j < mpm.length; j++){
                ParameterSet paramSet2 = mpm[j];
                Distribution dist = new Distribution(population, param1, paramSet2.iterator().next());
                matrix[paramSet1.getIndex()][paramSet2.getIndex()] = dist.getJointEntropy();
                matrix[paramSet2.getIndex()][paramSet1.getIndex()] = matrix[paramSet1.getIndex()][paramSet2.getIndex()];
            }
            
            Distribution distI = new Distribution(population, param1, param1);
            matrix[paramSet1.getIndex()][paramSet1.getIndex()] = distI.getJointEntropy();
        }
        //long entropyTimeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("MIMatrix.constructMIMatrix - entropyValues", entropyTimeEnd - entropyTimeStart);
        
        //calculate MI values
        //long miTimeStart = System.currentTimeMillis();
        for(int i = 0; i < population.getNumberOfParameters(); i++){
            for(int j = i + 1; j < population.getNumberOfParameters(); j++){
                matrix[i][j] = matrix[i][i] + matrix[j][j] - matrix[i][j];
                matrix[j][i] = matrix[i][j];
            }
        }
        //long miTimeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("MIMatrix.constructMIMatrix - MIValues", miTimeEnd - miTimeStart);
        
        long timeEnd = System.currentTimeMillis();
        Profiler.recordExecution("MIMatrix.constructMIMatrix", timeEnd - timeStart);
    }
    
    public void updateSMatrix(ParameterSet r0, ParameterSet r1){
        //long timeStart = System.currentTimeMillis();
        
        double mul0 = r0.size() / ((r0.size() + r1.size()) * 1.0);
        double mul1 = r1.size() / ((r0.size() + r1.size()) * 1.0);
        
        for(int i = 0; i < matrix.length; i++){
            if(i != r0.getIndex() && i != r1.getIndex()){
                matrix[i][r0.getIndex()] = mul0 * matrix[i][r0.getIndex()] + mul1 * matrix[i][r1.getIndex()];
                matrix[r0.getIndex()][i] = matrix[i][r0.getIndex()];
            }
        }
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("MIMatrix.updateSMatrix", timeEnd - timeStart);
    }
    

    /**
     * This function returns a new MIMatrix with the same contents as this MIMatrix.
     * @return A copy of this MIMatrix.
     */
    @Override
    public MIMatrix clone(){
        MIMatrix out = new MIMatrix(matrix.length);
        for(int i = 0; i < matrix.length; i++){
            out.matrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        
        return out;
    }
}
