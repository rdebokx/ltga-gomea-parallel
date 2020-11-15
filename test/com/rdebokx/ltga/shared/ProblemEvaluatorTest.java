package com.rdebokx.ltga.shared;

import static org.junit.Assert.*;
import nl.cwi.ltga.config.problems.MaxCutConfiguration;
import nl.cwi.ltga.config.problems.InstancesConfiguration;
import nl.cwi.ltga.config.problems.ProblemConfiguration;

import org.junit.Before;
import org.junit.Test;

public class ProblemEvaluatorTest {

    @Test
    public void testOneMax() {
        testOneMax(0, new boolean[]{});
        testOneMax(0, new boolean[]{false});
        testOneMax(0, new boolean[]{false, false, false});
        testOneMax(1, new boolean[]{true});
        testOneMax(1, new boolean[]{false, true});
        testOneMax(1, new boolean[]{true, false});
        testOneMax(3, new boolean[]{true, false, false, true, true, false, false});
    }
    
    private void testOneMax(double score, boolean[] solution){
        JobState jobState = new JobState();
        double result = ProblemEvaluator.installedProblemEvaluation(new ProblemConfiguration(Problem.ONEMAX), solution, jobState);
        assertEquals(score, result, .0001);
        assertEquals(1, jobState.getNumberOfEvaluations());
    }
    
    @Test
    public void testDTrap4Loose() {
        //{}
        test4Loose(0, new boolean[]{});
        
        //0000
        test4Loose(.75, new boolean[]{false, false, false, false});
        
        //0001
        test4Loose(.5, new boolean[]{false, false, false, true});
        
        //0011
        test4Loose(.25, new boolean[]{false, false, true, true});
        
        //0111
        test4Loose(0, new boolean[]{false, true, true, true});
        
        //1111
        test4Loose(1, new boolean[]{true, true, true, true});
        
        //0000 0000 0000 0000
        test4Loose(3, new boolean[]{
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false
        });
        
        //1000 1000 1000 1000
        test4Loose(3.25, new boolean[]{
                true, false, false, false,
                true, false, false, false,
                true, false, false, false,
                true, false, false, false
        });
        
        //0000 0000 0000 1111
        test4Loose(2, new boolean[]{
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                true, true, true, true
        });
        
        //1111 1111 1111 1111
        test4Loose(4, new boolean[]{
                true, true, true, true,
                true, true, true, true,
                true, true, true, true,
                true, true, true, true
        });
    }
    
    private void test4Loose(double score, boolean[] solution){
        JobState jobState = new JobState();
        double result = ProblemEvaluator.installedProblemEvaluation(new ProblemConfiguration(Problem.DECEPTIVE_TRAP_4_LOOSE_ENCODING), solution, jobState);
        assertEquals(score, result, .0001);
        assertEquals(1, jobState.getNumberOfEvaluations());
    }
    
    @Test
    public void testDTrap4Tight() {
        JobState jobState = new JobState();
        //{}
        test4Tight(0, new boolean[]{}, jobState);
        
        //0000
        test4Tight(.75, new boolean[]{false, false, false, false}, jobState);
        
        //0001
        test4Tight(.5, new boolean[]{false, false, false, true}, jobState);
        
        //0011
        test4Tight(.25, new boolean[]{false, false, true, true}, jobState);
        
        //0111
        test4Tight(0, new boolean[]{false, true, true, true}, jobState);
        
        //1111
        test4Tight(1, new boolean[]{true, true, true, true}, jobState);
        
        //0000 0000 0000 0000
        test4Tight(3, new boolean[]{
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false
        }, jobState);
        
        //0000 0000 0000 1111
        test4Tight(3.25, new boolean[]{
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                true, true, true, true
        }, jobState);
        
        //0000 0011 1111 1011
        test4Tight(2, new boolean[]{
                false, false, false, false,
                false, false, true, true,
                true, true, true, true,
                true, false, true, true
        }, jobState);
        
        //1111 1111 1111 1111
        test4Tight(4, new boolean[]{
                true, true, true, true,
                true, true, true, true,
                true, true, true, true,
                true, true, true, true
        }, jobState);
    }
    
    private void test4Tight(double score, boolean[] solution, JobState jobState){
        int evalNum = jobState.getNumberOfEvaluations();
        double result = ProblemEvaluator.installedProblemEvaluation(new ProblemConfiguration(Problem.DECEPTIVE_TRAP_4_TIGHT_ENCODING), solution, jobState);
        assertEquals(score, result, .0001);
        assertEquals(evalNum + 1, jobState.getNumberOfEvaluations());
    }
    
    @Test
    public void testDTrap5Loose() {
        JobState jobState = new JobState();
        //{}
        test5Loose(0, new boolean[]{}, jobState);
        
        //00000
        test5Loose(.8, new boolean[]{false, false, false, false, false}, jobState);
        
        //00001
        test5Loose(.6, new boolean[]{false, false, false, false, true}, jobState);
        
        //00011
        test5Loose(.4, new boolean[]{false, false, false, true, true}, jobState);
        
        //00111
        test5Loose(0.2, new boolean[]{false, false, true, true, true}, jobState);
        
        //01111
        test5Loose(0, new boolean[]{false, true, true, true, true}, jobState);
        
        //1111
        test5Loose(1, new boolean[]{true, true, true, true, true}, jobState);
        
        //0000 0000 0000 0000 0000
        test5Loose(3.2, new boolean[]{
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false
        }, jobState);
        
        //1000 1000 1000 1000 1000
        test5Loose(3.4, new boolean[]{
                true, false, false, false,
                true, false, false, false,
                true, false, false, false,
                true, false, false, false,
                true, false, false, false
        }, jobState);
        
        //0000 0000 0000 0000 1111
        test5Loose(2.4, new boolean[]{
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                true, true, true, true
        }, jobState);
        
        //1111 1111 1111 1111 1111
        test5Loose(4, new boolean[]{
                true, true, true, true,
                true, true, true, true,
                true, true, true, true,
                true, true, true, true,
                true, true, true, true
        }, jobState);
    }
    
    private void test5Loose(double score, boolean[] solution, JobState jobState){
        int evalNum = jobState.getNumberOfEvaluations();
        double result = ProblemEvaluator.installedProblemEvaluation(new ProblemConfiguration(Problem.DECEPTIVE_TRAP_5_LOOSE_ENCODING), solution, jobState);
        assertEquals(score, result, .0001);
        assertEquals(evalNum + 1, jobState.getNumberOfEvaluations());
    }
    
    @Test
    public void testDTrap5Tight() {
        JobState jobState = new JobState();
        //{}
        test5Tight(0, new boolean[]{}, jobState);
        
        //00000
        test5Tight(.8, new boolean[]{false, false, false, false, false}, jobState);
        
        //00001
        test5Tight(.6, new boolean[]{false, false, false, false, true}, jobState);
        
        //00011
        test5Tight(.4, new boolean[]{false, false, false, true, true}, jobState);
        
        //00111
        test5Tight(.2, new boolean[]{false, false, true, true, true}, jobState);
        
        //01111
        test5Tight(0, new boolean[]{false, true, true, true, true}, jobState);
        
        //11111
        test5Tight(1, new boolean[]{true, true, true, true, true}, jobState);
        
        
        //00000 00000 00000 00000
        test5Tight(3.2, new boolean[]{
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false
        }, jobState);
        
        //00000 00000 00000 11111
        test5Tight(3.4, new boolean[]{
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                true, true, true, true, true
        }, jobState);
        
        //00000 11111 11101 10100
        test5Tight(2.2, new boolean[]{
                false, false, false, false, false,
                true, true, true, true, true,
                true, true, true, false, true,
                true, false, true, false, false
        }, jobState);
        
        //11111 11111 11111 11111
        test5Tight(4, new boolean[]{
                true, true, true, true, true,
                true, true, true, true, true,
                true, true, true, true, true,
                true, true, true, true, true
        }, jobState);
    }
    
    private void test5Tight(double score, boolean[] solution, JobState jobState){
        double result = ProblemEvaluator.installedProblemEvaluation(new ProblemConfiguration(Problem.DECEPTIVE_TRAP_5_TIGHT_ENCODING), solution, jobState);
        assertEquals(score, result, .0001);
    }
    
    @Test
    public void testNKLandscapes1(){
        //Test with 3 parameters
        int k = 1;
        //int blockTransition = 1;
        int numberOfSubFunctions = 3;
        UniformEvaluationFunction[] functions = new UniformEvaluationFunction[numberOfSubFunctions];
        
        int[] key1 = {0};
        double[] map1 = new double[2];
        map1[0] = 0.0459;
        map1[1] = 0.7826;
        functions[0] = new UniformEvaluationFunction(key1, map1);
        
        int[] key2 = {1};
        double[] map2 = new double[2];
        map2[0] = 0.0663;
        map2[1] = 0.6816;
        functions[1] = new UniformEvaluationFunction(key2, map2);
        
        int[] key3 = {2};
        double[] map3 = new double[2];
        map3[0] = 0.515;
        map3[1] = 0.2915;
        functions[2] = new UniformEvaluationFunction(key3, map3);
        
        InstancesConfiguration nkConfig = new InstancesConfiguration(Problem.NK_LANDSCAPES, null, k, numberOfSubFunctions, new boolean[0], -1, functions);
        JobState js = new JobState();
        
        boolean[] sol1 = {false, false, false};
        assertEquals(.0459 + .0663 + .515, ProblemEvaluator.installedProblemEvaluation(nkConfig, sol1, js), .000001);
        
        boolean[] sol2 = {false, true, false};
        assertEquals(.0459 + .6816 + .515, ProblemEvaluator.installedProblemEvaluation(nkConfig, sol2, js), .000001);
        
        boolean[] sol3 = {true, false, true};
        assertEquals(.7826 + .0663 + .2915, ProblemEvaluator.installedProblemEvaluation(nkConfig, sol3, js), .000001);
        
        boolean[] sol4 = {true, true, true};
        assertEquals(.7826 + .6816 + .2915, ProblemEvaluator.installedProblemEvaluation(nkConfig, sol4, js), .000001);
    }
    
    @Test
    public void testNKLandscapes3(){
        //Test with 6 parameters
        int k = 3;
        //int blockTransition = 1;
        int numberOfSubfunctions = 4;

        UniformEvaluationFunction[] functions = new UniformEvaluationFunction[4];
        
        int[] key1 = {0, 1, 2};
        double[] map1 = new double[8];
        map1[Integer.parseInt("000", 2)] = 0.8431;
        map1[Integer.parseInt("001", 2)] = 0.1104;
        map1[Integer.parseInt("010", 2)] = 0.9825;
        map1[Integer.parseInt("011", 2)] = 0.9479;
        map1[Integer.parseInt("100", 2)] = 0.7825;
        map1[Integer.parseInt("101", 2)] = 0.9441;
        map1[Integer.parseInt("110", 2)] = 0.6905;
        map1[Integer.parseInt("111", 2)] = 0.6401;
        functions[0] = new UniformEvaluationFunction(key1, map1); 

        int[] key2 = {1, 2, 3};
        double[] map2 = new double[8];
        map2[Integer.parseInt("000", 2)] = 0.991;
        map2[Integer.parseInt("001", 2)] = 0.6855;
        map2[Integer.parseInt("010", 2)] = 0.6636;
        map2[Integer.parseInt("011", 2)] = 0.5314;
        map2[Integer.parseInt("100", 2)] = 0.4699;
        map2[Integer.parseInt("101", 2)] = 0.8393;
        map2[Integer.parseInt("110", 2)] = 0.8376;
        map2[Integer.parseInt("111", 2)] = 0.3419;
        functions[1] = new UniformEvaluationFunction(key2, map2);
        
        int[] key3 = {2, 3, 4};
        double[] map3 = new double[8];
        map3[Integer.parseInt("000", 2)] = 0.5589;
        map3[Integer.parseInt("001", 2)] = 0.2736;
        map3[Integer.parseInt("010", 2)] = 0.3399;
        map3[Integer.parseInt("011", 2)] = 0.8061;
        map3[Integer.parseInt("100", 2)] = 0.132;
        map3[Integer.parseInt("101", 2)] = 0.9695;
        map3[Integer.parseInt("110", 2)] = 0.617;
        map3[Integer.parseInt("111", 2)] = 0.0406;
        functions[2] = new UniformEvaluationFunction(key3, map3);
        
        int[] keys4 = {3, 4, 5};
        double[] map4 = new double[8];
        map4[Integer.parseInt("000", 2)] = 0.0527;
        map4[Integer.parseInt("001", 2)] = 0.9183;
        map4[Integer.parseInt("010", 2)] = 0.3277;
        map4[Integer.parseInt("011", 2)] = 0.047;
        map4[Integer.parseInt("100", 2)] = 0.7768;
        map4[Integer.parseInt("101", 2)] = 0.3914;
        map4[Integer.parseInt("110", 2)] = 0.6902;
        map4[Integer.parseInt("111", 2)] = 0.4796;
        functions[3] = new UniformEvaluationFunction(keys4, map4);
        
        InstancesConfiguration nkConfig = new InstancesConfiguration(Problem.NK_LANDSCAPES, null, k, numberOfSubfunctions, new boolean[0], -1, functions);
        JobState js = new JobState();
        
        boolean[] sol = {false, false, false, false, false, false};
        assertEquals(.8431 + .991 + .5589 + .0527, ProblemEvaluator.installedProblemEvaluation(nkConfig, sol, js), .000001);
        
        boolean[] sol2 = {false, true, true, false, false, true};
        assertEquals(.9479 + .8376 + .132 + .9183, ProblemEvaluator.installedProblemEvaluation(nkConfig, sol2, js), .000001);
        
        boolean[] sol3 = {true, false, true, true, false, true};
        assertEquals(.9441 + .5314 + .617 + .3914, ProblemEvaluator.installedProblemEvaluation(nkConfig, sol3, js), .000001);
        
        boolean[] sol4 = {true, true, true, true, true, true};
        assertEquals(.6401 + .3419 + .0406 + .4796, ProblemEvaluator.installedProblemEvaluation(nkConfig, sol4, js), .000001);
    }
    
    @Test
    public void testMaxCut(){
    	JobState js = new JobState();
    	final int numberOfVertices = 6;
    	final int[][] weights = new int[numberOfVertices][numberOfVertices];
    	weights[0][1] = weights[1][0] = 6;
    	weights[0][2] = weights[2][0] = 2;
    	weights[1][5] = weights[5][1] = 5;
    	weights[2][3] = weights[3][2] = 9;
    	MaxCutEvaluationFunction weightsFunction = new MaxCutEvaluationFunction(numberOfVertices, weights);
    	ProblemConfiguration problemConfig = new MaxCutConfiguration(null, 0, 0, weightsFunction);
    	
    	assertEquals(0, ProblemEvaluator.installedProblemEvaluation(problemConfig, Solution.parseSolution("000000"), js), .00001);
    	assertEquals(0, ProblemEvaluator.installedProblemEvaluation(problemConfig, Solution.parseSolution("111111"), js), .00001);
    	assertEquals(0, ProblemEvaluator.installedProblemEvaluation(problemConfig, Solution.parseSolution("000010"), js), .00001);
    	assertEquals(0, ProblemEvaluator.installedProblemEvaluation(problemConfig, Solution.parseSolution("111101"), js), .00001);
    	assertEquals(6+2, ProblemEvaluator.installedProblemEvaluation(problemConfig, Solution.parseSolution("100000"), js), .00001);
    	assertEquals(9+5, ProblemEvaluator.installedProblemEvaluation(problemConfig, Solution.parseSolution("111000"), js), .00001);
    	assertEquals(6+5+9, ProblemEvaluator.installedProblemEvaluation(problemConfig, Solution.parseSolution("010100"), js), .00001);
    }
}
