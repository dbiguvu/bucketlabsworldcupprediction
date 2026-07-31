package bucketlabsworldcupprediction;

import java.util.List;

// stores the output of the simulation on one fixture
public class PredictionResult {

	public final Fixture fixture; // which match the prediction is about

	public final double winProbA;  // probability that team A wins

	public final double drawProb; // probability that there's a draw

	public final double winProbB; // probability that team B wins

	public final double avgGoalsA;  // the average number of goals team A scored across all simulated matches

	public final double avgGoalsB;  // the average number of goals team B scored across all simulated matches

	public final List<ScorePrediction> topScores; // most likely EXACT scorelines (whole numbers), most likely first

	// constructor, stores all the values and allows us to create multiple PredictionResult objects
	public PredictionResult(Fixture fixture, double winProbA, double drawProb, double winProbB, double avgGoalsA,
			double avgGoalsB, List<ScorePrediction> topScores) {

		this.fixture = fixture;

		this.winProbA = winProbA;

		this.drawProb = drawProb;

		this.winProbB = winProbB;

		this.avgGoalsA = avgGoalsA;

		this.avgGoalsB = avgGoalsB;

		this.topScores = topScores;

	}

	// decides what the model's prediction is, compares 3 probabilities
	public String predictedOutcome() {

		if (winProbA >= drawProb && winProbA >= winProbB) {

			return "A";

		}

		if (winProbB >= drawProb && winProbB >= winProbA) {

			return "B";

		}

		else {

			return "DRAW";

		}
	}

	// returns how confident the model is in whatever it just predicted
	public double confidence() {

		switch (predictedOutcome()) {

		case "A":

			return winProbA;

		case "B":

			return winProbB;

		default:

			return drawProb;

		}
	}

	// turns the prediction into a readable sentence instead of just A or B
	public String predictedLabel() {

		switch (predictedOutcome()) {

		case "A":

			return fixture.teamA.name + " win";

		case "B":

			return fixture.teamB.name + " win";

		default:

			return "Draw";

		}

	}

	// the single most likely EXACT scoreline - whole numbers only, e.g. "2-1", never "2.0-1.0"
	public String topExactScore() {

		if (topScores.isEmpty())
			return "N/A";

		ScorePrediction best = topScores.get(0);

		return best.goalsA + "-" + best.goalsB;

	}

	// joins the top 3 exact scorelines into one readable string
	public String topScoresLine() {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < topScores.size(); i++) {

			if (i > 0)
				sb.append(", ");

			sb.append(topScores.get(i));

		}

		return sb.toString();

	}

	// builds the lines of text that gets printed to the console for each match
	@Override

	public String toString() {

		String mainLine = String.format(
				"%-28s vs %-28s  %s (%.1f%%) | A:%.1f%% D:%.1f%% B:%.1f%%",

				fixture.teamA.name, fixture.teamB.name, predictedLabel(), confidence() * 100, winProbA * 100,

				drawProb * 100, winProbB * 100);

		return mainLine + "\n" + "    Predicted final score: " + topExactScore()
				+ "  |  top 3: " + topScoresLine();

	}
}