package com.rdebokx.ltga.shared;

import java.util.Arrays;
import java.util.Random;

public class NotRandom extends Random {
    
    private int[] intSequence;
    private boolean[] boolSequence;
    private int intPointer;
    private int boolPointer;
    
    public void setSequence(int[] sequence){
        this.intSequence = sequence;
        intPointer = 0;
    }
    
    public void setSequence(boolean[] sequence){
        this.boolSequence = sequence;
        boolPointer = 0;
    }
    
    @Override
    public int nextInt(int range){
        int result = intSequence[intPointer];
        //System.out.println("Next not random int: sequence[" + intPointer + "]=" + result);
        intPointer++;
        intPointer = intPointer % intSequence.length;
        return result;
    }
    
    @Override
    public boolean nextBoolean(){
        boolean result = boolSequence[boolPointer];
        boolPointer++;
        boolPointer = boolPointer % boolSequence.length;
        return result;
    }
    
    @Override
    public String toString(){
        return "pointer: " + intPointer + " sequence: " + Arrays.toString(intSequence);
    }
    
}
