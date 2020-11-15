package com.rdebokx.ltga.profiler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Profiler {
    
    private static long programStart;
    private static long programEnd;
    private static HashMap<String, Integer> methodCounts = new HashMap<String, Integer>();
    private static HashMap<String, Long> executionTimes = new HashMap<String, Long>();
    
    /**
     * This method stores the recorded execution time for the given key. If a time is already recorded for the given key,
     * the given time is added to the time recorded earlier.
     * @param methodKey The key, identifying the function for which the execution time has to be stored.
     * @param timeInMillis The execution time in ms.
     */
    public static synchronized void recordExecution(String methodKey, Long timeInMillis){
        //Update methodCounts
        int newCount = 1;
        if(methodCounts.containsKey(methodKey)){
            newCount += methodCounts.get(methodKey);
        }
        methodCounts.put(methodKey, newCount);
        
        //Update executionTimes
        if(executionTimes.containsKey(methodKey)){
            timeInMillis += executionTimes.get(methodKey);
        }
        executionTimes.put(methodKey, timeInMillis);
    }
    
    /**
     * Set the time at which the program was started to the current system time.
     */
    public static void setProgramStart(){
        programStart = System.currentTimeMillis();
    }
    
    /**
     * Set the time at which the program has ended to the current system time.
     */
    public static void setProgramEnd(){
        programEnd = System.currentTimeMillis();
    }
    
    /**
     * @return The recorded execution time of the entire program.
     */
    public static long getProgramTime(){
    	return programEnd - programStart;
    }
    
    /**
     * @param key Key identifying for what function the execution has to be returned.
     * @return The execution time of the function identified by the given key.
     */
    public static long getRecord(String key){
    	return executionTimes.get(key);
    }
    
    /**
     * This function prints the times that were stored by the profiler.
     */
    public static void printResults(){
        printTotalTime();
        printRecords();
    }
    
    /**
     * This function prints the total time of the program in a human readable format.
     */
    public static void printTotalTime(){
        double totalTime = (programEnd - programStart) / 1000.0;
        long days = Math.round(Math.floor(totalTime / (24*60*60)));
        long hours = Math.round(Math.floor((totalTime - days*24*60*60) / (60*60)));
        long minutes = Math.round(Math.floor((totalTime - days*24*60*60 - hours*60*60) / 60));
        double seconds = totalTime - days*24*60*60 - hours*60*60 - minutes*60;
        System.out.println("Total ExecutionTime: " + totalTime + " seconds = " + days + " days " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
    }
    
    /**
     * This function prints all records that were stored by the profiler.
     */
    public static void printRecords(){
        System.out.println("Method name                                                                          Count               Time                Percentual time");
        System.out.println("-----------                                                                          -----               ----                ---------------");
        Set<Entry<String, Integer>> entries = methodCounts.entrySet();
        for(Entry<String, Integer> entry : entries){
            //Method key
            String record = entry.getKey();
            
            //Count
            int spacesNeeded = 85 - record.length();
            for(int i = 0; i < spacesNeeded; i++){
                record += " ";
            }
            record += entry.getValue();
            
            //Exec time
            spacesNeeded = 105 - record.length();
            for(int i = 0; i < spacesNeeded; i++){
                record += " ";
            }
            long executionTime = executionTimes.get(entry.getKey());
            record += executionTime / 1000.0;
            
            //Percentual time.
            spacesNeeded = 125 - record.length();
            for(int i = 0; i < spacesNeeded; i++){
                record += " ";
            }
            BigDecimal percentualTime = new BigDecimal(executionTime / ((programEnd - programStart) * .01));
            percentualTime = percentualTime.setScale(2, BigDecimal.ROUND_HALF_UP);
            record += percentualTime;
            System.out.println(record);
        }
        
    }
    
    /**
     * This function resets the profiler, meaning that all recorded execution times, including program start and end time, are cleared.
     */
    public static void reset(){
    	programStart = 0;
        programEnd = 0;
        methodCounts = new HashMap<String, Integer>();
        executionTimes = new HashMap<String, Long>();
    }

}
