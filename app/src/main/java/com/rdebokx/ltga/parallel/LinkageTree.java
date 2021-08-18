package com.rdebokx.ltga.parallel;

import java.util.ArrayList;
import java.util.Arrays;

import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.shared.NearestNeighborChain;
import com.rdebokx.ltga.shared.Pair;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Randomizer;

public class LinkageTree extends LearningModel {

    /**
     * Constructor, creating a new Linkage Tree that was learned from the given population.
     * @param population The population from which a new Linkage Tree has to be learned.
     * @param randomizer The Randomizer object that should be used.
     * @param execConfig The ExecutionConfiguration used for initializing the tree.
     */
    public LinkageTree(Population population, JobConfiguration jobConfig, Randomizer randomizer, ExecutionConfiguration execConfig){
        learnStructure(population, jobConfig, randomizer, execConfig);
    }
    
    /**
     * Constructor for testing purposes. This constructor accepts a given linkage tree, represented by an arrayList of ParameterSets.
     * @param tree The tree
     */
    public LinkageTree(ArrayList<ParameterSet> tree){
        super(tree);
    }
    
    /**
     * This function learns a Linkage Tree based on the given populaten, making use of a Nearest Neighbor Chain.
     * @param population The population from which the Linkage Tree has to be learned.
     * @param randomizer The Randomizer object that should be used.
     * @param execConfig The ExecutionConfiguration used for initializing the tree.
     */
    private void learnStructure(Population population, JobConfiguration jobConfig, Randomizer randomizer, ExecutionConfiguration execConfig){
        //long timeStart = System.currentTimeMillis();
        
        //Use random order, to avoid bias when determining NN (when some points have the same MI).
        
        final int numberOfParameters = population.getNumberOfParameters();
        int[] order = randomizer.getRandomOrder(numberOfParameters);
        
        //initialize mpm, LT and SMatrix
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        Arrays.parallelSetAll(mpm, (i) -> new ParameterSet(i, order[i]));
        model = new ArrayList<ParameterSet>(mpm.length);
        if(!execConfig.USE_LOCAL_SEARCH){
	        for(ParameterSet paramSet : mpm){
	            model.add(paramSet);
	        }
        };
        MIMatrix SMatrix = new MIMatrix(mpm, population, jobConfig);
        
        //Construct NN chain
        boolean done = false;
        NearestNeighborChain nnChain = new NearestNeighborChain(numberOfParameters);
        
        while(!done){
            Pair<ParameterSet> nn = nnChain.getNNTuple(SMatrix, mpm, randomizer);
            
            //store set of considered indices in the LT, update SMatrix and construct new MPM
            ParameterSet r0 = nn.getVal1();
            ParameterSet r1 = nn.getVal2();
            ParameterSet newSet = new ParameterSet(-1, r0, r1);
            //avoid adding the full set
            if(newSet.size() < numberOfParameters){
                model.add(newSet);
            }
            
            SMatrix.updateSMatrix(r0, r1, jobConfig.EXECUTION_CONFIG);
            
            mpm = constructNewMpm(mpm, SMatrix, nn.getVal1(), nn.getVal2(), newSet, jobConfig.EXECUTION_CONFIG);
            done = mpm.length == 1;
        }
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("LinkageTree.learnStructure", timeEnd - timeStart);
    }
    
    /**
     * This function constructs a new Marginal Product Model for when r0 and r1 would be merged, based on the current state.
     * It will return a new MPM and update the given Similarity Matrix.
     * 
     * This method was made public for testing purposes.
     * 
     * @param mpm The current MPM
     * @param SMatrix The Similarity Matrix before merging. This Matrix will be updated
     * @param r0 The first Parameter Set
     * @param r1 The second Parameter Set
     * @param newSet The merged Parameter Set
     * @return The new MPM, based on merging r0 and r1.
     */
    public static ParameterSet[] constructNewMpm(ParameterSet[] mpm, MIMatrix SMatrix, ParameterSet r0, ParameterSet r1, ParameterSet newSet, ExecutionConfiguration execConfig){
        //long timeStart = System.currentTimeMillis();
        
        int newSize = mpm.length - 1;
        ParameterSet[] newMpm = new ParameterSet[newSize];
        Arrays.parallelSetAll(newMpm, i -> mpm[i]);
        
        newMpm[r0.getIndex()] = newSet;
        newSet.setIndex(r0.getIndex());
        if(r1.getIndex() < newMpm.length){
            ParameterSet last = mpm[mpm.length - 1];
            newMpm[r1.getIndex()] = last;
            last.setIndex(r1.getIndex());
            
            //Update SMatrix
            for(int i = 0; i < newMpm.length; i++){
                SMatrix.set(i, r1.getIndex(), SMatrix.get(i, mpm.length - 1));
                SMatrix.set(r1.getIndex(), i, SMatrix.get(i, r1.getIndex()));
            }
            SMatrix.set(r1.getIndex(), r1.getIndex(), SMatrix.get(mpm.length - 1, mpm.length - 1));
        }
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("LinkageTree.constructNewMpm", timeEnd - timeStart);
        
        return newMpm;
    }
}
