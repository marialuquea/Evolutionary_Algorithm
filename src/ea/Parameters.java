package ea;

import java.util.Random;

public class Parameters
{
	public static Random rnd = new Random(System.currentTimeMillis());
	static final boolean [] DEFAULT_WOMENS_TRANSITION_STRATEGY = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true}; // 22 genes
	public static final int [] DEFAULT_WOMENS_PACING_STRATEGY = {300, 300, 300, 300, 300, 300, 300, 350, 350, 300, 300, 350, 350, 350, 350, 300, 300, 350, 350, 350, 350, 300, 300}; // 23 genes
	public static int popSize = 30; // population size - initially 20 - 70
	public static int mimPopSize = 20; // minimum size the population gets reduced down to (for saw-tooth)
	public static int reducePopSizeRate = 30; // how frequently an individual is extracted from the population (for saw-tooth)
	public static int tournamentSize = 5; // selection process - initially 2
	public static int mutationRateMax = 0; // out of len - initially 6, can be up to 21
	public static double mutationProbability = 1.0; // initially 0.5
	public static double crossoverProbability = 1.0; //initially 1.0
	public static int maxIterations = 5000; // initially 1000

	public static String [] selection = {"roulette", "tournament"};
	public static String [] crossover = {"uniform", "multipoint", "onepoint"};
	public static String [] mutation = {"simple", "swap"};
	public static String [] diversity = {"hillclimber", "sawtooth"};

	// public static int [] pacingNumbers = {300, 350, 400, 450, 500, 550};
	// public static int [] bound = {50, 100, 150, 200, 250, 300};
}
