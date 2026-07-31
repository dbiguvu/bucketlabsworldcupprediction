package bucketlabsworldcupprediction;

// one possible exact scoreline (whole numbers only, e.g. "2-1"), and how often it came up
// across all the simulated matches
public class ScorePrediction {

	public final int goalsA; // whole number of goals for team A in this exact scoreline

	public final int goalsB; // whole number of goals for team B in this exact scoreline

	public final double probability; // fraction of simulations that produced this exact scoreline

	public ScorePrediction(int goalsA, int goalsB, double probability) {

		this.goalsA = goalsA;

		this.goalsB = goalsB;

		this.probability = probability;

	}

	// readable form, e.g. "2-1 (12.4%)" - always whole numbers, never decimals
	@Override
	public String toString() {

		return String.format("%d-%d (%.1f%%)", goalsA, goalsB, probability * 100);

	}
}