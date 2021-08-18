package com.rdebokx.ltga.sequential;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import com.rdebokx.ltga.shared.JobState;
import com.rdebokx.ltga.shared.NearestNeighborChain;
import com.rdebokx.ltga.shared.Pair;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Randomizer;

import org.junit.Before;
import org.junit.Test;

public class NearestNeighborChainTest {

    @Test
    public void testGetNearestNeighbor() {
        final int numberOfParameters = 3;
        boolean[][] pop = {
            {true, true, false},
            {true, true, true},
            {false, true, false},
            {false, true, true},
            {false, false, true}
        };
        
        //Check values
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        
        //initialize mpm, LT and SMatrix
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        MIMatrix matrix = new MIMatrix(mpm, population);
        
        NearestNeighborChain nnChain = new NearestNeighborChain(numberOfParameters);
        ParameterSet nn = nnChain.getNearestNeighbor(mpm[0], matrix, mpm);
        assertEquals(1, nn.getIndex());
        
        matrix.set(1, 0, .1);
        matrix.set(0, 1, .1);
        matrix.set(2, 0, .5);
        matrix.set(0, 2, .5);
        nn = nnChain.getNearestNeighbor(mpm[0], matrix, mpm);
        assertEquals(2, nn.getIndex());
    }
    
    @Test
    public void testGetNNTuple(){
        final int numberOfParameters = 5;
        boolean[][] pop = {
            {true, true, false, true, false},
            {true, true, true, true, true},
            {false, true, false, true, true},
            {false, true, true, false, false},
            {false, false, true, false, true}
        };
        
        //Check values
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        Randomizer randomizer = new Randomizer();
        
        //initialize mpm, LT and SMatrix
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        MIMatrix matrix = new MIMatrix(mpm, population);
        //System.out.println(matrix);
        
        //test with a long chain
        NearestNeighborChain nnChain = new NearestNeighborChain(numberOfParameters);
        assertEquals(0, nnChain.size());
        nnChain.add(mpm[4]); //This will cause the chain 4->1->3->0->3
        assertEquals(1, nnChain.size());
        Pair<ParameterSet> tuple = nnChain.getNNTuple(matrix, mpm, randomizer);
        assertEquals(2, nnChain.size());
        
        if(tuple.getVal1().getIndex() == 0){
            //val 2 should be index 3
            assertEquals(3, tuple.getVal2().getIndex());
        } else if(tuple.getVal1().getIndex() == 3){
            //val2 should be index 0
            assertEquals(0, tuple.getVal2().getIndex());
        } else {
            fail("Tuple doesn't not consist of index 0 and 3: " + tuple);
        }
        
        
        //test with chain which results in empty chain after removing last 3 elements (use only 3 parameters?
        nnChain = new NearestNeighborChain(numberOfParameters);
        assertEquals(0, nnChain.size());
        nnChain.add(mpm[3]); //This will cause the chain 3->0->3
        assertEquals(1, nnChain.size());
        tuple = nnChain.getNNTuple(matrix, mpm, randomizer);
        assertEquals(0, nnChain.size());
        if(tuple.getVal1().getIndex() == 0){
            //val 2 should be index 3
            assertEquals(3, tuple.getVal2().getIndex());
        } else if(tuple.getVal1().getIndex() == 3){
            //val2 should be index 0
            assertEquals(0, tuple.getVal2().getIndex());
        } else {
            fail("Tuple doesn't not consist of index 0 and 3:" + tuple);
        }
    }

}
