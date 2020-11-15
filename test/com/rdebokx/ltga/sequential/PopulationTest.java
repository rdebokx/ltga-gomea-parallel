package com.rdebokx.ltga.sequential;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import nl.cwi.ltga.config.ExecutionConfiguration;
import nl.cwi.ltga.config.JobConfiguration;
import nl.cwi.ltga.config.problems.ProblemConfiguration;
import nl.cwi.ltga.shared.JobState;
import com.rdebokx.ltga.shared.NotRandom;
import nl.cwi.ltga.shared.ParameterSet;
import nl.cwi.ltga.shared.Problem;
import nl.cwi.ltga.shared.ProblemEvaluator;
import nl.cwi.ltga.shared.Randomizer;
import nl.cwi.ltga.shared.Solution;

import org.junit.Before;
import org.junit.Test;

public class PopulationTest {
    
    @Test
    public void testInitialize() {
        //check manually for random numbers:
        JobState js = new JobState();
        int populationSize = 5;
        int numberOfParameters = 4;
        
        NotRandom generator = new NotRandom();
        boolean[] popSequence = {
            true, true, true, true,
            false, true, true, true,
            false, false, false, false,
            true, true, false, false,
            true, false, false, true
        };
        generator.setSequence(popSequence);
        Randomizer randomizer = new Randomizer(generator);
        
        Population population = new Population(populationSize, numberOfParameters, js);
        ProblemConfiguration problemConfig = new ProblemConfiguration(Problem.ONEMAX);
        ExecutionConfiguration execConfig = new ExecutionConfiguration(1, -1, -1, false, -1, -1);
        population.initialize(new JobConfiguration(null, execConfig, problemConfig), randomizer);
        
        boolean[][] pop = population.getPopulation();
        assertEquals(populationSize, pop.length);
        assertEquals(populationSize, js.getNumberOfEvaluations());
        
        int i = 0;
        for(int j = 0; j < pop.length; j++){
            boolean[] sol = pop[j];
            for(boolean value : sol){
                assertEquals("Element in " + Arrays.toString(sol) + " not equal to sequence element at position " + i, popSequence[i], value);
                assertEquals(ProblemEvaluator.installedProblemEvaluation(problemConfig, sol, js), population.getObjectiveValue(j), .0001);
                assertEquals(0, population.getConstraintValue(j), .0001);
                i++;
            }
        }
    }
    
    @Test
    public void testBestSoFar(){
        //perform tests with known population
        JobState js = new JobState();
        //construct population array
        boolean[][] pop = {
            {true, true, true, false},
            {true, true, true, true},
            {false, true, false, false},
            {false, true, true, true}
        };
        double[] objectiveValues = {3, 4, 2, 3};
        double[] constraintValues = {0, 0, 0, 0};
        
        //Check with no previously best solution
        Population population = new Population(pop, objectiveValues, constraintValues, js);
        js.incrementNumberOfGenerations();
        
        Solution best = population.determineBestSoFar(null);
        assertNotNull(best);
        assertEquals(4, best.getObjectiveValue(), .001);
        assertEquals(0, best.getConstraintValue(), .001);
        assertTrue(Arrays.equals(new boolean[]{true, true, true, true}, best.getSolution()));
        assertEquals(0, js.getNoImprovementStretch());
        
        //Check with same previously best solution
        best = population.determineBestSoFar(best);
        assertNotNull(best);
        assertEquals(4, best.getObjectiveValue(), .001);
        assertEquals(0, best.getConstraintValue(), .001);
        assertTrue(Arrays.equals(new boolean[]{true, true, true, true}, best.getSolution()));
        assertEquals(1, js.getNoImprovementStretch());
        
        //check with worse solution as previous best
        Solution badSol = new Solution(new boolean[]{false, false, false, false}, 0, 0);
        best = population.determineBestSoFar(badSol);
        assertNotNull(best);
        assertEquals(4, best.getObjectiveValue(), .001);
        assertEquals(0, best.getConstraintValue(), .001);
        assertTrue(Arrays.equals(new boolean[]{true, true, true, true}, best.getSolution()));
        assertEquals(0, js.getNoImprovementStretch());
        
        //check with better solution as previous best
        Solution betterSol = new Solution(new boolean[]{false, false, false, false}, 5, 0);
        best = population.determineBestSoFar(betterSol);
        assertNotNull(best);
        assertEquals(5, best.getObjectiveValue(), .001);
        assertEquals(0, best.getConstraintValue(), .001);
        assertTrue(Arrays.equals(new boolean[]{false, false, false, false}, best.getSolution()));
        assertEquals(1, js.getNoImprovementStretch());
        
        //Test for fresh job (first evaluation):
        population = new Population(pop, objectiveValues, constraintValues, new JobState());
        best = population.determineBestSoFar(betterSol);
        assertNotNull(best);
        assertEquals(4, best.getObjectiveValue(), .001);
        assertEquals(0, best.getConstraintValue(), .001);
        assertTrue(Arrays.equals(new boolean[]{true, true, true, true}, best.getSolution()));
    }
}
