package bucketlabsworldcupprediction;

public class Fixture {

	public final String matchId; // label for the match
	
	public final String group; // which group the match belongs to
	
	public final Team teamA; // first team in the match
	
	public final Team teamB; // second team in the match

	//constructor, allows us to make multiple fixtures by storing the match id , group and the two teams
	public Fixture(String matchId, String group, Team teamA, Team teamB) {
	
		this.matchId = matchId;
		
		this.group = group;
		
		this.teamA = teamA;
		
		this.teamB = teamB;
	
	}

}
