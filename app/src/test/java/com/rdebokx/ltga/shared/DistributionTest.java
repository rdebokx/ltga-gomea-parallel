package com.rdebokx.ltga.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import com.rdebokx.ltga.sequential.Population;

import org.junit.Before;
import org.junit.Test;

public class DistributionTest {
    
    @Test
    public void testFrequencies() {
        JobState js = new JobState();
        //construct population array
        boolean[][] pop = {
            {true, true, true, false},
            {true, true, true, true},
            {false, true, false, false},
            {true, false, true, true}
        };
        double[] objectiveValues = {3, 4, 2, 3};
        double[] constraintValues = {0, 0, 0, 0};
        Population population = new Population(pop, objectiveValues, constraintValues, js);
        
        Distribution dist = new Distribution(population, 0, 1);
        int[][] frequencies = dist.getFrequencies();
        assertEquals(2, frequencies.length);
        assertEquals(2, frequencies[0].length);
        assertTrue(Arrays.equals(new int[]{0, 1}, frequencies[0]));
        assertTrue(Arrays.equals(new int[]{1, 2}, frequencies[1]));
        
        //add datapoint
        pop = new boolean[][]{
            {true, true, true, false},
            {true, true, true, true},
            {false, true, false, false},
            {true, false, true, true},
            {false, false, true, true}
        };
        objectiveValues = new double[]{3, 4, 2, 3, 2};
        constraintValues = new double[]{0, 0, 0, 0, 0};
        population = new Population(pop, objectiveValues, constraintValues, js);
        dist = new Distribution(population, 0, 1);
        
        frequencies = dist.getFrequencies();
        assertEquals(2, frequencies.length);
        assertEquals(2, frequencies[0].length);
        assertTrue(Arrays.equals(new int[]{1, 1}, frequencies[0]));
        assertTrue(Arrays.equals(new int[]{1, 2}, frequencies[1]));
        
        //add datapoint
        pop = new boolean[][]{
            {true, true, true, false},
            {true, true, true, true},
            {false, true, false, false},
            {true, false, true, true},
            {false, false, true, true},
            {true, true, false, false}
        };
        objectiveValues = new double[]{3, 4, 2, 3, 2, 2};
        constraintValues = new double[]{0, 0, 0, 0, 0, 0};
        population = new Population(pop, objectiveValues, constraintValues, js);
        dist = new Distribution(population, 0, 1);
        
        frequencies = dist.getFrequencies();
        assertEquals(2, frequencies.length);
        assertEquals(2, frequencies[0].length);
        assertTrue(Arrays.equals(new int[]{1, 1}, frequencies[0]));
        assertTrue(Arrays.equals(new int[]{1, 3}, frequencies[1]));
    }
    
    @Test
    public void testGetJointEntropy(){
        JobState js = new JobState();
        //construct population array
        boolean[][] pop = {
            {true, true, true, false},
            {true, true, true, true},
            {false, true, false, false},
            {true, true, true, true}
        };
        double[] objectiveValues = {3, 4, 2, 3};
        double[] constraintValues = {0, 0, 0, 0};
        Population population = new Population(pop, objectiveValues, constraintValues, js);
        Distribution dist = new Distribution(population, 0, 1);
        
        //entropy H(0, 0) = 0
        //entropy H(0, 1) = -1/4 * log2(1/4) = 0.5
        //entropy H(1, 0) = 0
        //entropy H(1, 1) = -3/4 * log2(3/4) = 0.3112781244591328639096957920391376184301391942306392
        assertEquals(0.8112781244591328639096957920391376184301391942306392, dist.getJointEntropy(), .0001);
        
        pop = new boolean[][]{
            {true, true, true, false},
            {true, true, true, true},
            {false, true, false, false},
            {true, true, true, true},
            {true, false, false, false}
        };
        objectiveValues = new double[]{3, 4, 2, 3, 1};
        constraintValues = new double[]{0, 0, 0, 0, 1};
        population = new Population(pop, objectiveValues, constraintValues, js);
        dist = new Distribution(population, 0, 1);
        
        //entropy H(0, 0) = 0
        //entropy H(0, 1) = -1/5 * log2(1/5) = 0.464385618977472469574063885897878035172966278604916122410951
        //entropy H(1, 0) = -1/5 * log2(1/5) = 0.464385618977472469574063885897878035172966278604916122410951
        //entropy H(1, 1) = -3/5 * log2(3/5) = 0.442179356499723699849948291324944200263010191199259730959402
        double je = 0.464385618977472469574063885897878035172966278604916122410951 + 0.464385618977472469574063885897878035172966278604916122410951 + 0.442179356499723699849948291324944200263010191199259730959402;
        assertEquals(je, dist.getJointEntropy(), .0001);
    }
    
}
