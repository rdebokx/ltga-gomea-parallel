package com.rdebokx.ltga.sequential;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.NotRandom;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Randomizer;

import org.junit.Before;
import org.junit.Test;

public class LinkageTreeTest {
    
    /**
     * Constructor and learnStructure are very hard to test because of stochastic behavior. Checked by hand.
     */
    @Test
    public void testLearnStructure(){
    	ExecutionConfiguration execConfig = new ExecutionConfiguration(1, -1, -1, false, -1, -1);
        //set order in LT by setting randomizer
        NotRandom generator = new NotRandom();
        int[] sequence = {
            1, 0, 1, 0, //shuffling indices in order to get [2, 3, 0, 1]
            0, 0 //index of first element to be added to nnChain
        };
        generator.setSequence(sequence);
        Randomizer randomizer = new Randomizer(generator);
        
        //final int numberOfParameters = 4;
        boolean[][] pop = {
            {true, true, false, false},
            {true, true, true, true},
            {false, true, false, false},
            {false, true, true, true},
            {false, false, true, true}
        };
        
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        LinkageTree lt = new LinkageTree(population, randomizer, execConfig);
        //Linkage tree should consist of {(2), (3), (0), (1), (2,3), (0, 1)}
        
        //System.out.println("LT: " + lt);
        assertEquals(6, lt.size());
        ParameterSet paramSet = lt.get(0);
        assertEquals(1, paramSet.size());
        Object[] params = paramSet.toArray();
        assertEquals(new Integer(2), (Integer) params[0]);
        
        paramSet = lt.get(1);
        assertEquals(1, paramSet.size());
        params = paramSet.toArray();
        assertEquals(new Integer(3), (Integer) params[0]);
        
        paramSet = lt.get(2);
        assertEquals(1, paramSet.size());
        params = paramSet.toArray();
        assertEquals(new Integer(0), (Integer) params[0]);
        
        paramSet = lt.get(3);
        assertEquals(1, paramSet.size());
        params = paramSet.toArray();
        assertEquals(new Integer(1), (Integer) params[0]);
        
        paramSet = lt.get(4);
        assertEquals(2, paramSet.size());
        ArrayList<Integer> expected = new ArrayList<Integer>();
        expected.add(2);
        expected.add(3);
        ParameterSet expParamSet = new ParameterSet(-1, expected);
        assertEquals(expParamSet, paramSet);
        
        paramSet = lt.get(5);
        assertEquals(2, paramSet.size());
        expected.clear();
        expected.add(0);
        expected.add(1);
        expParamSet = new ParameterSet(-1, expected);
        assertEquals(expParamSet, paramSet);
    }
    
    @Test
    public void testLearnStructureLoop(){
    	ExecutionConfiguration execConfig = new ExecutionConfiguration(1, -1, -1, false, -1, -1);
        //set order in LT by setting randomizer
        NotRandom generator = new NotRandom();
        int[] sequence = {
            1, 2, 2, 3, 1, 1, 1, 1, //shuffling indices in order to get [0, 2, 3, 1]
            0, 0 //index of first element to be added to nnChain
        };
        generator.setSequence(sequence);
        Randomizer randomizer = new Randomizer(generator);
        
        //final int numberOfParameters = 4;
        boolean[][] pop = {
            {true, true, false, false},
            {true, true, true, true},
            {false, true, false, false},
            {false, true, true, true},
            {false, false, true, true}
        };
        
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        LinkageTree lt = new LinkageTree(population, randomizer, execConfig);
        //Linkage tree should consist of {(0), (2), (3), (1), (0, 1), (2, 3)}
        
        //System.out.println("LT: " + lt);
        assertEquals(6, lt.size());
        
        ParameterSet paramSet = lt.get(0);
        assertEquals(1, paramSet.size());
        Object[] params = paramSet.toArray();
        assertEquals(new Integer(0), (Integer) params[0]);
        
        paramSet = lt.get(1);
        assertEquals(1, paramSet.size());
        params = paramSet.toArray();
        assertEquals(new Integer(2), (Integer) params[0]);
        
        paramSet = lt.get(2);
        assertEquals(1, paramSet.size());
        params = paramSet.toArray();
        assertEquals(new Integer(3), (Integer) params[0]);
        
        paramSet = lt.get(3);
        assertEquals(1, paramSet.size());
        params = paramSet.toArray();
        assertEquals(new Integer(1), (Integer) params[0]);
        
        paramSet = lt.get(4);
        assertEquals(2, paramSet.size());
        ArrayList<Integer> expected = new ArrayList<Integer>();
        expected.add(0);
        expected.add(1);
        ParameterSet expParamSet = new ParameterSet(-1, expected);
        assertEquals(expParamSet, paramSet);
        
        paramSet = lt.get(5);
        assertEquals(2, paramSet.size());
        expected.clear();
        expected.add(2);
        expected.add(3);
        expParamSet = new ParameterSet(-1, expected);
        assertEquals(expParamSet, paramSet);
    }
    
    @Test
    public void testConstructNewMpm() {
        final int numberOfParameters = 4;
        boolean[][] pop = {
            {true, true, false, false},
            {true, true, true, true},
            {false, true, false, false},
            {false, true, true, true},
            {false, false, true, true}
        };
        
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        assertEquals(numberOfParameters, mpm.length);
        MIMatrix matrix = new MIMatrix(mpm, population);
        matrix.updateSMatrix(mpm[0], mpm[1]);
        
        ParameterSet r0 = mpm[0];
        ParameterSet r1 = mpm[1];
        ParameterSet newSet = new ParameterSet(-1, r0, r1);
        
        //construct expected new MIMatrix
        MIMatrix oldMatrix = matrix.clone();
        
        /*
         * This function should do the following:
         * - Copy the existing mpm
         * - newMPM[p0.index] = combined set
         * - newMPM[p1.index] = last element of old mpm (if p1 wasn't last element already)
         * - SMatrix will be updated
         * TODO: investigate next two statements. p0 and p1 can only occur in the last 3 elements? As these are removed, it is not necessary to replace them?
         * - if p0 was in the nnChain, this should be replaced by the combined ParameterSet?
         * - if p1 was in the nnChain, this should be replaced by the last ParameterSet of the mpm?
         */
        
        ParameterSet[] newMpm = LinkageTree.constructNewMpm(mpm, matrix, r0, r1, newSet);
        
        //check if r0 and r1 are correctly replaced.
        assertEquals(mpm.length - 1, newMpm.length);
        assertEquals(newSet, newMpm[0]);
        assertEquals(0, newSet.getIndex());
        assertEquals(mpm[mpm.length - 1], newMpm[1]);
        assertEquals(1, newMpm[1].getIndex());
        
        //Check SMatrix
        assertEquals(oldMatrix.get(0, 0), matrix.get(0, 0), .0001);
        assertEquals(oldMatrix.get(0, 3), matrix.get(0, 1), .0001);
        assertEquals(oldMatrix.get(0, 2), matrix.get(0, 2), .0001);
        
        assertEquals(oldMatrix.get(3, 0), matrix.get(1, 0), .0001);
        assertEquals(oldMatrix.get(3, 3), matrix.get(1, 1), .0001);
        assertEquals(oldMatrix.get(3, 2), matrix.get(1, 2), .0001);
        
        assertEquals(oldMatrix.get(2, 0), matrix.get(2, 0), .0001);
        assertEquals(oldMatrix.get(2, 3), matrix.get(2, 1), .0001);
        assertEquals(oldMatrix.get(2, 2), matrix.get(2, 2), .0001);
    }

    @Test
    public void testConstructNewMpmLast() {
        final int numberOfParameters = 4;
        boolean[][] pop = {
            {true, true, false, false},
            {true, true, true, true},
            {false, true, false, false},
            {false, true, true, true},
            {false, false, true, true}
        };
        
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        assertEquals(numberOfParameters, mpm.length);
        MIMatrix matrix = new MIMatrix(mpm, population);
        matrix.updateSMatrix(mpm[0], mpm[1]);
        
        ParameterSet r0 = mpm[0];
        ParameterSet r1 = mpm[3];
        ParameterSet newSet = new ParameterSet(-1, r0, r1);
        
        //construct expected new MIMatrix
        MIMatrix oldMatrix = matrix.clone();
        
        /*
         * This function should do the following:
         * - Copy the existing mpm
         * - newMPM[p0.index] = combined set
         * - newMPM[p1.index] = last element of old mpm (if p1 wasn't last element already)
         * - SMatrix will be updated
         * TODO: investigate next two statements. p0 and p1 can only occur in the last 3 elements? As these are removed, it is not necessary to replace them?
         * - if p0 was in the nnChain, this should be replaced by the combined ParameterSet?
         * - if p1 was in the nnChain, this should be replaced by the last ParameterSet of the mpm?
         */
        ParameterSet[] newMpm = LinkageTree.constructNewMpm(mpm, matrix, r0, r1, newSet);
        
        //check if r0 and r1 are correctly replaced.
        assertEquals(mpm.length - 1, newMpm.length);
        assertEquals(newSet, newMpm[0]);
        assertEquals(0, newSet.getIndex());
        assertEquals(mpm[1], newMpm[1]);
        assertEquals(1, newMpm[1].getIndex());
        
        assertEquals(oldMatrix, matrix);
    }
}
