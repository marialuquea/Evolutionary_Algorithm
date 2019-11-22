package ea;

/***
 * This is an example of an EA used to solve the problem
 *  A chromosome consists of two arrays - the pacing strategy and the transition strategy
 * This algorithm is only provided as an example of how to use the code and is very simple - it ONLY evolves the transition strategy and simply sticks with the default
 * pacing strategy
 * The default settings in the parameters file make the EA work like a hillclimber:
 * 	the population size is set to 1, and there is no crossover, just mutation
 * The pacing strategy array is never altered in this version- mutation and crossover are only
 * applied to the transition strategy array
 * It uses a simple (and not very helpful) fitness function - if a strategy results in an
 * incomplete race, the fitness is set to 1000, regardless of how much of the race is completed
 * If the race is completed, the fitness is equal to the time taken
 * The idea is to minimise the fitness value
 */

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import teamPursuit.TeamPursuit;
import teamPursuit.WomensTeamPursuit;

public class EA
{
	// create a new team with the default settings
	public static TeamPursuit teamPursuit = new WomensTeamPursuit();
	public String bestTime = "";
	private ArrayList<Individual> population = new ArrayList<Individual>();
	private int iteration = 0;
	
	public EA() {
		
	}


	public Individual bestI(){
		Individual best = getBest(population);
		return best;
	}
	
	public static void main(String[] args) {
		EA ea = new EA();
		// ea.run();
	}

	public void runAlgorithm() {
		initialisePopulation();	
		System.out.println("finished init pop: "+ population.size());
		iteration = 0;

		while(iteration < Parameters.maxIterations){
			iteration++;

			if(iteration % Parameters.reducePopSizeRate == 0)
			{
				removeIndividual();

				if(population.size() <= Parameters.mimPopSize){
					refillPopulation();
				}
			}

			Individual parent1 = RoulletteSelection();
			Individual parent2 = RoulletteSelection();
			Individual child = UniformCrossover(parent1, parent2);
			// child = mutate(child);
			child = mutate2(child);
			child.evaluate(teamPursuit);
			replace(child);
			printStats();
		}

		Individual best = getBest(population);
		best.print();
	}

	private void printStats() {		
		System.out.println("" + iteration + "\t best:" + getBest(population) + "\t \t worst:" + getWorst(population) + "\t pop size: " + population.size());
	}

	public void removeIndividual(){
		population.remove(getWorst(population));
	}

	private void refillPopulation() {
		while(population.size() < Parameters.popSize){
			Individual individual = new Individual();
			individual.initialise();
			individual.evaluate(teamPursuit);
			population.add(individual);
		}
	}

	private void replace(Individual child) {
		Individual worst = getWorst(population);
		if(child.getFitness() < worst.getFitness()){
			int idx = population.indexOf(worst);
			population.set(idx, child);
		}
	}

	private Individual mutate2(Individual child) {
		if(Parameters.rnd.nextDouble() > Parameters.mutationProbability)
			return child;

		int mutationRate = 1 + Parameters.rnd.nextInt(Parameters.mutationRateMax);

		for(int i = 0; i < mutationRate; i++)
		{
				//inverts a random tranition
				int tIndex = Parameters.rnd.nextInt(child.transitionStrategy.length);
				child.transitionStrategy[tIndex] = !child.transitionStrategy[tIndex];

				//
				int mutationChange = ThreadLocalRandom.current().nextInt(-50,50);
				int pIndex = Parameters.rnd.nextInt(child.pacingStrategy.length);

				if (child.pacingStrategy[pIndex] + mutationChange >= 200 && child.pacingStrategy[pIndex] + mutationChange <= 1200)
					child.pacingStrategy[pIndex] = child.pacingStrategy[pIndex] + mutationChange;
		}
		return child;
	}

	private Individual mutate(Individual child) {
		if(Parameters.rnd.nextDouble() > Parameters.mutationProbability){
			return child;
		}
		// choose how many elements to alter
		int mutationRate = 1 + Parameters.rnd.nextInt(Parameters.mutationRateMax);
		
		// mutate the transition strategy
			//mutate the transition strategy by flipping boolean value
			for(int i = 0; i < mutationRate; i++){
				int index = Parameters.rnd.nextInt(child.transitionStrategy.length);
				child.transitionStrategy[index] = !child.transitionStrategy[index];
			}
		return child;
	}

	private Individual RoulletteSelection()
	{
		Individual temp = new Individual();

		double sum = 0;
		for(Individual individual:population)
		{
			sum += 1 / individual.getFitness();
			temp = individual;
		}

		double random = ThreadLocalRandom.current().nextDouble(0, 1) * sum;

		double counter = 0;
		for(Individual individual:population)
		{
			counter += 1 / individual.getFitness();
			if (counter >= random)
				return individual;
		}

		return temp;
	}

	private Individual UniformCrossover(Individual parent1, Individual parent2) {
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability)
			return parent1;

		Individual child1 = new Individual();

		for(int i = 0; i < parent1.pacingStrategy.length; i++)
		{
			if(Parameters.rnd.nextInt(2) == 0)
				child1.pacingStrategy[i] = parent1.pacingStrategy[i];

			else
				child1.pacingStrategy[i] = parent2.pacingStrategy[i];
		}

		for(int i = 0; i < parent1.transitionStrategy.length; i++)
		{
			if(Parameters.rnd.nextInt(2) == 0)
				child1.transitionStrategy[i] = parent1.transitionStrategy[i];

			else
				child1.transitionStrategy[i] = parent2.transitionStrategy[i];
		}

		return child1;
	}

	// this needs fixing
	private Individual crossover(Individual parent1, Individual parent2) {
		// probability of crossover happening
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability)
			return parent1;

		// create empty child individual
		Individual child = new Individual();

		// pick cut point - random cut-point along chromosome length
		int crossoverPoint1 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
		int crossoverPoint2 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);

		int crossoverPoint3 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);
		int crossoverPoint4 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);

		// point 1 should be smaller than point 2
		if (crossoverPoint1 > crossoverPoint1)
		{
			int save = crossoverPoint1;
			crossoverPoint1 = crossoverPoint2;
			crossoverPoint2 = save;
		}

		// point 3 should be smaller than point 4
		if (crossoverPoint3 > crossoverPoint3)
		{
			int save = crossoverPoint3;
			crossoverPoint3 = crossoverPoint4;
			crossoverPoint4 = save;
		}
		
		// genes from parent 1 for childs transition strategy
		for(int i = 0; i < crossoverPoint1; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}

		// genes from parent 2 for childs transition strategy
		for(int i = crossoverPoint1; i < crossoverPoint2; i++){
			child.transitionStrategy[i] = parent2.transitionStrategy[i];
		}

		// genes from parent 1 for childs transition strategy
		for(int i = crossoverPoint2; i < parent1.transitionStrategy.length; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}


		// genes from parent 1 for childs pacing strategy
		for(int i = 0; i < crossoverPoint3; i++){
			child.pacingStrategy[i] = parent1.pacingStrategy[i];
		}

		// genes from parent 2 childs pacing strategy
		for(int i = crossoverPoint3; i < crossoverPoint4; i++){
			child.pacingStrategy[i] = parent2.pacingStrategy[i];
		}

		// genes from parent 1 childs pacing strategy
		for(int i = crossoverPoint4; i < parent2.transitionStrategy.length; i++){
			child.pacingStrategy[i] = parent2.pacingStrategy[i];
		}
		return child;
	}

	/**
	 * Returns a COPY of the individual selected using tournament selection
	 * @return
	 */
	private Individual tournamentSelection() {
		ArrayList<Individual> candidates = new ArrayList<Individual>();
		for(int i = 0; i < Parameters.tournamentSize; i++){
			candidates.add(population.get(Parameters.rnd.nextInt(population.size())));
		}
		return getBest(candidates).copy();
	}

	private Individual getBest(ArrayList<Individual> aPopulation) {
		double bestFitness = Double.MAX_VALUE;
		Individual best = null;
		for(Individual individual : aPopulation){
			if(individual.getFitness() < bestFitness || best == null){
				best = individual;
				bestFitness = best.getFitness();
			}
		}
		return best;
	}

	private Individual getWorst(ArrayList<Individual> aPopulation) {
		double worstFitness = 0;
		Individual worst = null;
		for(Individual individual : population){
			if(individual.getFitness() > worstFitness || worst == null){
				worst = individual;
				worstFitness = worst.getFitness();
			}
		}
		return worst;
	}
	
	private void printPopulation() {
		for(Individual individual : population){
			System.out.println(individual);
		}
	}

	private void initialisePopulation() {
		population.clear();
		while(population.size() < Parameters.popSize){
			Individual individual = new Individual();
			individual.initialise();			
			individual.evaluate(teamPursuit);
			population.add(individual);
							
		}		
	}	
}
