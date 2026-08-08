package bucketlabsworldcupprediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

	// how many times each match gets simulated
	private static final int NUM_SIMULATIONS = 100_000;

	// main method
	public static void main(String[] args) throws IOException {

		// where to find the data
		String teamsPath = "data/teams.txt";
		String fixturesPath = "data/fixtures.txt";
		String resultsPath = "data/results.txt";

		// load the team's stats into a map
		Map<String, Team> teams = DataLoader.loadTeams(teamsPath);

		// load the full list of matchups (both rounds live in the same fixtures.txt)
		List<Fixture> allFixtures = DataLoader.loadFixtures(fixturesPath, teams);

		// create the actual simulation engine
		MonteCarloEngine engine = new MonteCarloEngine();

		List<PredictionResult> allPredictions = new ArrayList<>();

		System.out.println(" ROUND 3 PREDICTIONS (" + NUM_SIMULATIONS + " trials per match)\n");
		for (Fixture f : filterByGroup(allFixtures, "R3")) {
			PredictionResult result = engine.simulate(f, NUM_SIMULATIONS);
			allPredictions.add(result);
			System.out.println(f.matchId + ": " + result);
			System.out.println();
		}

		// rounds 1 and 2 are already played - simulate them silently (no printing) so the
		// accuracy evaluator below has something to compare against results.txt
		for (Fixture f : filterByGroup(allFixtures, "R1")) {
			PredictionResult result = engine.simulate(f, NUM_SIMULATIONS);
			allPredictions.add(result);
		}
		for (Fixture f : filterByGroup(allFixtures, "R2")) {
			PredictionResult result = engine.simulate(f, NUM_SIMULATIONS);
			allPredictions.add(result);
		}

		// compare rounds 1 and 2 predictions against the real results now on file -
		// round 3 rows in results.txt are still blank, so they're skipped automatically
		Map<String, ActualResult> actualResults = DataLoader.loadActualResults(resultsPath);
		AccuracyEvaluator.evaluate(allPredictions, actualResults);
	}

	// pulls out only the fixtures belonging to one round/group (e.g. "R1" or "R2")
	private static List<Fixture> filterByGroup(List<Fixture> fixtures, String group) {
		List<Fixture> filtered = new ArrayList<>();
		for (Fixture f : fixtures) {
			if (f.group.equalsIgnoreCase(group)) {
				filtered.add(f);
			}
		}
		return filtered;
	}
}