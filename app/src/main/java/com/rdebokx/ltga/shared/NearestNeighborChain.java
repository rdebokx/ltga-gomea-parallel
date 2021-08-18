package com.rdebokx.ltga.shared;

import java.util.ArrayList;

public class NearestNeighborChain extends ArrayList<ParameterSet>{

    private final int numberOfParameters;
    
    /**
     * Constructor, constructing a new Nearest Neighbor Chain. The number of parameters is stored for use by the other functions.
     * @param numberOfParameters The number of parameters in this problem.
     */
    public NearestNeighborChain(int numberOfParameters){
        super(numberOfParameters);
        this.numberOfParameters = numberOfParameters;
    }
    
    /**
     * This function extends the Nearest Neighbor Chain untill a loop is introduced. If the two last appended parameter sets 
     * have the same similarity, a loop is introduced avoid unexpected behavior.
     * @param SMatrix The similarity matrix on which the search for the to be appended nearest neighbors has to be based.
     * @param mpm The Marginal Product Model containing the parameter sets that correspond to the similarity matrix.
     * @param randomizer The Randomizer object that should be used.
     * @return A tuple of two ParameterSets that contain sets which are mutual nearest neighbor.
     */
    public Pair<ParameterSet> getNNTuple(MIMatrix SMatrix, ParameterSet[] mpm, Randomizer randomizer){
        //long timeStart = System.currentTimeMillis();
        
        //Initialize if needed
        if(this.size() == 0){
            int randInt = randomizer.generator.nextInt(mpm.length);
            this.add(mpm[randInt]);
        }
        
        //Extend if needed
        while(this.size() < 3){
            this.add(getNearestNeighbor(this.get(this.size() - 1), SMatrix, mpm));
        }
        ParameterSet last = this.get(this.size() - 1);
        ParameterSet secondLast = this.get(this.size() - 2);
        ParameterSet thirdLast = this.get(this.size() - 3);
        while(!thirdLast.equals(last)){
            this.add(getNearestNeighbor(last, SMatrix, mpm));
            
            //If last two similarity values are equal, create loop
            last = this.get(this.size() - 1);
            secondLast = this.get(this.size() - 2);
            thirdLast = this.get(this.size() - 3);
            if(SMatrix.get(secondLast.getIndex(), last.getIndex()) == SMatrix.get(secondLast.getIndex(), thirdLast.getIndex())){
                this.set(this.size() - 1, thirdLast);
                last = thirdLast;
            }
        }
        
        ParameterSet r0 = this.get(this.size() - 1);
        ParameterSet r1 = this.get(this.size() - 2);
        Pair<ParameterSet> result = null;
        if(r0.getIndex() < r1.getIndex()){
            result = new Pair<ParameterSet>(r0, r1);
        } else {
            result = new Pair<ParameterSet>(r1, r0);
        }
        
        //Remove last 3 elements
        for(int i = 0; i < 3; i++){
            this.remove(this.size() - 1);
        }
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("NearestNeighborChain.getNNTuple", timeEnd - timeStart);
        
        return result;
    }
    
    /**
     * This function finds the nearest neighbor of the given ParameterSet.
     * 
     * This method was made public for testing purposes.
     * 
     * @param paramSet The ParameterSet for which a nearest neighbor has to be found.
     * @param SMatrix The similarity matrix on which the search for the nearest neighbor has to be based.
     * @param mpm The Marginal Product Model containing the ParameterSets that correspond to the similarity matrix.
     * @return The nearest neighbor of the input ParameterSet.
     */
    public ParameterSet getNearestNeighbor(ParameterSet paramSet, MIMatrix SMatrix, ParameterSet[] mpm){
        ParameterSet result = mpm[0];
        if(paramSet.getIndex() == 0){
            result = mpm[1];
        }
        
        double maxScore = 0;
        int minWinnerSize = numberOfParameters;
        double[] paramSetSM = SMatrix.get(paramSet.getIndex());
        for(int i = 0; i < mpm.length; i++){
            if(i != paramSet.getIndex() && 
                    (paramSetSM[i] > maxScore || (paramSetSM[i] == maxScore && mpm[i].size() < minWinnerSize))){
                result = mpm[i];
                maxScore = paramSetSM[i];
                minWinnerSize = result.size();
            }
        }
        
        return result;
    }
}
