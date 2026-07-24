package bucketlabsworldcupprediction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// reads the data files, teams.txt and fixtures.txt
public class DataLoader {

	// Reads teams.txt and returns a Map where you can find any Team instantly just by typing its name as the key
	public static Map<String, Team> loadTeams(String path) throws IOException {

		// this will hold all the Team objects we build, indexed by name.
		Map<String, Team> teams = new HashMap<>();

		// opens the file for reading
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			String line = br.readLine(); // reads the first line and throws it away because the actual data is underneath

			// reads all the lines one at a time until there are no more left
			while ((line = br.readLine()) != null) {

				// skips any empty lines
				if (line.trim().isEmpty())
				
					continue;

				// turns the text into an array
				String[] p = line.split(",");

				//builds a team object out of those text pieces
				Team t = new Team(

						p[0].trim(), // name

						Integer.parseInt(p[1].trim()), // fifaRank

						Double.parseDouble(p[2].trim()), // elo

						Double.parseDouble(p[3].trim()), // winPct

						Double.parseDouble(p[4].trim()), // drawPct

						Double.parseDouble(p[5].trim()) // lossPct

				);

				// stores the team in the amp
				teams.put(t.name, t);

			}

		}

		return teams;

	}

	// reads fixtures.txt and returns a list of Fixture objects
	public static List<Fixture> loadFixtures(String path, Map<String, Team> teams) throws IOException {

		List<Fixture> fixtures = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			String line = br.readLine(); // header

			while ((line = br.readLine()) != null) {

				if (line.trim().isEmpty())
					
					continue;

				
				String[] p = line.split(",");

				String matchId = p[0].trim();

				String group = p[1].trim();

				String nameA = p[2].trim();

				String nameB = p[3].trim();

				// look up the team objects 
				Team a = teams.get(nameA);

				Team b = teams.get(nameB);

				//if a team wasn't found , print a warning
				if (a == null || b == null) {

					System.err.println("Skipping fixture " + matchId + ": missing team data for "

							+ (a == null ? nameA : nameB));

					continue;

				}

				//build the fixture and add it to the list
				fixtures.add(new Fixture(matchId, group, a, b));

			}
		}

		return fixtures;

	}

	
	public static Map<String, int[]> loadActualResults(String path) throws IOException {

		Map<String, int[]> results = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			String line = br.readLine(); // header

			while ((line = br.readLine()) != null) {

				if (line.trim().isEmpty())
					continue;

				String[] p = line.split(",");

				int scoreA = Integer.parseInt(p[1].trim());

				int scoreB = Integer.parseInt(p[2].trim());

				results.put(p[0].trim(), new int[] { scoreA, scoreB });

			}
		}

		return results;
	}
}
