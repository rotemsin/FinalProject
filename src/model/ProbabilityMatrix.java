package model;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import java.util.ArrayList;
import java.util.List;

/**
 * Probability Matrix class
 */
public class ProbabilityMatrix {

    private double[][] matrix;
    private Distribution distribution;

    /**
     * model.ProbabilityMatrix constructor
     * @param numberOfTeams - the number of teams in the tournament
     * @param distribution - the selected distribution
     */
    public ProbabilityMatrix(int numberOfTeams, Distribution distribution) {
        matrix = new double[numberOfTeams][numberOfTeams];
        this.distribution = distribution;
        switch (distribution) {
            case REAL_DATA: realDataProbabilityMatrix(); break;
            case REAL_DATA_2: realData2ProbabilityMatrix(); break;
            case UNIFORM: uniformOrNormalProbabilityMatrix(); break;
            case NORMAL: uniformOrNormalProbabilityMatrix(); break;
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = matrix[i][j] * 0.01;
            }
        }
    }

    /**
     * This method initializes the probability matrix according the real data distribution
     */
    private void realDataProbabilityMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                int sum = i + j + 1;
                matrix[i][j] = (j * 100) / sum;
                matrix[j][i] = 100 - matrix[i][j];
            }
        }
    }

    /**
     * This method initializes the probability matrix according the real data 2 distribution
     */
    private void realData2ProbabilityMatrix() {
        int[] winnerOddsArray = {5, 5, 6, 8, 9, 12, 18, 28, 33, 40, 40, 40, 66, 100, 100, 125, 125, 125, 150, 250,
                                    250, 250, 300, 300, 500, 500, 500, 750, 750, 750, 1500, 1500};
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                int sum = winnerOddsArray[i] + winnerOddsArray[j];
                matrix[i][j] = (winnerOddsArray[j] * 100) / sum;
                matrix[j][i] = 100 - matrix[i][j];
            }
        }
    }

    /**
     * This method initializes the probability matrix according uniform or normal distribution
     */
    private void uniformOrNormalProbabilityMatrix() {
        AbstractRealDistribution distributionGenerator;
        if (distribution == Distribution.UNIFORM)
            distributionGenerator = new UniformRealDistribution(0, 101);
        else
            distributionGenerator = new NormalDistribution(50, 10);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                matrix[i][j] = (int) distributionGenerator.sample();
                matrix[j][i] = 100 - matrix[i][j];
            }
        }
    }

    /**
     * This method finds the opponents of the favourite team in its' group by far adversary heuristic
     * @param otherTeamsList - the list of the teams that haven't been assigned to a group yet
     * @param favouriteTeam - the favourite team
     * @param numberOfOpponents - the number of teams that the favourite team will be playing against
     * @return - the list of teams that will be in the same group with the favourite team
     */
    public List<Team> findOpponentsByFarAdversary(Heuristic heuristic, List<Team> otherTeamsList, Team favouriteTeam,
                                                  int numberOfOpponents) {
        List<Team> opponentsList = new ArrayList<>();
        for (int i = 0; i < numberOfOpponents; i++) {
            int opponentIndex = -1;
            switch (heuristic) {
                case NO_HEURISTIC:
                    opponentIndex = findNextOpponentByRandom(otherTeamsList, favouriteTeam.getIndex());
                    break;
                case FAR_ADVERSARY:
                    opponentIndex = findNextOpponentByBestOdd(otherTeamsList, favouriteTeam.getIndex());
                    break;
                case FAR_ADVERSARY_2:
                    opponentIndex = findNextOpponentByAverage(otherTeamsList, favouriteTeam.getIndex());
                    break;
                case CLOSE_ADVERSARY:
                    opponentIndex = findNextOpponentByWorstOdd(otherTeamsList, favouriteTeam.getIndex());
                    break;
            }
            Team teamToRemove = null;
            for (Team team: otherTeamsList) {
                if (team.getIndex() == opponentIndex) {
                    teamToRemove = team;
                    opponentsList.add(team);
                }
            }
            otherTeamsList.remove(teamToRemove);
        }
        return opponentsList;
    }

    /**
     * This method finds the opponents of the favourite team in its' group by best win heuristic
     * @param otherTeamsList - the list of the teams that haven't been assigned to a group yet
     * @param favouriteTeam - the favourite team
     * @param numberOfOpponents - the number of teams that the favourite team will be playing against
     * @return - the list of teams that will be in the same group with the favourite team
     */
    public List<Team> findOpponentsByBestWin(List<Team> otherTeamsList, Team favouriteTeam, int numberOfOpponents) {
        List<Team> opponentsList = new ArrayList<>();
        Team nextTeam = favouriteTeam;
        for (int i = 0; i < numberOfOpponents; i++) {
            int opponentIndex = findNextOpponentByBestOdd(otherTeamsList, nextTeam.getIndex());
            Team teamToRemove = null;
            for (Team team: otherTeamsList) {
                if (team.getIndex() == opponentIndex) {
                    teamToRemove = team;
                    nextTeam = team;
                    opponentsList.add(team);
                }
            }
            otherTeamsList.remove(teamToRemove);
        }
        return opponentsList;
    }

    /**
     * This method finds the next opponent of the favourite team in its' group by random selection
     * @param otherTeamsList - the list of the teams that haven't been assigned to a group yet
     * @param favouriteTeamIndex - the index of the favourite team
     * @return - the index of the selected opponent
     */
    private int findNextOpponentByRandom(List<Team> otherTeamsList, int favouriteTeamIndex) {
        UniformRealDistribution distributionGenerator = new UniformRealDistribution(0, matrix.length);
        int selectedTeamIndex = (int) distributionGenerator.sample();
        if (selectedTeamIndex != favouriteTeamIndex) {
            for (Team team: otherTeamsList) {
                if (team.getIndex() == selectedTeamIndex)
                    return team.getIndex();
            }
            return findNextOpponentByRandom(otherTeamsList, favouriteTeamIndex);
        }
        return findNextOpponentByRandom(otherTeamsList, favouriteTeamIndex);

    }

    /**
     * This method finds the next opponent of the favourite team in its' group by the best odd selection
     * @param otherTeamsList - the list of the teams that haven't been assigned to a group yet
     * @param favouriteTeamIndex - the index of the favourite team
     * @return - the index of the selected opponent
     */
    private int findNextOpponentByBestOdd(List<Team> otherTeamsList, int favouriteTeamIndex) {
        double maxValue = -0.01;
        int selectedTeamIndex = -1;
        for (int j = 0; j < matrix[favouriteTeamIndex].length; j++) {
            if (j != favouriteTeamIndex && matrix[favouriteTeamIndex][j] > maxValue) {
                for (Team team: otherTeamsList) {
                    if (team.getIndex() == j) {
                        maxValue = matrix[favouriteTeamIndex][j];
                        selectedTeamIndex = j;
                    }
                }
            }
        }
        return selectedTeamIndex;
    }

    /**
     * This method finds the next opponent of the favourite team in its' group by the worst odd selection
     * @param otherTeamsList - the list of the teams that haven't been assigned to a group yet
     * @param favouriteTeamIndex - the index of the favourite team
     * @return - the index of the selected opponent
     */
    private int findNextOpponentByWorstOdd(List<Team> otherTeamsList, int favouriteTeamIndex) {
        double minValue = 1.01;
        int selectedTeamIndex = -1;
        for (int j = 0; j < matrix[favouriteTeamIndex].length; j++) {
            if (j != favouriteTeamIndex && matrix[favouriteTeamIndex][j] < minValue) {
                for (Team team: otherTeamsList) {
                    if (team.getIndex() == j) {
                        minValue = matrix[favouriteTeamIndex][j];
                        selectedTeamIndex = j;
                    }
                }
            }
        }
        return selectedTeamIndex;
    }

    /**
     * This method finds the next opponent of the favourite team in its' group by average odd selection
     * @param otherTeamsList - the list of the teams that haven't been assigned to a group yet
     * @param favouriteTeamIndex - the index of the favourite team
     * @return - the index of the selected opponent
     */
    private int findNextOpponentByAverage(List<Team> otherTeamsList, int favouriteTeamIndex) {
        double minAverageOdd = 1.01;
        int selectedTeamIndex = -1;
        for (int i = 0; i < matrix.length; i++) {
            double sumOfOdds = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (i != j)
                    sumOfOdds += matrix[i][j];
            }
            if ((i != favouriteTeamIndex) && (sumOfOdds / matrix.length < minAverageOdd)) {
                for (Team team: otherTeamsList) {
                    if (team.getIndex() == i) {
                        minAverageOdd = sumOfOdds;
                        selectedTeamIndex = i;
                    }
                }
            }
        }
        return selectedTeamIndex;
    }

    /**
     * Getter of the probability matrix
     * @return - the probability matrix
     */
    public double[][] getMatrix() {
        return matrix;
    }

    public Distribution getDistribution() {
        return distribution;
    }

}

