package bucketlabsworldcupprediction;

import java.util.List;
import java.util.Map;

// compares the model's predictions against real match results, to measure - and help improve - accuracy
public class AccuracyEvaluator {

	public static void evaluate(List<PredictionResult> predictions, Map<String, ActualResult> actualResults) {
		int evaluated = 0;
		int correct = 0;            // model's predicted winner vs whichever team actually advanced
		int exactScoreCorrect = 0;  // model's #1 predicted exact scoreline vs the real final score
		int decidedBeyondReg = 0;   // how many evaluated matches went to extra time or penalties
		double brierSum = 0.0;      // lower is better - 0.0 is a perfect forecast

		System.out.println("\n ACCURACY REPORT");
		

		for (PredictionResult pr : predictions) {
			ActualResult actual = actualResults.get(pr.fixture.matchId);
			if (actual == null) {
				continue; // hasn't been played yet, or no result recorded for it
			}
			evaluated++;

			// every knockout match has a team that actually moves on - even if regulation ended
			// in a tie and it was settled on penalties. This turns that into "A" or "B" for display.
			String actualLetter = actual.winnerName.equals(pr.fixture.teamA.name) ? "A" : "B";

			String predicted = pr.predictedOutcome(); // "A", "B", or "DRAW"
			boolean hit = predicted.equals(actualLetter);
			if (hit)
				correct++;

			boolean exactHit = !pr.topScores.isEmpty()
					&& pr.topScores.get(0).goalsA == actual.goalsA
					&& pr.topScores.get(0).goalsB == actual.goalsB;
			if (exactHit)
				exactScoreCorrect++;

			// Brier score still uses the true regulation-time result (A/DRAW/B from the scoreline
			// itself), since that's what the model's probabilities are actually trying to forecast -
			// a shootout winner isn't something the model can see coming.
			String regulationOutcome = actual.regulationOutcome();
			double actualA = regulationOutcome.equals("A") ? 1.0 : 0.0;
			double actualDraw = regulationOutcome.equals("DRAW") ? 1.0 : 0.0;
			double actualB = regulationOutcome.equals("B") ? 1.0 : 0.0;
			double matchBrier = Math.pow(pr.winProbA - actualA, 2)
					+ Math.pow(pr.drawProb - actualDraw, 2)
					+ Math.pow(pr.winProbB - actualB, 2);
			brierSum += matchBrier;

			if (actual.wasDecidedBeyondRegulation())
				decidedBeyondReg++;

			String decidedNote = actual.wasDecidedBeyondRegulation() ? " via " + actual.decidedBy : "";

			System.out.printf("%-8s predicted: %-5s  actual: %-5s (%d-%d, advanced: %s%s)  %s%n",
					pr.fixture.matchId, predicted, actualLetter, actual.goalsA, actual.goalsB,
					actual.winnerName, decidedNote, hit ? "CORRECT" : "miss");
		}



		System.out.println();
		System.out.printf("Evaluated %d of %d total predictions%n", evaluated, predictions.size());
		System.out.printf("Accuracy (predicted winner vs. who actually advanced): %d/%d (%.1f%%)%n",
				correct, evaluated, correct / (double) evaluated * 100.0);
		System.out.printf("Exact scoreline accuracy (top pick): %d/%d (%.1f%%)%n",
				exactScoreCorrect, evaluated, exactScoreCorrect / (double) evaluated * 100.0);
		System.out.printf("Average Brier score: %.4f  (0.0 = perfect, 2.0 = worst possible)%n",
				brierSum / evaluated);
		System.out.printf("%d of %d evaluated matches were decided beyond regulation (extra time/penalties)%n",
				decidedBeyondReg, evaluated);
	}
}