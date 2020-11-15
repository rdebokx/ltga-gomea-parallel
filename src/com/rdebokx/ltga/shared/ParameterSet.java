package com.rdebokx.ltga.shared;

import java.util.Collection;
import java.util.HashSet;

public class ParameterSet extends HashSet<Integer> {

    private int index;
    
    /**
     * Constructor, constructing an empty ParameterSet with index -1.
     */
    public ParameterSet(){
    	index = -1;
    }
    
    /**
     * Constructor, constructing a new ParameterSet with the given value as initial value. The index will be set to -1.
     * @param initValue The parameter that this ParameterSet should contain.
     */
    public ParameterSet(int initValue){
    	this.index = -1;
    	this.add(initValue);
    }
    
    /**
     * Constructor, constructing a new ParameterSet with the given index and value
     * @param index The index of this ParameterSet in the MPM
     * @param initValue The initial value of this ParameterSet
     */
    public ParameterSet(int index, int initValue){
        this.index = index;
        this.add(initValue);
    }
    
    /**
     * Constructor, constructing a new ParameterSet based on the given collection of values.
     * @param index The index of this ParameterSet in the MPM.
     * @param initValue The collection of values of which this ParameterSet consists.
     */
    public ParameterSet(int index, Collection<Integer> initValue){
        super(initValue);
        this.index = index;
    }
    
    /**
     * Constructor, constructing a new ParameterSet based on the given collection of values.
     * @param initValue The collection of values of which this ParameterSet consists.
     */
    public ParameterSet(Collection<Integer> initValue){
        super(initValue);
        this.index = -1;
    }
    
    /**
     * Constructor, constructing a new ParameterSet based on two collection of values. The collections are merged and 
     * added to this ParameterSet. 
     * @param index The index of this ParameterSet in the MPM.
     * @param col1 The first collection to put in this ParameterSet.
     * @param col2 The second collection to put in this ParameterSet.
     */
    public ParameterSet(int index, Collection<Integer> col1, Collection<Integer> col2){
        this(index, col1);
        this.addAll(col2);
    }
    /**
     * Constructor, constructing a new ParameterSet based on two collection of values. The collections are merged and 
     * added to this ParameterSet. 
     * @param col1 The first collection to put in this ParameterSet.
     * @param col2 The second collection to put in this ParameterSet.
     */
    public ParameterSet(Collection<Integer> col1, Collection<Integer> col2){
        super(col1);
        if(col2 != null){
            this.addAll(col2);
        }
    }
    
    /**
     * Constructor, constructing a new ParameterSet with the given two parameters.
     * @param index The index of this ParameterSet.
     * @param value1 The first parameter in this parameterSet.
     * @param value2 The second parameter in this parameterSet.
     */
    public ParameterSet(int index, int value1, int value2){
        this.index = index;
        this.add(value1);
        this.add(value2);
    }
    
    /**
     * @return The index of this ParameterSet in the MPM.
     */
    public int getIndex(){
        return index;
    }
    
    /**
     * Set the index of this ParameterSet in the MPM.
     * @param index
     */
    public void setIndex(int index){
        this.index = index;
    }
    
    public static ParameterSet parse(String text){
    	ParameterSet result = new ParameterSet();
    	String[] parts = text.replace("[", "").replace("]", "").split(", ");
    	for(String parameter : parts){
    		result.add(Integer.parseInt(parameter));
    	}
    	return result;
    }
    
    @Override
    public String toString(){
        //return index + "=" + super.toString();
    	return super.toString();
    }
}
