package bucketlabsworldcupprediction;

//blueprint for each country's team
public class Team {

	public final String name;  // teams name

	public final int fifaRank;   // official rank of the team, 1 being the best

	public final double eloRating; // teams elo rating, another way to measure their strength

	public final double winPct;    // win percentage

	public final double drawPct;   // draw percentage

	public final double lossPct;   // loss percentage

	// these three fields are the ACTUAL, UNMODIFIED numbers from eloratings.net's pre-tournament
	// snapshot (dated Wednesday June 10 2026 - the day before the 2026 World Cup started).
	// No rescaling, no invented rating - just the real reported career totals.
	public final double goalsFor;      // real career Goals For total
	public final double goalsAgainst;  // real career Goals Against total
	public final double matchesPlayed; // real career total matches played

	// real International Caps count for this team's most-experienced (presumed starting)
	// goalkeeper, from a pre-tournament source. Caps = a real, complete, verifiable stat for
	// every keeper; no rating/save% column was usable since it only covered 12 of 32 teams.
	public final double goalkeeperCaps;

	// constructor, allows us to make multiple team objects
	public Team(String name, int fifaRank, double eloRating, double winPct, double drawPct, double lossPct,
			double goalsFor, double goalsAgainst, double matchesPlayed, double goalkeeperCaps) {

		this.name = name;

		this.fifaRank = fifaRank;

		this.eloRating = eloRating;

		this.winPct = winPct;

		this.drawPct = drawPct;

		this.lossPct = lossPct;

		this.goalsFor = goalsFor;

		this.goalsAgainst = goalsAgainst;

		this.matchesPlayed = matchesPlayed;

		this.goalkeeperCaps = goalkeeperCaps;

	}

	// tells us the win percentage minus the lose percentage, positive means they win more
	public double recentFormScore() {

		return winPct - lossPct;

	}

	// real goals scored per match - a direct division of two real numbers (goalsFor / matchesPlayed),
	// necessary because teams have wildly different career match totals, so raw goalsFor alone
	// would unfairly favor teams with a long history over teams with fewer matches on record
	public double goalsForPerMatch() {

		return goalsFor / matchesPlayed;

	}

	// real goals conceded per match - same reasoning as above
	public double goalsAgainstPerMatch() {

		return goalsAgainst / matchesPlayed;

	}

	// overrides the default toString method, makes sure the teams name gets printed and not the reference it holds
	@Override
	public String toString() {

		return name;

	}
}