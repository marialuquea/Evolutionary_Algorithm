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
	
	public EA() { }

	public static void main(String[] args) {
		EA ea = new EA();
		// ea.run();
	}

	public void runAlgorithm(int start, int bound, String selection, String crossover, String mutation, String diversity) {
		initialisePopulation(start, bound);
		System.out.println("finished init pop: "+ population.size());
		iteration = 0;
		while(iteration < Parameters.maxIterations){
			iteration++;
			if((diversity.equals("sawtooth")) && iteration % Parameters.reducePopSizeRate == 0)
			{
				// SAWTOOTH
				removeIndividual(); // every 30 iterations, remove worst individual
				if(population.size() <= Parameters.mimPopSize)
					refillPopulation(start, bound); // refill when limit is reached
			}
			// SELECTION OPTIONS
			Individual parent1 = new Individual();
			Individual parent2 = new Individual();
			if (selection.equals("roulette")){
				parent1 = RouletteSelection();
				parent2 = RouletteSelection(); }
			if (selection.equals("tournament")){
				parent1 = tournamentSelection();
				parent2 = tournamentSelection(); }

			// CROSSOVER OPTIONS
			Individual child = new Individual();
			if (crossover.equals("uniform"))
				child = UniformCrossover(parent1, parent2);
			if (crossover.equals("multipoint"))
				child = multipoint(parent1, parent2);
			if (crossover.equals("onepoint"))
				child = crossover_initial(parent1, parent2);

			// MUTATION OPTIONS
			if (mutation.equals("simple"))
				child = mutate(child);
			if (mutation.equals("swap"))
				child = swapMutation(child);

			child.evaluate(teamPursuit);
			replace(child);
			printStats();
		}
		Individual best = getBest(population);
		best.print();
		//if (diversity.equals("hillclimber"))
		hill_climber(best);
	}

	private void printStats() {		
		System.out.println("" + iteration + "\t best:" + getBest(population) + "\t \t worst:" + getWorst(population) + "\t pop size: " + population.size());
	}

	// INITIALISE
	private void initialisePopulation(int start, int bound) {
		population.clear();
		while(population.size() < Parameters.popSize){
			Individual individual = new Individual();
			individual.initialise(start, bound);
			individual.evaluate(teamPursuit);
			population.add(individual);

		}
	}


	// SELECTION METHODS
	private Individual RouletteSelection() {
		// the lower the race time, the higher the fitness
		Individual parent = new Individual(); // new empty parent

		// adding circle portions to a total circle area
		double total = 0; // adds up all fitnesses
		for(Individual i : population)
		{
			// reverse it so a smaller fitness represents the biggest part of the circle and therefore has a higher change of being selected to be a parent
			total += 1 / i.getFitness(); // total size of circle being added to total
			parent = i;
		}

		// spinner - percentage of circle (0-1) * area of circle gives a position in the circle
		double spinner = total * ThreadLocalRandom.current().nextDouble(0, 1); // create a random number between 0 and 1

		double count = 0; // go through all the circle until we reach the spinner.
		for(Individual i : population) {
			count += (1 / i.getFitness());
			if (count >= spinner) // when the spinner is reached, return that parent
				return i;
		}
		return parent;
	}

	private Individual tournamentSelection() {
		ArrayList<Individual> candidates = new ArrayList<Individual>();
		for(int i = 0; i < Parameters.tournamentSize; i++){ // choose a number of chromosomes to participate in the tournament
			candidates.add(population.get(Parameters.rnd.nextInt(population.size()))); // add random pop to array
		}
		return getBest(candidates).copy(); // return best one from array
	}


	// CROSSOVER METHODS
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

	private Individual multipoint(Individual parent1, Individual parent2) {
		// crossover probability
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability)
			return parent1;

		Individual child = new Individual(); // new empty child

		// PACING
		// random crossover points
		int pacingCrossoverPoint1 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);
		int pacingCrossoverPoint2 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);

		// make sure first crossover point is before second one
		if(pacingCrossoverPoint1 > pacingCrossoverPoint2)
		{
			int temp = pacingCrossoverPoint1;
			pacingCrossoverPoint1 = pacingCrossoverPoint2;
			pacingCrossoverPoint2 = temp;
		}


		for(int i = 0; i < pacingCrossoverPoint1; i++)
			child.pacingStrategy[i] = parent1.pacingStrategy[i];

		for(int i = pacingCrossoverPoint1; i < pacingCrossoverPoint2; i++)
			child.pacingStrategy[i] = parent2.pacingStrategy[i];

		for(int i = pacingCrossoverPoint2; i < parent1.pacingStrategy.length; i++)
			child.pacingStrategy[i] = parent1.pacingStrategy[i];

		// TRANSITION
		int transitionCrossoverPoint1 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
		int transitionCrossoverPoint2 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);

		if(transitionCrossoverPoint1 > transitionCrossoverPoint2)
		{
			int temp = transitionCrossoverPoint1;
			transitionCrossoverPoint1 = transitionCrossoverPoint2;
			transitionCrossoverPoint2 = temp;
		}

		for(int i = 0; i < transitionCrossoverPoint1; i++)
			child.transitionStrategy[i] = parent1.transitionStrategy[i];

		for(int i = transitionCrossoverPoint1; i < transitionCrossoverPoint2; i++)
			child.transitionStrategy[i] = parent2.transitionStrategy[i];

		for(int i = transitionCrossoverPoint2; i < parent1.transitionStrategy.length; i++)
			child.transitionStrategy[i] = parent1.transitionStrategy[i];

		return child;
	}

	private Individual crossover_initial(Individual parent1, Individual parent2) {
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability){
			return parent1;
		}
		Individual child = new Individual();

		int crossoverPoint_t = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
		int crossoverPoint_p = Parameters.rnd.nextInt(parent1.pacingStrategy.length);

		// transition
		for(int i = 0; i < crossoverPoint_t; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}
		for(int i = crossoverPoint_t; i < parent2.transitionStrategy.length; i++){
			child.transitionStrategy[i] = parent2.transitionStrategy[i];
		}
		// pacing
		for(int i = 0; i < crossoverPoint_p; i++){
			child.pacingStrategy[i] = parent1.pacingStrategy[i];
		}
		for(int i = crossoverPoint_p; i < parent2.pacingStrategy.length; i++){
			child.pacingStrategy[i] = parent2.pacingStrategy[i];
		}

		return child;
	}


	// MUTATION METHODS
	private Individual mutate(Individual child) {

		// mutation probability
		if(Parameters.rnd.nextDouble() > Parameters.mutationProbability)
			return child;

		// mutate 1 to 6 genes
		int mutationRate = 1 + Parameters.rnd.nextInt(Parameters.mutationRateMax);

		for(int i = 0; i < mutationRate; i++)
		{
			// inverts a random transition from true to false and vice versa
			int index_transition = Parameters.rnd.nextInt(child.transitionStrategy.length);
			child.transitionStrategy[index_transition] = !child.transitionStrategy[index_transition];

			// the bigger the mutation, the more diversity
			int mutationChange = 200 - Parameters.rnd.nextInt(100);
			int index_pacing = Parameters.rnd.nextInt(child.pacingStrategy.length);

			// ensure mutation is valid
			if (child.pacingStrategy[index_pacing] + mutationChange >= 200 && child.pacingStrategy[index_pacing] + mutationChange <= 1200)
				child.pacingStrategy[index_pacing] = child.pacingStrategy[index_pacing] + mutationChange;
		}
		return child;
	}

	private Individual swapMutation(Individual child){

		// mutation probability
		if(Parameters.rnd.nextDouble() > Parameters.mutationProbability)
			return child;

		// transition strategy swap
		int index1 = Parameters.rnd.nextInt(child.transitionStrategy.length);
		int index2 = Parameters.rnd.nextInt(child.transitionStrategy.length);
		Boolean swap1 = child.transitionStrategy[index1];
		Boolean swap2 = child.transitionStrategy[index2];
		Boolean temp = swap1;
		child.transitionStrategy[index1] = swap2;
		child.transitionStrategy[index2] = temp;

		// pacing strategy swap
		index1 = Parameters.rnd.nextInt(child.pacingStrategy.length);
		index2 = Parameters.rnd.nextInt(child.pacingStrategy.length);
		int swap_1 = child.pacingStrategy[index1];
		int swap_2 = child.pacingStrategy[index2];
		int temp_ = swap_1;
		child.pacingStrategy[index1] = swap_2;
		child.pacingStrategy[index2] = swap_1;

		return child;
	}


	// OTHER
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

	public Individual getBestIndividual(){
		Individual best = getBest(population);
		return best;
	}

	private void replace(Individual child) {
		Individual worst = getWorst(population);
		if(child.getFitness() < worst.getFitness()){
			int idx = population.indexOf(worst);
			population.set(idx, child);
		}
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


	// DIVERSITY - SAWTOOTH & HILL CLIMBER
	public void removeIndividual(){	population.remove(getWorst(population)); }

	private void refillPopulation(int start, int bound) {
		// refill population with random seeds
		while(population.size() < Parameters.popSize){
			Individual individual = new Individual();
			individual.initialise(start, bound);
			individual.evaluate(teamPursuit);
			population.add(individual);
		}
	}

	private Individual hill_climber(Individual i){

		System.out.println("\n---HILL CLIMBER---");
		Individual best = i.copy();
		Individual temp = i.copy(); // the best seed after running the EA
		double before = temp.getFitness();

		for (int j = 0; j < 100; j++)
		{
			// flip one random transition gene
			int index_transition = Parameters.rnd.nextInt(temp.transitionStrategy.length);
			temp.transitionStrategy[index_transition] = !temp.transitionStrategy[index_transition];

			// change the value of 1 pacing value
			int mutationChange = 200 - Parameters.rnd.nextInt(100);
			int index_pacing = Parameters.rnd.nextInt(temp.pacingStrategy.length);

			// ensure mutation is valid
			if (temp.pacingStrategy[index_pacing] + mutationChange >= 200 && temp.pacingStrategy[index_pacing] + mutationChange <= 1200)
				temp.pacingStrategy[index_pacing] = temp.pacingStrategy[index_pacing] + mutationChange;

			temp.evaluate(EA.teamPursuit); // evaluate new individual

			// if after mutation, individual is better, now work on that one
			if (temp.getFitness() <  best.getFitness()) {
				best = temp.copy();
				System.out.println("better: "+best.getFitness()+" at iteration "+j);
			}
			else
				temp = best.copy(); // forget about the mutated allele and work on first one
		}
		System.out.println("best: "+best.getFitness());
		return best;
	}



}
