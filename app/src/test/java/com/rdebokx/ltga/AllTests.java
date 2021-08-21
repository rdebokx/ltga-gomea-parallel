package com.rdebokx.ltga;

import com.rdebokx.ltga.parallel.EntropyWorkerTest;
import com.rdebokx.ltga.sequential.LinkageTreeTest;
import com.rdebokx.ltga.sequential.MIMatrixTest;
import com.rdebokx.ltga.sequential.NearestNeighborChainTest;
import com.rdebokx.ltga.sequential.PopulationTest;
import com.rdebokx.ltga.shared.DistributionTest;
import com.rdebokx.ltga.shared.FitnessComparatorTest;
import com.rdebokx.ltga.shared.ProblemEvaluatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    EntropyWorkerTest.class,
    com.rdebokx.ltga.parallel.MIMatrixTest.class,
    com.rdebokx.ltga.parallel.PopulationTest.class,
    
    LinkageTreeTest.class,
    PopulationTest.class,
    MIMatrixTest.class,
    NearestNeighborChainTest.class,
    
    DistributionTest.class,
    FitnessComparatorTest.class,
    ProblemEvaluatorTest.class
})

public class AllTests {
    private AllTests(){}
    
    public static void main(String[] args){
        org.junit.runner.JUnitCore.runClasses(AllTests.class);
    }

}
