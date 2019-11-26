package ea;

import teamPursuit.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Individual {

	public String results = "";
	
	boolean[] transitionStrategy = new boolean[22];
	int[] pacingStrategy = new int[23];
	
	SimulationResult result = null;
	
	public Individual() {		
		
	}

	public String getResult(){
		// System.out.println("-----sending results: "+results);
		return results;
	}

	// this code just evolves the transition strategy
	// an individual is initialised with a random strategy that will evolve
	// the pacing strategy is initialised to the default strategy and remains fixed

	public void initialise(int start, int bound) {
		for(int i = 0; i < transitionStrategy.length; i++)
			transitionStrategy[i] = Parameters.rnd.nextBoolean();

		for(int i = 0; i < pacingStrategy.length; i++) {
			pacingStrategy[i] = start - Parameters.rnd.nextInt(bound);
		}
	}
	
	// this is just there in case you want to check the default strategies
	public void initialise_default(){
		for(int i = 0; i < transitionStrategy.length; i++){
			transitionStrategy[i] = Parameters.DEFAULT_WOMENS_TRANSITION_STRATEGY[i];
		}
		
		for(int i = 0; i < pacingStrategy.length; i++){
			pacingStrategy[i] = Parameters.DEFAULT_WOMENS_PACING_STRATEGY[i];
		}
		
	}
	
	
	public void evaluate(TeamPursuit teamPursuit){		
		try {
			result = teamPursuit.simulate(transitionStrategy, pacingStrategy);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	// this is a very basic fitness function
	// if the race is not completed, the chromosome gets fitness 1000
	// otherwise, the fitness is equal to the time taken
	// chromosomes that don't complete all get the same fitness (i.e regardless of whether they 
	// complete 10% or 90% of the race
	
	public double getFitness(){
		double fitness = 1000; // the less the better
		if (result == null || result.getProportionCompleted() < 0.999){
		    // race not completed, ran out of energy before finishing
            // System.out.println("completed: "+(1000 - (100 * result.getProportionCompleted())));
            //System.out.println("1000: "+((result.getProportionCompleted())));
            //System.out.println("finish time: " + result.getFinishTime());
            //System.out.println("energy remaining: " + result.getEnergyRemaining().length);
			return(500 - (100 * result.getProportionCompleted()));
			// return fitness; // before
		}
		else{
		    // race was completed
			fitness = result.getFinishTime();
			//System.out.println("finish time: " + result.getFinishTime());
			//System.out.println("energy remaining: " + result.getEnergyRemaining());
			//System.out.println(result.getEnergyRemaining().length);
			/*
			int energyRemaining = 0;
			for (int i = 0; i < result.getEnergyRemaining().length; i++) {
				energyRemaining += result.getEnergyRemaining()[i];
				//System.out.println("Cyclist " + (i+1) + " Energy Remaining: " + result.getEnergyRemaining()[i] + " joules\n");
			}
			//System.out.println("Team energy remaining: "+ energyRemaining);

			int yes = 0;
			if (yes <= 254)
			{
				// export to file
				try(FileWriter fw = new FileWriter("results/energy2.csv", true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw))
				{
					out.println(result.getFinishTime()+","+energyRemaining);
					//System.out.println(result.getFinishTime()+","+energyRemaining);
					yes++;
				}
				catch (IOException e) { e.printStackTrace(); }
			}

			 */

		}
		return fitness;
	}
	
	
	
	public Individual copy(){
		Individual individual = new Individual();
		for(int i = 0; i < transitionStrategy.length; i++){
			individual.transitionStrategy[i] = transitionStrategy[i];
		}		
		for(int i = 0; i < pacingStrategy.length; i++){
			individual.pacingStrategy[i] = pacingStrategy[i];
		}		
		individual.evaluate(EA.teamPursuit);	
		return individual;
	}
	
	@Override
	public String toString() {
		String str = "";
		if(result != null){
			str += getFitness();
		}
		return str;
	}

	public void print() {
		String best = "";
		String parameters = "";
		parameters = parameters
				+ Parameters.popSize + ","
                + Parameters.mimPopSize + ","
                + Parameters.reducePopSizeRate + ","
				+ Parameters.tournamentSize + ","
				+ Parameters.mutationRateMax + ","
				+ Parameters.mutationProbability + ","
				+ Parameters.crossoverProbability + ","
				+ Parameters.maxIterations + ",";

        System.out.println("\r\n" + this);
        best += this + "," + parameters;

		for(int i : pacingStrategy){
			System.out.print(i + ",");
			best += (i + ",");
		}
		System.out.println();
		for(boolean b : transitionStrategy){
			if(b){
				System.out.print("true,");
				best += "true,";
			}else{
				System.out.print("false,");
				best += "false,";
			}
		}

		// System.out.println("best: "+best);
		results += best;
		// System.out.println("-r: "+results);
	}
}
