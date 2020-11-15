package com.rdebokx.ltga.parallel.executables;

import com.rdebokx.ltga.parallel.EPJobRunner;
import com.rdebokx.ltga.config.JobConfiguration;

public class EmbarrassinglyParallel {
    
    public static void main(String[] args) {
        JobConfiguration jobConfig = Main.loadJobConfiguration(args);
        EPJobRunner runner = new EPJobRunner(jobConfig, true);
        runner.run();
        System.out.println("Best found solution: " + runner.getBestFound());
    }
    
    

}
