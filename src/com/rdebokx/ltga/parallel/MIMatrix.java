package com.rdebokx.ltga.parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.rdebokx.ltga.parallel.workers.EntropyWorker;
import com.rdebokx.ltga.profiler.Profiler;
import com.rdebokx.ltga.config.ExecutionConfiguration;
import com.rdebokx.ltga.config.JobConfiguration;
import com.rdebokx.ltga.shared.Pair;
import com.rdebokx.ltga.shared.ParameterSet;

public class MIMatrix extends com.rdebokx.ltga.shared.MIMatrix {
    
    /**
     * Constructor, constructing a Mutual Information Matrix based on the given MPM and population.
     * @param mpm The Marginal Product Model for which the Mutual Information Matrix was constructed.
     * @param population The population on which the Mutual Information Matrix has to be based.
     * @param jobConfig The job configuration, used to determine the amount of threads available.
     */
    public MIMatrix(ParameterSet[] mpm, Population population, JobConfiguration jobConfig){
        super(population.getNumberOfParameters());
        constructMIMatrix(mpm, population, jobConfig);
    }
    
    /**
     * Private constructor, used to clone this MIMatrix.
     * @param numberOfParameters The number of parameters for this MIMatrix
     */
    public MIMatrix(int numberOfParameters){
        super(numberOfParameters);
    }
    
    /**
     * This is an internal method that calculates the values of the matrix, based on the given population
     * @param population The population for which the Mutual Information Matrix has to be constructed.
     */
    private void constructMIMatrix(ParameterSet[] mpm, Population population, JobConfiguration jobConfig){
        long timeStart = System.currentTimeMillis();
        
        //Fill with Entropy values
        fillWithEntropyValues(mpm, population, jobConfig.EXECUTION_CONFIG);
        
        //calculate MI values
        calculateMIValues(population.getNumberOfParameters(), jobConfig.EXECUTION_CONFIG);
        
        long timeEnd = System.currentTimeMillis();
        Profiler.recordExecution("MIMatrix.constructMIMatrix", timeEnd - timeStart);
    }
    
    /**
     * This method fills the matrix with entropy values in parallel.
     * @param mpm The Marginal Product Model for which the entropy values have to be calculated.
     * @param population The populations of which the entropy values have to be calculated.
     * @param execConfig The execution configuration, needed for the amount of threads available.
     */
    public void fillWithEntropyValues(ParameterSet[] mpm, Population population, ExecutionConfiguration execConfig){
        /*
         * Fill with entropy values.
         * Note that this seems to differ from the implementation in c, where the cumulative chances are calculated and after that
         * transformed back to individual chances, which are then used to calculate the joint entropy. This is equal to our implementation
         * of calculating the joint entropy in our Distribution class.
         */
        //long entropyTimeStart = System.currentTimeMillis();
        
        int l = population.getNumberOfParameters();
        int processableElements = l * (l + 1) / 2;
        int elemsPerThread = (int) Math.ceil(processableElements / (execConfig.THREADS * 1.0));
        ExecutorService executor = Executors.newFixedThreadPool(execConfig.THREADS);
        
        ArrayList<Pair<ParameterSet>> taskArray = new ArrayList<Pair<ParameterSet>>();
        for(int i = 0; i < population.getNumberOfParameters(); i++){
            ParameterSet paramSet1 = mpm[i];
            for(int j = i; j < mpm.length; j++){
                ParameterSet paramSet2 = mpm[j];
                if(taskArray.size() == elemsPerThread){
                    executor.submit(new EntropyWorker(matrix, population, taskArray));
                    taskArray = new ArrayList<Pair<ParameterSet>>();
                }
                taskArray.add(new Pair<ParameterSet>(paramSet1, paramSet2));
            }
        }
        if(taskArray.size() > 0){
            executor.submit(new EntropyWorker(matrix, population, taskArray));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(365, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //long entropyTimeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("MIMatrix.constructMIMatrix - entropyValues", entropyTimeEnd - entropyTimeStart);
    }
    
    /**
     * This method calculates the MI values for this matrix in parallel.
     * @param numberOfParameters The number of parameters in the matrix.
     * @param execConfig The execution configuration, needed for the amount of threads available.
     */
    public void calculateMIValues(int numberOfParameters, ExecutionConfiguration execConfig){
        //long miTimeStart = System.currentTimeMillis();
        
        ExecutorService executor = Executors.newFixedThreadPool(execConfig.THREADS);
        int elemsPerThread = (int) Math.ceil(numberOfParameters * numberOfParameters / (execConfig.THREADS * 1.0));
        
        for(int t = 0; t < execConfig.THREADS; t++){
            int fromElem = t * elemsPerThread;
            
            executor.submit(() -> {
                int i = fromElem / numberOfParameters;
                int j = fromElem - numberOfParameters * i;
                
                for(int k = 0; k < elemsPerThread; k++){
                    matrix[i][j] = matrix[i][i] + matrix[j][j] - matrix[i][j];
                    j++;
                    if(j == numberOfParameters){
                        j = 0;
                        i++;
                    }
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(365, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //long miTimeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("MIMatrix.constructMIMatrix - MIValues", miTimeEnd - miTimeStart);
    }
    
    /**
     * This method updates this Mutual Information Matrix for when the given ParameterSets would be merged.
     * @param r0 The first ParameterSet.
     * @param r1 The second ParameterSet.
     */
    public void updateSMatrix(ParameterSet r0, ParameterSet r1, ExecutionConfiguration execConfig){
        //long timeStart = System.currentTimeMillis();
        
        double mul0 = r0.size() / ((r0.size() + r1.size()) * 1.0);
        double mul1 = r1.size() / ((r0.size() + r1.size()) * 1.0);
        
        for(int i = 0; i < matrix.length; i++){
            if(i != r0.getIndex() && i != r1.getIndex()){
                matrix[i][r0.getIndex()] = mul0 * matrix[i][r0.getIndex()] + mul1 * matrix[i][r1.getIndex()];
                matrix[r0.getIndex()][i] = matrix[i][r0.getIndex()];
            }
        }
        
        //long timeEnd = System.currentTimeMillis();
        //Profiler.recordExecution("MIMatrix.updateSMatrix", timeEnd - timeStart);
    }
    
    /**
     * This function returns a new MIMatrix with the same contents as this MIMatrix.
     * @return A copy of this MIMatrix.
     */
    @Override
    public MIMatrix clone(){
        MIMatrix out = new MIMatrix(matrix.length);
        for(int i = 0; i < matrix.length; i++){
            out.matrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        
        return out;
    }
}
