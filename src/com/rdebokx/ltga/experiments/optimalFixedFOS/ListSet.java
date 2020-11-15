package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * ListSet: an ordered set.
 *
 * @param <T> The class of elements this ListSet should contain.
 */
public class ListSet<T> extends HashSet<T>{
    
    /**
     * The ordered list of this ListSet
     */
    private ArrayList<T> list;
    
    /**
     * Constructor, constructing an empty ListSet
     */
    public ListSet(){
        super();
        list = new ArrayList<T>();
    }
    
    /**
     * Constructor, constructing a ListSet containing the given values.
     * @param initialValue
     */
    public ListSet(Collection<T> initialValue){
        super(initialValue);
        list = new ArrayList<T>(initialValue);
    }
    
    @Override
    public boolean add(T value){
        boolean result = super.add(value);
        if(result){
            list.add(value);
        }
        return result;
    }
    
    @Override
    public boolean addAll(Collection<? extends T> values){
        boolean result = false;
        for(T value : values){
            result |= this.add(value);
        }
        return result;
    }
    
    @Override
    public void clear(){
        super.clear();
        list.clear();
    }
    
    @Override
    public Object clone(){
        return new ListSet<T>(this);
    }
    
    @Override
    public boolean remove(Object value){
        boolean result = super.remove(value);
        if(result){
            list.remove(value);
        }
        return result;
    }
    
    @Override
    public boolean removeAll(Collection<? extends Object> values){
        boolean result = false;
        for(Object value : values){
            result |= this.remove(value);
        }
        return result;
    }
    
    @Override
    public boolean removeIf(Predicate<? super T> filter){
        boolean result = super.removeIf(filter);
        if(result){
            list.removeIf(filter);
        }
        return result;
    }
    
    @Override
    public Iterator<T> iterator(){
        return list.iterator();
    }
    
    /**
     * Sort this ListSet using the given comparator.
     * @param comparator The comparator that should be used for sorting this ListSet.
     */
    public void sort(Comparator<T> comparator){
    	T[] array = (T[]) list.toArray();
    	Arrays.parallelSort(array, comparator);
    	this.list.clear();
    	list.addAll(Arrays.asList(array));
    }
    
    /**
     * @param index The index for which the value has to be returned.
     * @return The value at the given index.
     */
    public T get(int index){
        return list.get(index);
    }
    
    @Override
    public int size(){
        return list.size();
    }
    
    @Override
    public String toString(){
        return list.toString();
    }
}
