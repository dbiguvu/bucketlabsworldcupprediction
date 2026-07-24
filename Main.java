package bucketlabsworldcupprediction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

	// how many times each match gets simulated
	private static final int NUM_SIMULATIONS = 100_000;

	// main method
	public static void main(String[] args) throws IOException {

		// where to find the data, and where to save the output
		String teamsPath = "data/teams.txt";

		String fixturesPath = "data/fixtures.txt";

		String outputPath = "data/predictions.txt";

		// load the team's stats into a map
		Map<String, Team> teams = DataLoader.loadTeams(teamsPath);

		//load the list of matchups
		List<Fixture> fixtures = DataLoader.loadFixtures(fixturesPath, teams);

		// create the actual simulation engine
		MonteCarloEngine engine = new MonteCarloEngine();

		// collects the prediction result for each match
		List<PredictionResult> predictions = new ArrayList<>();

		System.out.println(" 2026 WORLD CUP - FIRST ROUND PREDICTIONS (" + fixtures.size() + " matches, "
				+ (fixtures.size() * 2) + " teams)");

		System.out.println(" Monte Carlo simulation: " + NUM_SIMULATIONS + " trials per match\n");

		// go through each fixture one at a time
		for (Fixture f : fixtures) {

			PredictionResult result = engine.simulate(f, NUM_SIMULATIONS);

			predictions.add(result);

		
			System.out.println(f.matchId + ": " + result);

		}

		// prints the prediction
		writePredictionsFile(outputPath, predictions);

		System.out.println("\n" + predictions.size() + " predictions written to " + outputPath);

	}

	// helper method, writes all the predictions out to a text file
	private static void writePredictionsFile(String path, List<PredictionResult> predictions) throws IOException {

		try (FileWriter fw = new FileWriter(path)) {

			// writes the header row
			fw.write("matchId,teamA,teamB,winProbA,drawProb,winProbB,predicted,confidence,projScoreA,projScoreB\n");

			for (PredictionResult pr : predictions) {

				// writes one line for each prediction - %s for text %.4f for decimals with 4 digits, and more
				fw.write(String.format("%s,%s,%s,%.4f,%.4f,%.4f,%s,%.4f,%.2f,%.2f%n",

						pr.fixture.matchId, pr.fixture.teamA.name, pr.fixture.teamB.name,

						pr.winProbA, pr.drawProb, pr.winProbB,

						pr.predictedLabel(), pr.confidence(), pr.avgGoalsA, pr.avgGoalsB));

			}
		}
	}
}