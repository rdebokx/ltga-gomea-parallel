package com.rdebokx.ltga.sequential;

import java.util.ArrayList;

import com.rdebokx.ltga.shared.LearningModel;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.shared.NearestNeighborChain;
import com.rdebokx.ltga.shared.Pair;
import com.rdebokx.ltga.shared.ParameterSet;
import com.rdebokx.ltga.shared.Randomizer;

public class LinkageTree extends LearningModel {

    /**
     * Constructor, creating a new Linkage Tree that was learned from the given population.
     * @param population The population from which a new Linkage Tree has to be learned.
     * @param randomizer The Randomizer object that should be used.
     */
    public LinkageTree(Population population, Randomizer randomizer, ExecutionConfiguration execConfig){
        learnStructure(population, randomizer, execConfig);
    }
    
    /**
     * Constructor for testing purposes. This constructor accepts a given linkage tree, represented by an arrayList of ParameterSets.
     * @param tree The tree
     */
    public LinkageTree(ArrayList<ParameterSet> tree){
        super(tree);
    }
    
    /**
     * This function learns a Linkage Tree based on the given population, making use of a Nearest Neighbor Chain.
     * @param population The population from which the Linkage Tree has to be learned.
     * @param randomizer The Randomizer object that should be used.
     */
    private void learnStructure(Population population, Randomizer randomizer, ExecutionConfiguration execConfig){
        //long timeStart = System.currentTimeMillis();
        
        //Use random order, to avoid bias when determining NN (when some points have the same MI).
        final int numberOfParameters = population.getNumberOfParameters();
        int[] order = randomizer.getRandomOrder(numberOfParameters);
        
        //initialize mpm, LT and SMatrix
        ParameterSet[] mpm = new ParameterSet[numberOfParameters];
        for(int i = 0; i < numberOfParameters; i++){
            mpm[i] = new ParameterSet(i, order[i]);
        }
        MIMatrix SMatrix = new MIMatrix(mpm, population);
        
        learnStructureWithMatrix(numberOfParameters, SMatrix, mpm, randomizer, execConfig.USE_LOCAL_SEARCH);
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("LinkageTree.learnStructure", timeEnd - timeStart);
    }
    
    /**
     * This function learns a Linkage Tree based on the given MIMatrix and mpm
     * @param numberOfParameters The number of paramters of the problem at hand.
     * @param SMatrix The Similarity Matrix on which the LT has to be based.
     * @param mpm The Marginal Product Model for learning the LT.
     * @param randomizer The randomizer that is to be used.
     * @param useLocalSearch Boolean indicating whether local search is used. If local search is not used, all singleton sets will be added to the bottom of the tree.
     */
    public void learnStructureWithMatrix(int numberOfParameters, MIMatrix SMatrix, ParameterSet[] mpm, Randomizer randomizer, boolean useLocalSearch){
        model = new ArrayList<ParameterSet>(mpm.length);
        if(!useLocalSearch){
            for(ParameterSet paramSet : mpm){
                model.add(paramSet);
            }
        }
        
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
            
            SMatrix.updateSMatrix(r0, r1);
            
            mpm = constructNewMpm(mpm, SMatrix, nn.getVal1(), nn.getVal2(), newSet);
            done = mpm.length == 1;
        }
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
    public static ParameterSet[] constructNewMpm(ParameterSet[] mpm, MIMatrix SMatrix, ParameterSet r0, ParameterSet r1, ParameterSet newSet){
        //long timeStart = System.currentTimeMillis();
        
        int newSize = mpm.length - 1;
        ParameterSet[] newMpm = new ParameterSet[newSize];
        
        for(int i = 0; i < newSize; i++){
            //Add all elements except for last one
            newMpm[i] = mpm[i];
        }
        
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
    
    /**
     * @return The size of this Linkage Tree
     */
    public int size(){
        return model.size();
    }
    
    /**
     * Returns the parameter at the given position in the Linkage Tree.
     * @param i The position for which the ParameterSet has to be returned.
     * @return The ParameterSet at the given position.
     */
    public ParameterSet get(int i){
        return model.get(i);
    }
}
