package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * model.Tournament class
 */
public class Tournament {

    private ProbabilityMatrix probabilityMatrix;
    private DatabaseConnection dbConnection;
    private FileManager fileManager;
    private Heuristic heuristic;
    private Team favouriteTeam;
    private RunType runType;

    private List<Team> teamsList;
    private List<Group> groupsList;
    private Map<Team, Double> firstPlaceOddsMap;
    private Map<Team, Double> secondPlaceOddsMap;
    private Map<Team, Double> topHalfOddsMap;
    private Map<Team, Double> bottomHalfOddsMap;
    private Map<Team, Double> championOddsMap;

    /**
     * model.Tournament constructor
     * @param favouriteTeamName - the name of the favourite team
     * @param heuristic - the selected heuristic
     */
    public Tournament(String favouriteTeamName, ProbabilityMatrix matrix, Heuristic heuristic,
                      DatabaseConnection dbConnection, FileManager fileManager, RunType runType){
        this.heuristic = heuristic;
        this.dbConnection = dbConnection;
        this.fileManager = fileManager;
        this.runType = runType;
        teamsList = new ArrayList<>();
        setTeamsList();
        favouriteTeam = getFavouriteTeam(favouriteTeamName);
        this.probabilityMatrix = matrix;
        groupsList = new ArrayList<>();
        createGroups(teamsList, 8, heuristic);
        setUpdatedTeamsList();
        firstPlaceOddsMap = new HashMap<>();
        secondPlaceOddsMap = new HashMap<>();
        topHalfOddsMap = new HashMap<>();
        bottomHalfOddsMap = new HashMap<>();
        championOddsMap = new HashMap<>();
    }

    /**
     * This method adds the teams to the teams' list
     */
    public void setTeamsList() {
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/teams.txt"))) {
            String input;
            int index = 0;
            while((input = reader.readLine()) != null) {
                teamsList.add(new Team(input, index));
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method resets the teams list after the groups have been created and the original teams list was changed in order for
     * the groups to be created
     */
    private void setUpdatedTeamsList() {
        for (Group group: groupsList) {
            for (Team team: group.getTeamsList()) {
                teamsList.add(team);
            }
        }
    }

    /**
     * Favourite team getter
     * @param teamName - the team's name
     * @return - the favourite team
     */
    public Team getFavouriteTeam(String teamName) {
        for (Team team: teamsList) {
            if (team.getName().equals(teamName))
                favouriteTeam = team;
        }
        return favouriteTeam;
    }

    /**
     * This method creates the groups in the tournament
     * @param otherTeams - the teams other than the favourite team
     * @param numberOfGroups - the number of groups to be created
     * @param heuristic - the selected heuristic
     */
    public void createGroups(List<Team> otherTeams, int numberOfGroups, Heuristic heuristic) {
        otherTeams.remove(favouriteTeam.getIndex());
        List<Team> groupATeams;
        if (heuristic == Heuristic.BEST_WIN)
            groupATeams = probabilityMatrix.findOpponentsByBestWin(otherTeams, favouriteTeam, 3);
        else
            groupATeams = probabilityMatrix.findOpponentsByFarAdversary(heuristic, otherTeams, favouriteTeam, 3);
        groupATeams.add(favouriteTeam);
        Group groupA = new Group(1, groupATeams);
        groupsList.add(groupA);
        if (heuristic == Heuristic.BEST_WIN) {
            Team nextTeam = groupA.getTeamsList().get(groupA.getTeamsList().size() - 2);
            for (int i = 2; i <= numberOfGroups; i++) {
                List<Team> groupTeams = probabilityMatrix.findOpponentsByBestWin(otherTeams, nextTeam, 4);
                Group nextGroup = new Group(i, groupTeams);
                groupsList.add(nextGroup);
                nextTeam = nextGroup.getTeamsList().get(nextGroup.getTeamsList().size() - 1);
            }
        } else {
            for (int i = 2; i <= numberOfGroups; i++) {
                List<Team> groupTeams = probabilityMatrix.findOpponentsByFarAdversary(heuristic, otherTeams,
                        favouriteTeam, 4);
                groupsList.add(new Group(i, groupTeams));
            }
        }
        if (runType == RunType.SINGLE_RUN) {
            try {
                fileManager.writeGroupDetailsToFile(groupsList);
                dbConnection.updateTeamRecords(groupsList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method runs the matches in all the groups and computes the odds for the teams to go through to the next round
     * in first or second place
     */
    public void runGroupStage() {
        double[][] oddsMatrix = probabilityMatrix.getMatrix();
        for (Group group : groupsList) {
            List<String> binaryStringsList = new ArrayList<>();
            createBinaryResults("", group.getMatchesList().size(), binaryStringsList);
            List<Double> firstPlaceInGroup = Arrays.asList(0.0, 0.0, 0.0, 0.0);
            List<Double> secondPlaceInGroup = Arrays.asList(0.0, 0.0, 0.0, 0.0);
            for (int i = 0; i < binaryStringsList.size(); i++) { //cover all scenarios
                List<Team> tempTeamsList = new ArrayList<>(group.getTeamsList());
                Group tempGroup = new Group(group.getNumber(), tempTeamsList);
                double currentOdd = runScenario(binaryStringsList.get(i), tempGroup, oddsMatrix);
                List<Team> nextRoundTeamsListFromGroup = tempGroup.getNextRoundTeams(tempGroup.getFinalRankingList().size() / 2);
                for (int j = 0; j < group.getTeamsList().size(); j++) {
                    for (int k = 0; k < nextRoundTeamsListFromGroup.size(); k++) {
                        if (group.getTeamsList().get(j).getIndex() == nextRoundTeamsListFromGroup.get(k).getIndex()) {
                            if (k == 0) {
                                double newOdd = firstPlaceInGroup.get(j) + currentOdd;
                                firstPlaceInGroup.set(j, newOdd);
                            }
                            if (k == 1) {
                                double newOdd = secondPlaceInGroup.get(j) + currentOdd;
                                secondPlaceInGroup.set(j, newOdd);
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < group.getTeamsList().size(); i++) {
                firstPlaceOddsMap.put(group.getTeamsList().get(i), firstPlaceInGroup.get(i));
                secondPlaceOddsMap.put(group.getTeamsList().get(i), secondPlaceInGroup.get(i));
            }
        }
        if (runType == RunType.SINGLE_RUN) {
            try {
                fileManager.writeGroupOddsToFile(firstPlaceOddsMap, secondPlaceOddsMap, teamsList);
                dbConnection.updateGroupOdds(firstPlaceOddsMap, secondPlaceOddsMap, teamsList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method "runs" a scenario (which is a binary string, a representation of a win or a loss of team 1 over team 2
     * over all matches in a group) as well as calculating the final ranking of the group
     * @param binaryString - a binary representation of the results of all the matches in the scenario
     * @param tempGroup - a temporary group on which the calculations of the scenario will be made
     * @param oddsMatrix - the matrix of the odds of each team to beat all other teams
     * @return - the odd of the scenario to happen (the multiplication of all the odds of the results in the scenario)
     */
    private double runScenario(String binaryString, Group tempGroup, double[][] oddsMatrix) {
        double odd = 1.0;
        for (int i = 0; i < tempGroup.getMatchesList().size(); i++) { //cover all matches
            Team winningTeam;
            Team losingTeam;
            if (binaryString.charAt(i) == '0') {
                winningTeam = tempGroup.getMatchesList().get(i).getTeam1();
                losingTeam = tempGroup.getMatchesList().get(i).getTeam2();
            } else {
                winningTeam = tempGroup.getMatchesList().get(i).getTeam2();
                losingTeam = tempGroup.getMatchesList().get(i).getTeam1();
            }
            tempGroup.updateRanking(winningTeam);
            odd *= oddsMatrix[winningTeam.getIndex()][losingTeam.getIndex()];
        }
        while (tempGroup.getTeamsList().size() != 0) {
            tempGroup.determineRanking();
        }
        return odd;
    }

    /**
     * This recursive method creates all the possible results (scenarios) of a group in the form of a binary string, with 1 representing a
     * win for team 1 and 0 representing a win for team 2
     * @param soFar - the string so far
     * @param iterations - the number of iterations remaining until the string is complete
     * @param binaryStringsList - the list of all possible strings (scenarios)
     */
    private void createBinaryResults(String soFar, int iterations, List<String> binaryStringsList) {
        if (iterations == 0)
            binaryStringsList.add(soFar);
        else {
            createBinaryResults(soFar + "0", iterations - 1, binaryStringsList);
            createBinaryResults(soFar + "1", iterations - 1, binaryStringsList);
        }
    }

    /**
     * This method computes the odds of all the teams to win the tournament based on their odds to go through to the
     * knockout stage
     */
    public void runKnockoutStage() {
        double[][] oddsMatrix = probabilityMatrix.getMatrix();
        int endIndex = 4;
        boolean goForward = true;
        int j = endIndex;
        runKnockoutStagePart1(oddsMatrix, goForward, endIndex, j);
        endIndex = 8;
        goForward = true;
        runKnockoutStagePart2(oddsMatrix, goForward, endIndex);
        runKnockoutStagePart3(oddsMatrix);
    }

    /**
     * This helper method computes the odds of all the teams to go through the first round of the knockout stage
     * by dividing the odds of all the teams to play in the top half or the bottom half of the draw, according to their
     * odds to finish in first or second place in the group stage
     * @param oddsMatrix - the matrix of the odds of each team to beat all other teams
     * @param goForward - a boolean value which determines whether to compute the odds by going forwards (if the team was in an
     *        odd-numbered group) or backwards (if the team was in an even-numbered group)
     * @param endIndex - the last index to which the computation is to continue (in this method it is 4)
     * @param j - the current index of the odds computation which reaches 0 or lastIndex * 2 (depending on the value of goForward)
     */
    public void runKnockoutStagePart1(double[][] oddsMatrix, boolean goForward, int endIndex, int j) {
        for (int i = 0; i < teamsList.size(); i++) {
            double newOddTopHalf = 0;
            double newOddBottomHalf = 0;
            if (goForward) {
                for (int k = 0; k < endIndex; k++) {
                    newOddTopHalf += (oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(i + j + k).getIndex()] *
                            (firstPlaceOddsMap.get(teamsList.get(i)) * secondPlaceOddsMap.get(teamsList.get(i + j + k))));
                    newOddBottomHalf += (oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(i + j + k).getIndex()] *
                            (secondPlaceOddsMap.get(teamsList.get(i)) * firstPlaceOddsMap.get(teamsList.get(i + j + k))));
                }
                j--;
            } else {
                for (int k = 0; k < endIndex; k++) {
                    newOddTopHalf += (oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(i - j + k).getIndex()] *
                            (secondPlaceOddsMap.get(teamsList.get(i)) * firstPlaceOddsMap.get(teamsList.get(i - j + k))));
                    newOddBottomHalf += (oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(i - j + k).getIndex()] *
                            (firstPlaceOddsMap.get(teamsList.get(i)) * secondPlaceOddsMap.get(teamsList.get(i - j + k))));
                }
                j++;
            }
            topHalfOddsMap.put(teamsList.get(i), newOddTopHalf);
            bottomHalfOddsMap.put(teamsList.get(i), newOddBottomHalf);
            if (j == 0 || j == endIndex * 2) {
                if (goForward) goForward = false; else goForward = true;
                j = endIndex;
            }
        }
    }

    /**
     * This helper method computes the odds of all the teams to go through the final of the knockout stage
     * by computing the odds of all the teams to go through to the final in the top half or the bottom half of the draw,
     * according to their odds from the previous method
     * @param oddsMatrix - the matrix of the odds of each team to beat all other teams
     * @param goForward - a boolean value which determines whether to compute the odds by going forwards (if the team was in the
     *        top half of the particular half) or backwards (if the team was in the bottom half of the particular half)
     * @param endIndex - the last index to which the computation is to continue (in this method it starts at 8 end finishes with 32)
     */
    public void runKnockoutStagePart2(double[][] oddsMatrix, boolean goForward, int endIndex) {
        while (endIndex < teamsList.size()) {
            int j = endIndex;
            List<Double> tempTopHalfOddsList = new ArrayList<>();
            List<Double> tempBottomHalfOddsList = new ArrayList<>();
            for (int i = 0; i < teamsList.size(); i++) {
                double newTopHalfOdd = 0;
                double newBottomHalfOdd = 0;
                if (goForward) {
                    for (int k = 0; k < endIndex; k++) {
                        newTopHalfOdd += (oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(i + j + k).getIndex()] *
                                (topHalfOddsMap.get(teamsList.get(i)) * topHalfOddsMap.get(teamsList.get(i + j + k))));
                        newBottomHalfOdd += (oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(i + j + k).getIndex()] *
                                (bottomHalfOddsMap.get(teamsList.get(i)) * bottomHalfOddsMap.get(teamsList.get(i + j + k))));
                    }
                    j--;
                } else {
                    for (int k = 0; k < endIndex; k++) {
                        newTopHalfOdd += (oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(i - j + k).getIndex()] *
                                (topHalfOddsMap.get(teamsList.get(i)) * topHalfOddsMap.get(teamsList.get(i - j + k))));
                        newBottomHalfOdd += (oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(i - j + k).getIndex()] *
                                (bottomHalfOddsMap.get(teamsList.get(i)) * bottomHalfOddsMap.get(teamsList.get(i - j + k))));
                    }
                    j++;
                }
                tempTopHalfOddsList.add(newTopHalfOdd);
                tempBottomHalfOddsList.add(newBottomHalfOdd);
                if ((j == 0) || (j == endIndex * 2)) {
                    if (goForward) goForward = false; else goForward = true;
                    j = endIndex;
                }
            }
            topHalfOddsMap = new HashMap<>();
            bottomHalfOddsMap = new HashMap<>();
            for (int i = 0; i < teamsList.size(); i++) {
                topHalfOddsMap.put(teamsList.get(i), tempTopHalfOddsList.get(i));
                bottomHalfOddsMap.put(teamsList.get(i), tempBottomHalfOddsList.get(i));
            }
            endIndex *= 2;
        }
    }

    /**
     * This helper method computes the odds of all the teams win the final of the knockout stage by computing the odds
     * of all the teams to beat each other team in the final according to their odds from the previous method
     * @param oddsMatrix - the matrix of the odds of each team to beat all other teams
     */
    public void runKnockoutStagePart3(double[][] oddsMatrix) {
        for (int i = 0; i < teamsList.size(); i++) {
            double odd = 0;
            for (int k = 0; k < teamsList.size(); k++) {
                if (i != k) {
                    odd += topHalfOddsMap.get(teamsList.get(i)) * bottomHalfOddsMap.get(teamsList.get(k)) *
                            oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(k).getIndex()];
                    odd += bottomHalfOddsMap.get(teamsList.get(i)) * topHalfOddsMap.get(teamsList.get(k)) *
                            oddsMatrix[teamsList.get(i).getIndex()][teamsList.get(k).getIndex()];
                }
            }
            championOddsMap.put(teamsList.get(i), odd);
        }
        if (runType == RunType.STATISTICS) {
            try {
                fileManager.writeOddsToFile(championOddsMap, teamsList, favouriteTeam);
                dbConnection.insertOdd(heuristic, probabilityMatrix.getDistribution(), championOddsMap.get(favouriteTeam));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                dbConnection.updateWinningOdds(championOddsMap, teamsList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
