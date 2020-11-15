package com.rdebokx.ltga.shared;

public class Pair<T> {

    private T val1;
    private T val2;
    
    /**
     * Constructor, constructing a pair of values.
     * @param val1 The first value.
     * @param val2 The second value
     */
    public Pair(T val1, T val2){
        this.val1 = val1;
        this.val2 = val2;
    }
    
    /**
     * @return The first value.
     */
    public T getVal1(){
        return val1;
    }
    
    /**
     * @return The second value.
     */
    public T getVal2(){
        return val2;
    }
    
    @Override
    public String toString(){
        return "(" + val1 + ", " + val2 + ")";
    }
}
