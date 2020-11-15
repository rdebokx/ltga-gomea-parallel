package com.rdebokx.ltga.experiments.optimalFixedFOS;

import java.util.HashSet;
import java.util.Set;

import com.rdebokx.ltga.shared.ParameterSet;

public class Split {
    
    private ParameterSet parent;
    private Set<ParameterSet> children;
    
    /**
     * Constructor, constructing a new Split object. The parent should be the union set of the given children,
     * which should be mutually exclusive. 
     * @param parent The parent from which was split. This should be the unionSet of the given children.
     * @param child1 The first child, should be a subset of the parent and mutually exclusive from child2.
     * @param child2 The second child, should be a subset of the parent and mutually exclusive from child1.
     */
    public Split(ParameterSet parent, ParameterSet child1, ParameterSet child2){
        this.parent = parent;
        this.children = new HashSet<ParameterSet>();
        children.add(child1);
        children.add(child2);
    }
    
    /**
     * @return The parent set.
     */
    public ParameterSet getParent(){
        return parent;
    }
    
    /**
     * @return The set of children
     */
    public Set<ParameterSet> getChildren(){
        return children;
    }
    
    @Override
    public boolean equals(Object o){
        boolean result = false;
        if(o instanceof Split){
            Split that = (Split) o;
            result = this.parent.equals(that.getParent()) && this.children.equals(that.getChildren());
        }
        return result;
    }
    
    @Override
    public int hashCode(){
        return parent.hashCode() + children.hashCode();
    }
    
    @Override
    public String toString(){
        return parent + "->" + children;
    }
}
