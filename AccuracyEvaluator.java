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

			// the outcome purely from the scoreline itself - "A", "B", or "DRAW". This is what
			// the model's probabilities are actually trying to forecast (90 minutes of expected
			// goals) - a penalty shootout winner is not something the model can see coming.
			String regulationOutcome = actual.regulationOutcome();

			String predicted = pr.predictedOutcome(); // "A", "B", or "DRAW"
			boolean hit = predicted.equals(regulationOutcome);
			if (hit)
				correct++;

			boolean exactHit = !pr.topScores.isEmpty()
					&& pr.topScores.get(0).goalsA == actual.goalsA
					&& pr.topScores.get(0).goalsB == actual.goalsB;
			if (exactHit)
				exactScoreCorrect++;

			// Brier score also uses the true regulation-time result - same reasoning as above.
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
					pr.fixture.matchId, predicted, regulationOutcome, actual.goalsA, actual.goalsB,
					actual.winnerName, decidedNote, hit ? "CORRECT" : "miss");
		}

		if (evaluated == 0) {
			System.out.println("No matches could be evaluated - no matching results found in results.txt.");
			return;
		}

		System.out.println();
		System.out.printf("Evaluated %d of %d total predictions%n", evaluated, predictions.size());
		System.out.printf("Accuracy (predicted outcome vs. actual scoreline): %d/%d (%.1f%%)%n",
				correct, evaluated, correct / (double) evaluated * 100.0);
		System.out.printf("Exact scoreline accuracy (top pick): %d/%d (%.1f%%)%n",
				exactScoreCorrect, evaluated, exactScoreCorrect / (double) evaluated * 100.0);
		System.out.printf("Average Brier score: %.4f  (0.0 = perfect, 2.0 = worst possible)%n",
				brierSum / evaluated);
		System.out.printf("%d of %d evaluated matches were decided beyond regulation (extra time/penalties)%n",
				decidedBeyondReg, evaluated);
	}
}
