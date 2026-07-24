package bucketlabsworldcupprediction;

import java.util.Random;

// given two teams, it estimates how likely each outcome (A wins / draw / B wins) is, by simulating the match thousands of times with realistic randomness
public class MonteCarloEngine {

	// how much weight recent form (win% and loss%) gets in deciding who's stronger
	private static final double FORM_WEIGHT = 60.0;

	//how much weight FIFA rank gets in deciding who's stronger
	private static final double RANK_WEIGHT = 1.5;

	private static final double LEAGUE_AVG_GOALS = 1.35;

	// generates random numbers
	private final Random random;

	// constructor , used if a long is entered when creating a MonteCarloEngine object
	public MonteCarloEngine(long seed) {
	
		this.random = new Random(seed);
	
	}

	// method overloading, used if no arguments are passed in when creating a MonteCarloEngine object
	public MonteCarloEngine() {
	
		this(System.nanoTime());
	
	}

	//this method calculates what the probability that team A beats team B outright is
	private double expectedScoreA(Team a, Team b) {

		double eloDiff = a.eloRating - b.eloRating; // tells us how much better A's Elo rating is than B's

		double formAdj = (a.recentFormScore() - b.recentFormScore()) * FORM_WEIGHT; // tells us how much better A's recent form is than B's

		double rankAdj = (b.fifaRank - a.fifaRank) * RANK_WEIGHT; // tells us how much better A's FIFA rank is than B's. lower rank numbers are better

		double combinedDiff = eloDiff + formAdj + rankAdj; // adds all three factors together to tell us how much better is A than B overall

		// runs that combined number through the standard Elo formula, returns a number between 0 and 1
		return 1.0 / (1.0 + Math.pow(10, -combinedDiff / 400.0));

	}

	// this method estimates how many goals this team should score on average, in this particular match
	private double expectedGoals(double strengthMultiplier) {

		
		double adjusted = LEAGUE_AVG_GOALS * strengthMultiplier;

		//make sure the final number never drops to zero or below
		return Math.max(0.15, adjusted);

	}
  // generates one random, realistic goal count based on an average expected value
	private int samplePoisson(double lambda) {

		double L = Math.exp(-lambda);

		int k = 0;

		double p = 1.0;

		// do while loop, multiplies until p is less than L
		do {

			k++;

			p *= random.nextDouble();

		} while (p > L);

		return k - 1;

	}

	// runs the full Monte Carlo simulation
	public PredictionResult simulate(Fixture fixture, int numSimulations) {

		Team a = fixture.teamA;

		Team b = fixture.teamB;

		// figures out roughly how much stronger team A is than team B
		double expA = expectedScoreA(a, b);

		// converts that number into a strength multiplier for each team, stronger teams get a boost to their expected goals and weaker teams get a small reduction
		double strengthMultiplierA = 0.75 + 0.5 * expA;

		double strengthMultiplierB = 0.75 + 0.5 * (1 - expA);

		//calculates each team's expected average goals for this specific match
		double lambdaA = expectedGoals(strengthMultiplierA);

		double lambdaB = expectedGoals(strengthMultiplierB);

		// these counters will track results across all the simulated matches
		long winsA = 0, winsB = 0, draws = 0;

		long totalGoalsA = 0, totalGoalsB = 0;

		/* the monte carlo loop, randomly generates a score for both teams and records
		 who won. Does this thousands of times and looks at how often
		 each outcome happened, gives us realistic probabilities. */
		
		for (int i = 0; i < numSimulations; i++) {

			// randomly generate a goal count for each team in this one simulated match
			int goalsA = samplePoisson(lambdaA);

			int goalsB = samplePoisson(lambdaB);

			totalGoalsA += goalsA;

			totalGoalsB += goalsB;

			if (goalsA > goalsB)
				
				winsA++;
			
			else if (goalsB > goalsA)
			
				winsB++;
		
			else
			
				draws++;

		}

		// after all the simulations are done, turn the counts into percentages
		double winProbA = winsA / (double) numSimulations;

		double winProbB = winsB / (double) numSimulations;

		double drawProb = draws / (double) numSimulations;

		// calculate the average number of goals each team scored across all the simulated matches
		double avgGoalsA = totalGoalsA / (double) numSimulations;

		double avgGoalsB = totalGoalsB / (double) numSimulations;

		return new PredictionResult(fixture, winProbA, drawProb, winProbB, avgGoalsA, avgGoalsB);
	}
}