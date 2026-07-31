package bucketlabsworldcupprediction;

//blueprint for each country's team
public class Team {

	public final String name;  // teams name

	public final int fifaRank;   // official rank of the team, 1 being the best

	public final double eloRating; // teams elo rating, another way to measure their strength

	public final double winPct;    // win percentage

	public final double drawPct;   // draw percentage

	public final double lossPct;   // loss percentage

	// these three fields are the ACTUAL, UNMODIFIED numbers from FIFA's official team-statistics
	// page for the 2026 World Cup (Attacking / Goalkeeping tabs). No rescaling, no invented
	// rating scale - just the real reported values.
	public final double xG;             // real Expected Goals total, from the Attacking tab
	public final double goalsConceded;  // real Goals Conceded total, from the Goalkeeping tab
	public final double saves;          // real Goalkeeper Saves total, from the Goalkeeping tab

	// constructor, allows us to make multiple team objects
	public Team(String name, int fifaRank, double eloRating, double winPct, double drawPct, double lossPct,
			double xG, double goalsConceded, double saves) {

		this.name = name;

		this.fifaRank = fifaRank;

		this.eloRating = eloRating;

		this.winPct = winPct;

		this.drawPct = drawPct;

		this.lossPct = lossPct;

		this.xG = xG;

		this.goalsConceded = goalsConceded;

		this.saves = saves;

	}

	// tells us the win percentage minus the lose percentage, positive means they win more
	public double recentFormScore() {

		return winPct - lossPct;

	}

	// real save percentage - a direct computation from the two real numbers above
	// (saves / (saves + goals conceded)), not an invented rating
	public double savePct() {

		return saves / (saves + goalsConceded);

	}

	// overrides the default toString method, makes sure the teams name gets printed and not the reference it holds
	@Override
	public String toString() {

		return name;

	}
}