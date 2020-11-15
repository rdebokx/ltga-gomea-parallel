package com.rdebokx.ltga.parallel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import nl.cwi.ltga.parallel.workers.EntropyWorker;
import nl.cwi.ltga.shared.JobState;
import nl.cwi.ltga.shared.Pair;
import nl.cwi.ltga.shared.ParameterSet;

import org.junit.Test;

public class EntropyWorkerTest {
    
    private int numberOfParameters = 3;
    private boolean[][] pop = {
        {true, true, false},
        {true, true, true},
        {false, true, false},
        {false, true, true},
        {false, false, true}
    };
    
    //Similarity values
    private double s00 = MIMatrixTest.entropy(3.0/5.0) + MIMatrixTest.entropy(2.0/5.0);
    private double s01 = MIMatrixTest.entropy(1.0/5.0) + 2*MIMatrixTest.entropy(2.0/5.0);
    private double s02 = 3*MIMatrixTest.entropy(1.0/5.0) + MIMatrixTest.entropy(2.0/5.0);
    
    private double s10 = s01;
    private double s11 = MIMatrixTest.entropy(1.0/5.0) + MIMatrixTest.entropy(4.0/5.0);
    private double s12 = MIMatrixTest.entropy(1.0/5.0) + 2*MIMatrixTest.entropy(2.0/5.0);
    
    private double s20 = s02;
    private double s21 = s12;
    private double s22 = MIMatrixTest.entropy(2.0/5.0) + MIMatrixTest.entropy(3.0/5.0);
    
    @Test
    public void testRunAll() {
        //Check values
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        
        //initialize mpm, LT and SMatrix
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        assertEquals(numberOfParameters, mpm.length);
        
        ArrayList<Pair<ParameterSet>> taskList = new ArrayList<Pair<ParameterSet>>();
        taskList.add(new Pair<ParameterSet>(new ParameterSet(0, 0), new ParameterSet(0, 0)));
        taskList.add(new Pair<ParameterSet>(new ParameterSet(0, 0), new ParameterSet(1, 1)));
        taskList.add(new Pair<ParameterSet>(new ParameterSet(0, 0), new ParameterSet(2, 2)));
        
        taskList.add(new Pair<ParameterSet>(new ParameterSet(1, 1), new ParameterSet(1, 1)));
        taskList.add(new Pair<ParameterSet>(new ParameterSet(1, 1), new ParameterSet(2, 2)));
        
        taskList.add(new Pair<ParameterSet>(new ParameterSet(2, 2), new ParameterSet(2, 2)));
        
        double[][] matrix = new double[numberOfParameters][numberOfParameters];
        EntropyWorker worker = new EntropyWorker(matrix, population, taskList);
        worker.run();
        
        assertEquals(s00, matrix[0][0], .0001);
        assertEquals(s01, matrix[0][1], .0001);
        assertEquals(s02, matrix[0][2], .0001);
        
        assertEquals(s10, matrix[1][0], .0001);
        assertEquals(s11, matrix[1][1], .0001);
        assertEquals(s12, matrix[1][2], .0001);
        
        assertEquals(s20, matrix[2][0], .0001);
        assertEquals(s21, matrix[2][1], .0001);
        assertEquals(s22, matrix[2][2], .0001);
    }
    
    @Test
    public void testRunOne() {
        //Check values
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        
        //initialize mpm, LT and SMatrix
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        assertEquals(numberOfParameters, mpm.length);
        
        ArrayList<Pair<ParameterSet>> taskList = new ArrayList<Pair<ParameterSet>>();
        taskList.add(new Pair<ParameterSet>(new ParameterSet(0, 0), new ParameterSet(1, 1)));
        
        double[][] matrix = new double[numberOfParameters][numberOfParameters];
        EntropyWorker worker = new EntropyWorker(matrix, population, taskList);
        worker.run();
        
        assertEquals(0, matrix[0][0], .0001);
        assertEquals(s01, matrix[0][1], .0001);
        assertEquals(0, matrix[0][2], .0001);
        
        assertEquals(s10, matrix[1][0], .0001);
        assertEquals(0, matrix[1][1], .0001);
        assertEquals(0, matrix[1][2], .0001);
        
        assertEquals(0, matrix[2][0], .0001);
        assertEquals(0, matrix[2][1], .0001);
        assertEquals(0, matrix[2][2], .0001);
    }
    
    @Test
    public void testRunSome() {
        //Check values
        Population population = new Population(pop, new double[]{}, new double[]{}, new JobState());
        
        //initialize mpm, LT and SMatrix
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, i);
        }
        assertEquals(numberOfParameters, mpm.length);
        
        ArrayList<Pair<ParameterSet>> taskList = new ArrayList<Pair<ParameterSet>>();
        taskList.add(new Pair<ParameterSet>(new ParameterSet(0, 0), new ParameterSet(1, 1)));
        taskList.add(new Pair<ParameterSet>(new ParameterSet(0, 0), new ParameterSet(2, 2)));
        
        taskList.add(new Pair<ParameterSet>(new ParameterSet(1, 1), new ParameterSet(1, 1)));
        
        taskList.add(new Pair<ParameterSet>(new ParameterSet(2, 2), new ParameterSet(2, 2)));
        
        double[][] matrix = new double[numberOfParameters][numberOfParameters];
        EntropyWorker worker = new EntropyWorker(matrix, population, taskList);
        worker.run();
        
        assertEquals(0, matrix[0][0], .0001);
        assertEquals(s01, matrix[0][1], .0001);
        assertEquals(s02, matrix[0][2], .0001);
        
        assertEquals(s10, matrix[1][0], .0001);
        assertEquals(s11, matrix[1][1], .0001);
        assertEquals(0, matrix[1][2], .0001);
        
        assertEquals(s20, matrix[2][0], .0001);
        assertEquals(0, matrix[2][1], .0001);
        assertEquals(s22, matrix[2][2], .0001);
    }
    
}
