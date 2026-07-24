package bucketlabsworldcupprediction;

//blueprint for each country's team
public class Team {

	public final String name;  // teams name

	public final int fifaRank;   // official rank of the team, 1 being the best

	public final double eloRating; // teams elo rating, another way to measure their strength

	public final double winPct;    // win percentage

	public final double drawPct;   // draw percentage

	public final double lossPct;   // loss percentage

	// constructor, allows us to make multiple team objects
	public Team(String name, int fifaRank, double eloRating, double winPct, double drawPct, double lossPct) {

		this.name = name;

		this.fifaRank = fifaRank;

		this.eloRating = eloRating;

		this.winPct = winPct;

		this.drawPct = drawPct;

		this.lossPct = lossPct;

	}

	// tells us the win percentage minus the lose percentage, positive means they win more
	public double recentFormScore() {

		return winPct - lossPct;

	}
	// overrides the default toString method, makes sure the teams name gets printed and not the reference it holds
	@Override
	public String toString() {

		return name;

	}
}