package it.revevol.simulation.domain;

import java.util.Map;

/**
 * Represents the result of the simulation
 * @author Angelo
 *
 */
public class SimulationResult {
	
	//How many different numbers have been generated
	private int differentNums;
	
	//A number and its occurrence in the simulation (expressed in %)
	private Map<Integer,Double> occurrencePercent;
	
	//A number and its occurrence in the simulation
	private Map<Integer,Integer> occurrence;
	
	//The most frequent number generated
	private int mostFrequent;
	
	//The simulation execution time
	private long executionTime;

	public SimulationResult(int differentNums, Map<Integer, Integer> occurrence, Map<Integer, Double> occurrencePercent,
			int mostFrequent, long executionTime) {
		this.differentNums = differentNums;
		this.occurrence = occurrence;
		this.occurrencePercent = occurrencePercent;
		this.mostFrequent = mostFrequent;
		this.executionTime = executionTime;
	}
	
	public SimulationResult(){
		
	}
	public Map<Integer, Double> getOccurencePercent() {
		return occurrencePercent;
	}
	public void setOccurencePercent(Map<Integer, Double> occurencePercent) {
		this.occurrencePercent = occurencePercent;
	}
	public Map<Integer, Integer> getOccurence() {
		return occurrence;
	}

	public void setOccurence(Map<Integer, Integer> occurence) {
		this.occurrence = occurence;
	}
	public int getDifferentNums() {
		return differentNums;
	}
	public void setDifferentNums(int differentNums) {
		this.differentNums = differentNums;
	}
	public int getMostFrequent() {
		return mostFrequent;
	}
	public void setMostFrequent(int mostFrequent) {
		this.mostFrequent = mostFrequent;
	}
	public long getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	
}

