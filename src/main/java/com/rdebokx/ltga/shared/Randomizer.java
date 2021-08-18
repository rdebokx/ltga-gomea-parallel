package com.rdebokx.ltga.shared;

import java.util.ArrayList;
import java.util.Random;

public class Randomizer {
    
    public Random generator;
    
    /**
     * Constructor, constructing a new Randomizer based on a new Random object.
     */
    public Randomizer(){
        this(new Random());
    }
    
    /**
     * Constructor, constructing a new Randomizer that uses a new Random object with the given seed.
     * @param seed The seed for the generator that should be used by this Randomizer.
     */
    public Randomizer(long seed){
        this(new Random(seed));
    }
    
    /**
     * Constructor, constructing a new Randomizer that uses the given random object. Only for testing purposes.
     * @param generator The generator that should be used by this Randomizer.
     */
    public Randomizer(Random generator){
        this.generator = generator;
    }

    /**
     * This function generates an array with values between 0 and the given size and shuffles this array, resulting in an 
     * array that represents a random order.
     * @param size The size of the random order.
     * @return An array representing a random order.
     */
    public int[] getRandomOrder(int size){
        int[] order = new int[size];
        for(int i = 0; i < size; i++){
            order[i] = i;
        }
        this.shuffle(order, size);
        return order;
    }

    /**
     * This function shuffles the given array using the Fisher-Yates shuffle technique.
     * @param order The array that should be shuffled.
     * @return A shuffled version of the inputted array.
     */
    public void shuffle(int[] order, int size){
        for(int i = size - 1; i > 0; i--){
            int j = generator.nextInt(i+1);
            int temp = order[j];
            order[j] = order[i];
            order[i] = temp;
        }
    }
    
    /**
     * This function shuffles the given arrayList using the Fisher-Yates shuffle technique.
     * @param list The arrayList that should be shuffled.
     * @return A shuffled version of the inputted arrayList.
     */
    public void shuffle(ArrayList order){
        for(int i = order.size() - 1; i > 0; i--){
            int j = generator.nextInt(i+1);
            Object temp = order.get(j);
            order.set(j, order.get(i));
            order.set(i, temp);
        }
    }
}
