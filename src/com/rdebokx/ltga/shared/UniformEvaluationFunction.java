package com.rdebokx.ltga.shared;

public class UniformEvaluationFunction implements EvaluationFunction {
	
	private final int[] params;
	private final int[] powers;
	private final double[] table;
	
	/**
	 * Constructor, constructing a UniformEvaluation function for the given parameters based on the given lookup table.
	 * It is assumed that the lookup table contains objective values at positions corresponding to parsing 
	 * bitstrings to integers.
	 * @param params The parameters
	 * @param table
	 */
	public UniformEvaluationFunction(int[] params, double[] table){
		this.params = params;
		this.table = table;
		this.powers = new int[params.length];
		this.powers[0] = 1;
		for(int i = 1; i < powers.length; i++){
			powers[i] = powers[i - 1] * 2;
		}
	}
	
	/**
     * @see EvaluationFunction#evaluate(boolean[])
     */
	@Override
    public double evaluate(boolean[] solution){
		int key = 0;
		for(int i = 0; i < params.length; i++){
			key += solution[params[i]] ? powers[params.length - i - 1] : 0;
		}
		return table[key];
	}
}
