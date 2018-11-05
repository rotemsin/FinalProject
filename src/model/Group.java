package model;

import java.util.ArrayList;
import java.util.List;

/**
 * model.Group class
 */
public class Group {

    private int number;
    private List<Team> teamsList;
    private List<Match> matchesList;
    private List<Integer> pointsList;
    private List<Team> finalRankingList;

    /**
     * model.Group constructor
     * @param number - the group number
     * @param teamsList - the list of teams in the group
     */
    public Group(int number, List<Team> teamsList) {
        this.number = number;
        this.teamsList = teamsList;
        matchesList = new ArrayList<>();
        setMatchesList();
        pointsList = new ArrayList<>();
        initPointsList();
        finalRankingList = new ArrayList<>();
    }

    /**
     * This method sets up the matches in a group
     */
    private void setMatchesList() {
        for (Team team1: teamsList) {
            for (Team team2: teamsList) {
                if (team1.getIndex() != team2.getIndex()) {
                    Match matchToAdd = new Match(team1, team2);
                    boolean matchFound = false;
                    for (Match match: matchesList) {
                        if (matchToAdd.equals(match))
                            matchFound = true;
                    }
                    if (!matchFound)
                        matchesList.add(matchToAdd);
                }
            }
        }
    }

    /**
     * This method initializes the number of points for each team in a group
     */
    public void initPointsList() {
        for (Team team: teamsList) {
            pointsList.add(0);
        }
    }

    /**
     * This method adds the points scored by a winning team in the group rankings
     * @param winningTeam - the team that won the match
     */
    public void updateRanking(Team winningTeam) {
        int i = 0;
        for (Team team: teamsList) {
            if (team.getIndex() == winningTeam.getIndex())
                pointsList.set(i, pointsList.get(i) + 3);
            else
                i++;
        }
    }

    /**
     * This method calculates the final ranking of a group after all the matches were completed
     */
    public void determineRanking() {
        int maxValue = 0;
        int nextTeamIndex = 0;
        for (int i = 0; i < pointsList.size(); i++) {
            if (pointsList.get(i) > maxValue) {
                maxValue = pointsList.get(i);
                nextTeamIndex = i;
            }
        }
        finalRankingList.add(teamsList.get(nextTeamIndex));
        pointsList.remove(nextTeamIndex);
        teamsList.remove(nextTeamIndex);
    }

    /**
     * This method calculates which teams go through to the next round after all the matches were completed
     * @param size - the number of teams in the group
     * @return - A list of the teams that got through to the next round
     */
    public List<Team> getNextRoundTeams(int size) {
        List<Team> nextRoundTeamsList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            nextRoundTeamsList.add(finalRankingList.get(i));
        }
        return nextRoundTeamsList;
    }

    /**
     * Getter for matches' list
     * @return - the list of the matches in a group
     */
    public List<Match> getMatchesList() {
        return matchesList;
    }

    /**
     * Getter for teams' list
     * @return - A list of the teams in a group
     */
    public List<Team> getTeamsList() {
        return teamsList;
    }

    /**
     * Getter for the group number
     * @return - the group's number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Getter for the list of teams sorted by their ranking
     * @return - A list of the teams in the group sorted by their ranking
     */
    public List<Team> getFinalRankingList() {
        return finalRankingList;
    }
}
