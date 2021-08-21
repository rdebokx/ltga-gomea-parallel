package com.rdebokx.ltga.sequential;

import static org.junit.Assert.assertEquals;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Randomizer;

import org.junit.Before;
import org.junit.Test;

public class MIMatrixTest {

    private double entropy(double frequency){
        return -frequency * Math.log(frequency) / Math.log(2); 
    }
    
    @Test
    public void testConstructor() {
        final int numberOfParameters = 3;
        boolean[][] pop = {
            {true, true, false},
            {true, true, true},
            {false, true, false},
            {false, true, true},
            {false, false, true}
        };
        
        //Similarity values
        double s00 = entropy(3.0/5.0) + entropy(2.0/5.0);
        double s01 = entropy(1.0/5.0) + 2*entropy(2.0/5.0);
        double s02 = 3*entropy(1.0/5.0) + entropy(2.0/5.0);
        
        double s10 = s01;
        double s11 = entropy(1.0/5.0) + entropy(4.0/5.0);
        double s12 = entropy(1.0/5.0) + 2*entropy(2.0/5.0);
        
        double s20 = s02;
        double s21 = s12;
        double s22 = entropy(2.0/5.0) + entropy(3.0/5.0);
        
        //MI values
        double mi00 = s00;
        double mi01 = s00 + s11 - s01;
        double mi02 = s00 + s22 - s02;
        
        double mi10 = mi01;
        double mi11 = s11;
        double mi12 = s11 + s22 - s12;
        
        double mi20 = mi02;
        double mi21 = mi12;
        double mi22 = s22;
        
        //Check values
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        
        //initialize mpm, LT and SMatrix
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        assertEquals(numberOfParameters, mpm.length);
        MIMatrix matrix = new MIMatrix(mpm, population);
        
        assertEquals(mi00, matrix.get(0, 0), .0001);
        assertEquals(mi01, matrix.get(0, 1), .0001);
        assertEquals(mi02, matrix.get(0, 2), .0001);
        
        assertEquals(mi10, matrix.get(1, 0), .0001);
        assertEquals(mi11, matrix.get(1, 1), .0001);
        assertEquals(mi12, matrix.get(1, 2), .0001);
        
        assertEquals(mi20, matrix.get(2, 0), .0001);
        assertEquals(mi21, matrix.get(2, 1), .0001);
        assertEquals(mi22, matrix.get(2, 2), .0001);
    }
    
    @Test
    public void testUpdateSMatrix(){
        final int numberOfParameters = 3;
        boolean[][] pop = {
            {true, true, false},
            {true, true, true},
            {false, true, false},
            {false, true, true},
            {false, false, true}
        };
        
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        assertEquals(numberOfParameters, mpm.length);
        MIMatrix matrix = new MIMatrix(mpm, population);
        double mi00 = matrix.get(0, 0);
        double mi01 = matrix.get(0, 1);
        double mi02 = matrix.get(0, 2);
        
        double mi10 = matrix.get(0, 1);
        double mi11 = matrix.get(1, 1);
        double mi12 = matrix.get(1, 2);
        
        double mi20 = matrix.get(2, 0);
        double mi21 = matrix.get(2, 1);
        double mi22 = matrix.get(2, 2);
        
        matrix.updateSMatrix(mpm[0], mpm[1]);
        
        double mul0 = mpm[0].size() / ((mpm[0].size() + mpm[1].size()) * 1.0);
        double mul1 = mpm[1].size() / ((mpm[0].size() + mpm[1].size()) * 1.0);
        double newVal = mul0 * mi20 + mul1 * mi21;
        assertEquals(mi00, matrix.get(0, 0), .0001);
        assertEquals(mi01, matrix.get(0, 1), .0001);
        assertEquals(newVal, matrix.get(0, 2), .0001); //this is changed
        
        assertEquals(mi10, matrix.get(1, 0), .0001);
        assertEquals(mi11, matrix.get(1, 1), .0001);
        assertEquals(mi12, matrix.get(1, 2), .0001);
        
        assertEquals(newVal, matrix.get(2, 0), .0001); //this is changed
        assertEquals(mi21, matrix.get(2, 1), .0001);
        assertEquals(mi22, matrix.get(2, 2), .0001);
    }

}
