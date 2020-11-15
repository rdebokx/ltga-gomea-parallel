package com.rdebokx.ltga.shared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class FitnessComparatorTest {
    
    @Test
    public void testBetterFitness(){
        
        /** Tests with X being infeasible **/
        //Y feasible: false
        assertFalse(FitnessComparator.betterFitness(10, 1, 1, 0));
        //Y infeasible and smaller constraint violation: false
        assertFalse(FitnessComparator.betterFitness(1, .4, .8, .3));
        //Y infeasible and larger constraint violation: true
        assertTrue(FitnessComparator.betterFitness(.8, .1, .8, .3));
        
        /** Tests with X being feasbie **/
        //Y infeasible: true
        assertTrue(FitnessComparator.betterFitness(.1, 0, .8, .05));
        //Y feasible and smaller objective value: true
        assertTrue(FitnessComparator.betterFitness(.1, 0, .05, 0));
        //Y feasible and larger objective value: false
        assertFalse(FitnessComparator.betterFitness(.1, 0, .5, 0));
    }
    
    @Test
    public void testEqualFitness(){
        assertTrue(FitnessComparator.equalFitness(0, 0, 0, 0));
        assertTrue(FitnessComparator.equalFitness(-1, -2, -1, -2));
        assertTrue(FitnessComparator.equalFitness(42, 0, 42, 0));
        
        assertFalse(FitnessComparator.equalFitness(0, 1, 0, 0));
        assertFalse(FitnessComparator.equalFitness(1, 0, 0, 0));
        assertFalse(FitnessComparator.equalFitness(0, 0, 42, 0));
    }
    
}
