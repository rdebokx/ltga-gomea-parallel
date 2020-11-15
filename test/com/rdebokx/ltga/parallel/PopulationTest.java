package com.rdebokx.ltga.parallel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
        ExecutionConfiguration execConfig = new ExecutionConfiguration(4, -1, -1, false, -1, .0001);
        JobConfiguration jobConfig = new JobConfiguration(null, execConfig, problemConfig);
        
        assertEquals(0, js.getNumberOfEvaluations());
        population.initialize(jobConfig, randomizer);
        
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

    @Test
    public void testGenerateNewSolution(){
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
        ExecutionConfiguration execConfig = new ExecutionConfiguration(4, -1, -1, false, -1, .0001);
        JobConfiguration jobConfig = new JobConfiguration(null, execConfig, problemConfig);
        population.initialize(jobConfig, randomizer);
        Solution fakeBest = new Solution(new boolean[]{false, false, false, false}, 5, 0);
        
        ArrayList<Integer> allParams = new ArrayList<Integer>();
        allParams.add(0);
        allParams.add(1);
        allParams.add(2);
        allParams.add(3);
        ParameterSet completeSet = new ParameterSet(-1, allParams);
        
        //Test for LT = {3}. base solution 3, copying from solution 0
        ArrayList<ParameterSet> tree = new ArrayList<ParameterSet>();
        tree.add(new ParameterSet(-1, 3));
        //tree.add(completeSet);
        LinkageTree lt = new LinkageTree(tree);
        generator.setSequence(new int[]{0});
        
        //increased to 1110
        Solution offspring = population.generateNewSolution(lt, population.getPopulation()[3], population.getObjectiveValue(3), population.getConstraintValue(3), null, jobConfig, randomizer);
        assertTrue(Arrays.equals(new boolean[]{true,  true, false, true}, offspring.getSolution()));
        assertEquals(3, offspring.getObjectiveValue(), .0001);
        assertEquals(0, offspring.getConstraintValue(), .0001);
        
        //Test for LT = {2}. Base solution 0, copying from sol 2
        tree.set(0, new ParameterSet(-1, 2));
        lt = new LinkageTree(tree);
        generator.setSequence(new int[]{2});
        
        //not increased, should be equal to fakeBest
        offspring = population.generateNewSolution(lt, population.getPopulation()[0], population.getObjectiveValue(0), population.getConstraintValue(0), fakeBest, jobConfig, randomizer);
        assertTrue(Arrays.equals(fakeBest.getSolution(), offspring.getSolution()));
        assertEquals(fakeBest.getObjectiveValue(), offspring.getObjectiveValue(), .0001);
        assertEquals(fakeBest.getConstraintValue(), offspring.getConstraintValue(), .0001);
        
        //Test for LT = {0}, {1}. Base solution 1, copying from solution 2 and 4
        tree.set(0, new ParameterSet(-1, 0));
        tree.add(new ParameterSet(-1, 1));
        generator.setSequence(new int[]{0, 0});
        //Increased to 1111
        offspring = population.generateNewSolution(lt, population.getPopulation()[1], population.getObjectiveValue(1), population.getConstraintValue(1), fakeBest, jobConfig, randomizer);
        assertTrue(Arrays.equals(new boolean[]{true, true, true, true}, offspring.getSolution()));
        assertEquals(4, offspring.getObjectiveValue(), .0001);
        assertEquals(0, offspring.getConstraintValue(), .0001);
        
        //Test for LT = {0}, {1}. Base solution 1, copying from solution 4 and 0
        //Not increased, return bestSoFar
        generator.setSequence(new int[]{0});
        offspring = population.generateNewSolution(lt, population.getPopulation()[3], population.getObjectiveValue(3), population.getConstraintValue(3), fakeBest, jobConfig, randomizer);
        assertTrue(Arrays.equals(fakeBest.getSolution(), offspring.getSolution()));
        assertEquals(fakeBest.getObjectiveValue(), offspring.getObjectiveValue(), .0001);
        assertEquals(fakeBest.getConstraintValue(), offspring.getConstraintValue(), .0001);
        
        //Test for LT = {0, 1}. Base solution 2, copy from 0
        ArrayList<Integer> params01 = new ArrayList<Integer>();
        params01.add(0);
        params01.add(1);
        tree.clear();
        tree.add(new ParameterSet(-1, params01));
        generator.setSequence(new int[]{0});
        //increased to 1100
        offspring = population.generateNewSolution(lt, population.getPopulation()[2], population.getObjectiveValue(2), population.getConstraintValue(2), fakeBest, jobConfig, randomizer);
        assertTrue(Arrays.equals(new boolean[]{true,  true, false, false}, offspring.getSolution()));
        assertEquals(2, offspring.getObjectiveValue(), .0001);
        assertEquals(0, offspring.getConstraintValue(), .0001);
        
        //Test for LT = {0, 1}, {0, 1, 2, 3}. Base solution 3, copy from 0
        //not increased, fakeBest returned
        offspring = population.generateNewSolution(lt, population.getPopulation()[3], population.getObjectiveValue(3), population.getConstraintValue(3), fakeBest, jobConfig, randomizer);
        assertTrue(Arrays.equals(fakeBest.getSolution(), offspring.getSolution()));
        assertEquals(fakeBest.getObjectiveValue(), offspring.getObjectiveValue(), .0001);
        assertEquals(fakeBest.getConstraintValue(), offspring.getConstraintValue(), .0001);
        
        //test for LT = {2}, {0, 1, 3} {0, 1, 2, 3}. Base solution 2, copy from 0 and 1
        ArrayList<Integer> params013 = new ArrayList<Integer>();
        params013.add(0);
        params013.add(1);
        params013.add(3);
        tree.clear();
        tree.add(new ParameterSet(-1, 2));
        tree.add(new ParameterSet(-1, params013));
        tree.add(completeSet);
        generator.setSequence(new int[]{0});
        //increased to 1111
        offspring = population.generateNewSolution(lt, population.getPopulation()[2], population.getObjectiveValue(2), population.getConstraintValue(2), fakeBest, jobConfig, randomizer);
        assertTrue(Arrays.equals(new boolean[]{true,  true, true, true}, offspring.getSolution()));
        assertEquals(4, offspring.getObjectiveValue(), .0001);
        assertEquals(0, offspring.getConstraintValue(), .0001);
    }
    
    @Test
    public void testGenerateNewSolutionStretch(){
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
        ExecutionConfiguration execConfig = new ExecutionConfiguration(4, -1, -1, false, -1, .0001);
        JobConfiguration jobConfig = new JobConfiguration(null, execConfig, problemConfig);
        population.initialize(jobConfig, randomizer);
        Solution fakeBest = new Solution(new boolean[]{false, false, false, false}, 5, 0);
        
        ArrayList<Integer> allParams = new ArrayList<Integer>();
        allParams.add(0);
        allParams.add(1);
        allParams.add(2);
        allParams.add(3);
        ParameterSet completeSet = new ParameterSet(-1, allParams);
        
        //Test for LT = {3}, {0, 1, 2, 3}. base solution 3, copying from solution 0
        ArrayList<ParameterSet> tree = new ArrayList<ParameterSet>();
        tree.add(new ParameterSet(-1, 3));
        tree.add(completeSet);
        LinkageTree lt = new LinkageTree(tree);
        generator.setSequence(new int[]{0});
        
        //increased to 1110, however because of improvement stretch, it should be set to fakeBest
        js.setNoImprovementStretch(21);
        Solution offspring = population.generateNewSolution(lt, population.getPopulation()[3], population.getObjectiveValue(3), population.getConstraintValue(3), fakeBest, jobConfig, randomizer);
        assertTrue(Arrays.equals(fakeBest.getSolution(), offspring.getSolution()));
        assertEquals(fakeBest.getObjectiveValue(), offspring.getObjectiveValue(), .0001);
        assertEquals(fakeBest.getConstraintValue(), offspring.getConstraintValue(), .0001);
    }
    
    @Test
    public void testShuffle(){
        Randomizer randomizer = new Randomizer(42);
        int[] order = randomizer.getRandomOrder(10);
        assertTrue(Arrays.equals(order, new int[]{4, 6, 2, 1, 7, 9, 8, 5, 3, 0}));
    }
}
