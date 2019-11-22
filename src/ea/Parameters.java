package ea;

import java.util.Random;

public class Parameters
{
	public static Random rnd = new Random(System.currentTimeMillis());
	static final boolean [] DEFAULT_WOMENS_TRANSITION_STRATEGY = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
	public static final int [] DEFAULT_WOMENS_PACING_STRATEGY = {300, 300, 300, 300, 300, 300, 300, 350, 350, 300, 300, 350, 350, 350, 350, 300, 300, 350, 350, 350, 350, 300, 300};
	public static int popSize = 100; // population size - initially 20 - 70
	public static int tournamentSize = 10; // selection process - initially 2
	public static int mutationRateMax = 6; // out of len - initially 6, can be up to 23
	public static double mutationProbability = 0.5; // initially 0.5
	public static double crossoverProbability = 1.0; //initially 1.0
	public static int maxIterations = 200; // initially 1000
}
