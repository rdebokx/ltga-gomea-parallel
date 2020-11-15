package com.rdebokx.ltga.experiments.timers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SchedulerTimer {

    public static void main(String[] args) {
        long singleThreadStart = System.currentTimeMillis();
        for(int i = 0; i < 100; i++){
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.submit(() -> {
                //Do nothing
            });
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long singleThreadEnd = System.currentTimeMillis();
        double singleThreadTime = (singleThreadEnd - singleThreadStart) / 100.0;
        
        System.out.println("Single Thread time: " + singleThreadTime);
        
        long multipleThreadStart = System.currentTimeMillis();
        for(int i = 0; i < 100; i++){
            ExecutorService executor = Executors.newFixedThreadPool(64);
            for(int j = 0; j < 64; j++){
                executor.submit(() -> {
                    //Do nothing
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long multipleThreadEnd = System.currentTimeMillis();
        double multipleThreadTime = (multipleThreadEnd - multipleThreadStart) / 100.0;
        
        System.out.println("Single Thread time: " + multipleThreadTime);
    }

}
