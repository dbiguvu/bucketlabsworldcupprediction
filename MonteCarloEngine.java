package bucketlabsworldcupprediction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// given two teams, it estimates how likely each outcome (A wins / draw / B wins) is, by simulating
// the match thousands of times with realistic randomness - and also tracks the exact scorelines
// that came up most often, so we can report actual whole-number score predictions too
public class MonteCarloEngine {

	// how much weight recent form (win% and loss%) gets in the "overall quality" adjustment
	private static final double FORM_WEIGHT = 60.0;

	// how much weight FIFA rank gets in the "overall quality" adjustment
	private static final double RANK_WEIGHT = 1.5;

	// how many of the most frequent exact scorelines to report per match
	private static final int TOP_SCORES_TRACKED = 3;

	// generates random numbers
	private final Random random;

	// constructor , used if a seed is provided (reproducible results - good for testing)
	public MonteCarloEngine(long seed) {

		this.random = new Random(seed);

	}

	// method overloading, used if no arguments are passed in when creating a MonteCarloEngine object
	public MonteCarloEngine() {

		 this.random = new Random();

	}

	//this method calculates what the probability that team A beats team B outright is, based on
	// Elo/form/rank - used as a smaller "overall quality" nudge on top of the real GF/GA math
	private double expectedScoreA(Team a, Team b) {

		double eloDiff = a.eloRating - b.eloRating; // tells us how much better A's Elo rating is than B's

		double formAdj = (a.recentFormScore() - b.recentFormScore()) * FORM_WEIGHT; // tells us how much better A's recent form is than B's

		double rankAdj = (b.fifaRank - a.fifaRank) * RANK_WEIGHT; // tells us how much better A's FIFA rank is than B's. lower rank numbers are better

		double combinedDiff = eloDiff + formAdj + rankAdj; // adds all three factors together to tell us how much better is A than B overall

		// runs that combined number through the standard Elo formula, returns a number between 0 and 1
		return 1.0 / (1.0 + Math.pow(10, -combinedDiff / 400.0));

	}

	// how many goals "scoringTeam" should be expected to score against "concedingTeam", using
	// ONLY real numbers: the plain average of scoringTeam's real goals-scored-per-match rate and
	// concedingTeam's real goals-conceded-per-match rate. No rescaling, no invented averages,
	// no ratios against a computed dataset mean - just two real per-match rates, averaged.
	private double expectedGoalsFromRealStats(Team scoringTeam, Team concedingTeam) {

		double raw = (scoringTeam.goalsForPerMatch() + concedingTeam.goalsAgainstPerMatch()) / 2.0;

		return Math.max(0.15, raw);

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

		// overall quality edge from Elo/form/rank, turned into a mild multiplier (0.85 - 1.15).
		// the real xG/goals-conceded/save% data does the main work now; this just nudges things.
		double expA = expectedScoreA(a, b);

		double qualityMultiplierA = 0.85 + 0.3 * expA;

		double qualityMultiplierB = 0.85 + 0.3 * (1 - expA);

		// base expected goals purely from real stats
		double baseLambdaA = expectedGoalsFromRealStats(a, b);

		double baseLambdaB = expectedGoalsFromRealStats(b, a);

		// final expected goals for this match: real-stats baseline, nudged by overall quality
		double lambdaA = Math.max(0.15, baseLambdaA * qualityMultiplierA);

		double lambdaB = Math.max(0.15, baseLambdaB * qualityMultiplierB);

		// these counters will track results across all the simulated matches
		long winsA = 0, winsB = 0, draws = 0;

		long totalGoalsA = 0, totalGoalsB = 0;

		// tracks how many times each exact scoreline ("2-1", "0-0", etc) came up
		Map<String, Long> scorelineCounts = new HashMap<>();

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

			scorelineCounts.merge(goalsA + "-" + goalsB, 1L, Long::sum);

		}

		// after all the simulations are done, turn the counts into percentages
		double winProbA = winsA / (double) numSimulations;

		double winProbB = winsB / (double) numSimulations;

		double drawProb = draws / (double) numSimulations;

		// calculate the average number of goals each team scored across all the simulated matches
		double avgGoalsA = totalGoalsA / (double) numSimulations;

		double avgGoalsB = totalGoalsB / (double) numSimulations;

		List<ScorePrediction> topScores = extractTopScorelines(scorelineCounts, numSimulations);

		return new PredictionResult(fixture, winProbA, drawProb, winProbB, avgGoalsA, avgGoalsB, topScores);
	}

	// turns the raw scoreline tally into a sorted list of the most frequent exact whole-number results
	private List<ScorePrediction> extractTopScorelines(Map<String, Long> counts, int numSimulations) {

		List<Map.Entry<String, Long>> entries = new ArrayList<>(counts.entrySet());

		entries.sort((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())); // highest count first

		List<ScorePrediction> topScores = new ArrayList<>();

		for (int i = 0; i < Math.min(TOP_SCORES_TRACKED, entries.size()); i++) {

			Map.Entry<String, Long> entry = entries.get(i);

			String[] parts = entry.getKey().split("-");

			int goalsA = Integer.parseInt(parts[0]);

			int goalsB = Integer.parseInt(parts[1]);

			double probability = entry.getValue() / (double) numSimulations;

			topScores.add(new ScorePrediction(goalsA, goalsB, probability));

		}

		return topScores;

	}
}