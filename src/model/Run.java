package model;

import java.util.List;

/**
 * Run class
 */
public class Run {

    private int numberOfRuns;
    private String favouriteTeam;
    private String heuristic;
    private String distribution;

    public static final int SIZE = 32;
    public static final String HEURISTIC = "heuristic";
    public static final String DISTRIBUTION = "distribution";

    /**
     * Run class constructor for statistics run
     * @param numberOfRuns - the number of runs in the run
     * @param favouriteTeam - the favourite tea  in the run
     */
    public Run(int numberOfRuns, String favouriteTeam) {
        this.favouriteTeam = favouriteTeam;
        this.numberOfRuns = numberOfRuns;
    }

    /**
     * Run class constructor for single run
     * @param heuristic - the heuristic of the run
     * @param distribution - the distribution of the run
     * @param favouriteTeam - the favourite team of the run
     */
    public Run(String heuristic, String distribution, String favouriteTeam) {
        this.favouriteTeam = favouriteTeam;
        this.heuristic = heuristic;
        this.distribution = distribution;
    }

    /**
     * This method performs the ststistics run
     * @return - a list of query results which is to be shown on the screen
     */
    public List<QueryResult> statisticsRun() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        if (!dbConnection.open()) {
            System.out.println("Can't open database connection");
            return null;
        }
        FileManager fileManager = new FileManager();
        ProbabilityMatrix matrix;
        Tournament tournament;

        //REAL DATA DISTRIBUTION
        for (int i = 0; i < numberOfRuns; i++) {
            matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA);
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY_2, dbConnection, fileManager,RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.BEST_WIN, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.CLOSE_ADVERSARY, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.NO_HEURISTIC, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
        }

        //REAL DATA 2 DISTRIBUTION
        for (int i = 0; i < numberOfRuns; i++) {
            matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA_2);
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY_2, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.BEST_WIN, dbConnection, fileManager,RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.CLOSE_ADVERSARY, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.NO_HEURISTIC, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
        }

        //UNIFORM DISTRIBUTION
        for (int i = 0; i < numberOfRuns; i++) {
            matrix = new ProbabilityMatrix(SIZE, Distribution.UNIFORM);
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY_2, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.BEST_WIN, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.CLOSE_ADVERSARY, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.NO_HEURISTIC, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
        }

        //NORMAL DISTRIBUTION
        for (int i = 0; i < numberOfRuns; i++) {
            matrix = new ProbabilityMatrix(SIZE, Distribution.NORMAL);
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY_2, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.BEST_WIN, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.CLOSE_ADVERSARY,dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
            tournament = new Tournament(favouriteTeam, matrix, Heuristic.NO_HEURISTIC, dbConnection, fileManager, RunType.STATISTICS);
            tournament.runGroupStage();
            tournament.runKnockoutStage();
        }

        List<QueryResult> queryResultsList = dbConnection.queryOddsTable();
        if (queryResultsList == null) {
            System.out.println("No Query Results");
            return null;
        }
        for (QueryResult queryResult : queryResultsList) {
            System.out.println("Heuristic = " + queryResult.getHeuristic() +
                    ", Distribution = " + queryResult.getDistribution() + ", Odd = " + queryResult.getOdd());
        }

        System.out.println();
        System.out.println("Statistics by Distribution:");
        queryResultsList = dbConnection.queryByAverageOdd(DISTRIBUTION);
        if (queryResultsList == null) {
            System.out.println("No Query Results");
            return null;
        }
        for (QueryResult queryResult : queryResultsList) {
            System.out.println("Distribution = " + queryResult.getDistribution() + ", Average Odd = " + queryResult.getAverageOdd());
        }

        System.out.println();
        System.out.println("Statistics by Heuristic:");
        queryResultsList = dbConnection.queryByAverageOdd(HEURISTIC);

        if (queryResultsList == null) {
            System.out.println("No Query Results");
            return null;
        }
        for (QueryResult queryResult : queryResultsList) {
            System.out.println("Heuristic = " + queryResult.getHeuristic() + ", Average Odd = " + queryResult.getAverageOdd());
        }

        dbConnection.close();
        return queryResultsList;
    }

    /**
     * This method performs the single tournament run
     * @return - a list of query results which is to be shown on the screen
     */
    public List<QueryResult> singleTournamentRun() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        if (!dbConnection.open()) {
            System.out.println("Can't open database connection");
            return null;
        }
        FileManager fileManager = new FileManager();
        ProbabilityMatrix matrix;
        Tournament tournament = null;

        if (heuristic.equals("Far Adversary")) {
            if (distribution.equals("Real Data")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Real Data 2")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA_2);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Uniform")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.UNIFORM);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Normal")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.NORMAL);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
        }

        if (heuristic.equals("Far Adversary 2")) {
            if (distribution.equals("Real Data")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY_2, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Real Data 2")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA_2);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY_2, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Uniform")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.UNIFORM);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY_2, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Normal")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.NORMAL);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.FAR_ADVERSARY_2, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
        }

        if (heuristic.equals("Best Win")) {
            if (distribution.equals("Real Data")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.BEST_WIN, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Real Data 2")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA_2);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.BEST_WIN, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Uniform")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.UNIFORM);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.BEST_WIN, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Normal")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.NORMAL);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.BEST_WIN, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
        }

        if (heuristic.equals("Close Adversary")) {
            if (distribution.equals("Real Data")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.CLOSE_ADVERSARY, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Real Data 2")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA_2);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.CLOSE_ADVERSARY, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Uniform")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.UNIFORM);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.CLOSE_ADVERSARY, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Normal")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.NORMAL);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.CLOSE_ADVERSARY, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
        }

        if (heuristic.equals("No Heuristic")) {
            if (distribution.equals("Real Data")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.NO_HEURISTIC, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Real Data 2")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.REAL_DATA_2);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.NO_HEURISTIC, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Uniform")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.UNIFORM);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.NO_HEURISTIC, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
            if (distribution.equals("Normal")) {
                matrix = new ProbabilityMatrix(SIZE, Distribution.NORMAL);
                tournament = new Tournament(favouriteTeam, matrix, Heuristic.NO_HEURISTIC, dbConnection, fileManager, RunType.SINGLE_RUN);
            }
        }

        tournament.runGroupStage();
        tournament.runKnockoutStage();

        List<QueryResult> queryResultsList = dbConnection.queryByTeamsOdds();
        if (queryResultsList == null) {
            System.out.println("No Query Results");
            return null;
        }
        for (QueryResult queryResult : queryResultsList) {
            System.out.println("Team = " + queryResult.getTeamName() + ", Odd = " + queryResult.getOdd());
        }

        dbConnection.close();
        return queryResultsList;
    }

}