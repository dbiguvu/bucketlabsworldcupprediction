package bucketlabsworldcupprediction;

// one real-world match result: which group/round it belongs to, who actually advanced,
// the final score, and how it was decided (regulation, extra time, or penalties)
public class ActualResult {

	public final String group;      // which round this result belongs to (e.g. "R1")
	public final String winnerName; // the team that advanced
	public final int goalsA;        // final score for fixture.teamA (90 minutes, before any shootout)
	public final int goalsB;        // final score for fixture.teamB (90 minutes, before any shootout)
	public final String decidedBy;  // "REG" (regulation), "AET" (extra time), or "PENS" (penalties)

	public ActualResult(String group, String winnerName, int goalsA, int goalsB, String decidedBy) {
		this.group = group;
		this.winnerName = winnerName;
		this.goalsA = goalsA;
		this.goalsB = goalsB;
		this.decidedBy = decidedBy;
	}

	// the outcome purely from the scoreline - "A", "B", or "DRAW". For AET/PENS matches this
	// will usually be "DRAW", since that's what the scoreline actually shows before the shootout -
	// the model only simulates 90 minutes and was never trying to predict who wins a shootout.
	public String regulationOutcome() {
		if (goalsA > goalsB)
			return "A";
		if (goalsB > goalsA)
			return "B";
		return "DRAW";
	}

	// true if this match needed extra time or penalties to be settled
	public boolean wasDecidedBeyondRegulation() {
		return decidedBy.equals("AET") || decidedBy.equals("PENS");
	}
}