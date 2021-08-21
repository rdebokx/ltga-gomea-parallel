package com.rdebokx.ltga.config;


/**
 * 
 * @author Rdebokx
 *
 */
public class GeneticConfiguration {

    public final int POPULATION_SIZE;
    public final int TOURNAMENT_SIZE;
    public final int SELECTION_SIZE;
    public final int NUMBER_OF_PARAMETERS;

    /**
     * Constructor, constructing a Genetic Configuration object based on the given parameters.
     * @param populationSize The population size for the job.
     * @param tournamentSize The tournament size of the job.
     * @param selectionSize The size of the selection of the job.
     * @param numberOfParameters The number of parameters for the problem of this job.
     */
    public GeneticConfiguration(int populationSize, int tournamentSize, int selectionSize, int numberOfParameters) {
        POPULATION_SIZE = populationSize;
        TOURNAMENT_SIZE = tournamentSize;
        SELECTION_SIZE = selectionSize;
        NUMBER_OF_PARAMETERS = numberOfParameters;
    }
    
    @Override
    public String toString(){
        return "PopulationSize: " + POPULATION_SIZE + "\nTournamentSize: " + TOURNAMENT_SIZE + "\nSelectionSize: " + SELECTION_SIZE +
                "\nNumberOfParameters: " + NUMBER_OF_PARAMETERS;
    }
}
