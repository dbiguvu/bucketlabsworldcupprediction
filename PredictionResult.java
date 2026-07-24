package bucketlabsworldcupprediction;

// stores the output of the simulation on one fixture
public class PredictionResult {

	public final Fixture fixture; // which match the prediction is about

	public final double winProbA;  // probability that team A wins

	public final double drawProb; // probability that there's a draw

	public final double winProbB; // probability that team B wins
	
	public final double avgGoalsA;  // the average number of goals team A scored across all simulated matches

	public final double avgGoalsB;  // the average number of goals team B scored across all simulated matches

	// constructor, stores all the values and allows us to create multiple PredictionResult objects
	public PredictionResult(Fixture fixture, double winProbA, double drawProb, double winProbB, double avgGoalsA,
			double avgGoalsB) {

		this.fixture = fixture;

		this.winProbA = winProbA;

		this.drawProb = drawProb;

		this.winProbB = winProbB;

		this.avgGoalsA = avgGoalsA;

		this.avgGoalsB = avgGoalsB;

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

	// builds the lines of text that gets printed to the console for each match
	@Override

	public String toString() {

		return String.format("%-28s vs %-28s  %s (%.1f%%) | A:%.1f%% D:%.1f%% B:%.1f%% | proj score %.1f-%.1f",

				fixture.teamA.name, fixture.teamB.name, predictedLabel(), confidence() * 100, winProbA * 100,

				drawProb * 100, winProbB * 100, avgGoalsA, avgGoalsB);

	}
}
