package com.rdebokx.ltga.shared;

import java.util.ArrayList;

public class LearningModel {

    protected ArrayList<ParameterSet> model;
    
    /**
     * Empty constructor
     */
    public LearningModel(){}
    
    /**
     * Constructor for testing purposes. This constructor accepts a given linkage tree, represented by an arrayList of ParameterSets.
     * @param tree The tree
     */
    public LearningModel(ArrayList<ParameterSet> model){
        this.model = model;
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
    
    @Override
    public String toString(){
        return model.toString();
    }
}
