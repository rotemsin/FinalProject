package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DatabaseConnection class
 */
public class DatabaseConnection {

    private Connection connection;
    private Statement statement;

    public static final String DB_NAME = "database.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;
    public static final int SIZE = 32;

    public static final String TABLE_TEAMS = "teams";
    public static final String COLUMN_TEAMS_ID = "id";
    public static final String COLUMN_TEAMS_NAME = "name";
    public static final String COLUMN_TEAMS_GROUP_STAGE_ = "group_stage";
    public static final String COLUMN_TEAMS_FIRST_PLACE_IN_GROUP_STAGE_ODD = "first_place_in_group_stage_odd";
    public static final String COLUMN_TEAMS_SECOND_PLACE_IN_GROUP_STAGE_ODD = "second_place_in_group_stage_odd";
    public static final String COLUMN_TEAMS_WINNING_ODD = "winning_odd";

    public static final String TABLE_ODDS = "odds";
    public static final String COLUMN_ODDS_HEURISTIC = "heuristic";
    public static final String COLUMN_ODDS_DISTRIBUTION = "distribution";
    public static final String COLUMN_ODDS_ODD = "odd";

    public static final String QUERY_TABLE_ODDS =
            "SELECT * FROM " + TABLE_ODDS + " ORDER BY " + COLUMN_ODDS_ODD + " DESC";
    public static final String QUERY_ODDS_BY_HEURISTIC =
            "SELECT " + COLUMN_ODDS_HEURISTIC + ", AVG(" + COLUMN_ODDS_ODD + ") AS average FROM " + TABLE_ODDS +
                    " GROUP BY " + COLUMN_ODDS_HEURISTIC +
                    " ORDER BY average DESC ";
    public static final String  QUERY_ODDS_BY_DISTRIBUTION =
            "SELECT " + COLUMN_ODDS_DISTRIBUTION + ", AVG(" + COLUMN_ODDS_ODD + ") AS average FROM " + TABLE_ODDS +
                    " GROUP BY " + COLUMN_ODDS_DISTRIBUTION +
                    " ORDER BY average DESC ";
    public static final String QUERY_ODDS_BY_TEAM =
            "SELECT " + COLUMN_TEAMS_NAME + ", " + COLUMN_TEAMS_WINNING_ODD + " FROM " + TABLE_TEAMS +
                    " ORDER BY " + COLUMN_TEAMS_WINNING_ODD + " desc ";

    /**
     * model.DatabaseConnection constructor
     */
    public DatabaseConnection() {
        try {
            open();
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + TABLE_TEAMS);
            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS +
                    " (" + COLUMN_TEAMS_ID + " integer, " +
                    COLUMN_TEAMS_NAME + " text, " +
                    COLUMN_TEAMS_GROUP_STAGE_ + " integer, " +
                    COLUMN_TEAMS_FIRST_PLACE_IN_GROUP_STAGE_ODD + " real, " +
                    COLUMN_TEAMS_SECOND_PLACE_IN_GROUP_STAGE_ODD + " real, " +
                    COLUMN_TEAMS_WINNING_ODD + " real" +
                    ")");
            for (int i = 0; i < SIZE; i++) {
                statement.execute("INSERT INTO " + TABLE_TEAMS +
                        " (" + COLUMN_TEAMS_ID +
                        ") " +
                        "VALUES(" + i + ")");
            }
            statement.execute("DROP TABLE IF EXISTS " + TABLE_ODDS);
            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_ODDS +
                    " (" + COLUMN_ODDS_HEURISTIC + " text, " +
                    COLUMN_ODDS_DISTRIBUTION + " text, " +
                    COLUMN_ODDS_ODD + " integer " +
                    ")");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method opens the database connection through the DriverManager class
     * @return
     */
    public boolean open() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        } catch(SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    /**
     * This method closes the database connection
     */
    public void close() {
        try {
            if (connection != null)
                connection.close();
        } catch(SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    //RUN STATISTICS METHODS

    /**
     * This method performs the insertOdd queryinto the odds table
     * @param heuristic - the chosen heuristic
     * @param distribution - the chosen distribution
     * @param odd - the odd of the winning team
     * @throws SQLException
     */
    public void insertOdd(Heuristic heuristic, Distribution distribution, double odd) throws SQLException {
        statement.execute("INSERT INTO " + TABLE_ODDS +
                " (" + COLUMN_ODDS_HEURISTIC + ", " +
                COLUMN_ODDS_DISTRIBUTION + ", " +
                COLUMN_ODDS_ODD +
                ") " +
                "VALUES( '" + heuristic + "' , '" + distribution + "' , " + odd + ")");
    }

    /**
     * This method generates a QueryResult list with the results of the odds table query
     * @return - a list of QueryResults which represent a run and it's details
     */
    public List<QueryResult> queryOddsTable() {
        List<QueryResult> queryResultsList = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(QUERY_TABLE_ODDS)) {
            while (results.next()) {
                QueryResult queryResult = new QueryResult();
                queryResult.setHeuristic(results.getString(1));
                queryResult.setDistribution(results.getString(2));
                queryResult.setOdd(results.getDouble(3));
                queryResultsList.add(queryResult);
            }
        }
        catch(SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
        return queryResultsList;
    }

    /**
     * This method generates a QueryResult list with the results of the odds table, based on several runs and grouped by
     * a criterion which is a heuristic or a distribution
     * @param groupByCriterion - either a heuristic or a distribution, which is by what the results are grouped
     * @return - a list of QueryResults composed of a set of runs and the average odd of the winning team
     */
    public List<QueryResult> queryByAverageOdd(String groupByCriterion) {
        List<QueryResult> queryResultsList = new ArrayList<>();
        if (groupByCriterion.equals(COLUMN_ODDS_HEURISTIC)) {
            try (Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery(QUERY_ODDS_BY_HEURISTIC)) {
                while (results.next()) {
                    QueryResult queryResult = new QueryResult();
                    queryResult.setHeuristic(results.getString(1));
                    queryResult.setAverageOdd(results.getDouble(2));
                    queryResultsList.add(queryResult);
                }
            }
            catch(SQLException e) {
                System.out.println("Query failed: " + e.getMessage());
                return null;
            }
        }
        else {
            try (Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery(QUERY_ODDS_BY_DISTRIBUTION)) {
                while (results.next()) {
                    QueryResult queryResult = new QueryResult();
                    queryResult.setDistribution(results.getString(1));
                    queryResult.setAverageOdd(results.getDouble(2));
                    queryResultsList.add(queryResult);
                }
            } catch(SQLException e) {
                System.out.println("Query failed: " + e.getMessage());
                return null;
            }
        }
        return queryResultsList;
    }

    //RUN SINGLE TOURNAMENT METHODS

    /**
     * This method populates the teams table
     * @param groupsList - the list of groups from which the teams are taken
     * @throws SQLException
     */
    public void updateTeamRecords(List<Group> groupsList) throws SQLException {
        for (Group group: groupsList) {
            for (Team team : group.getTeamsList()) {
                statement.execute("UPDATE " + TABLE_TEAMS +
                        " SET " + COLUMN_TEAMS_NAME + " = '" + team.getName() + "' , " +
                        COLUMN_TEAMS_GROUP_STAGE_ + " = " + group.getNumber() +
                        " WHERE " + COLUMN_TEAMS_ID + " = " + team.getIndex());
            }
        }
    }

    /**
     * This method populates the teams table with the odds of each team in the group stage
     * @param firstPlaceOddsMap - a map of teams and their odds from which the data is stored in the table under first
     * place
     * @param secondPlaceOddsMap - a map of teams and their odds from which the data is stored in the table under second
     *      * place
     * @param teamsList - the list of teams whose names are stored in the table
     * @throws SQLException
     */
    public void updateGroupOdds(Map<Team, Double> firstPlaceOddsMap, Map<Team, Double> secondPlaceOddsMap, List<Team> teamsList)
            throws SQLException {
        for (int i = 0; i < firstPlaceOddsMap.size(); i++) {
            statement.execute("UPDATE " + TABLE_TEAMS +
                    " SET " + COLUMN_TEAMS_FIRST_PLACE_IN_GROUP_STAGE_ODD + " = " + firstPlaceOddsMap.get(teamsList.get(i)) + ", " +
                    COLUMN_TEAMS_SECOND_PLACE_IN_GROUP_STAGE_ODD + " = " + secondPlaceOddsMap.get(teamsList.get(i)) +
                    " WHERE " + COLUMN_TEAMS_ID + " = " + teamsList.get(i).getIndex());
        }
    }

    /**
     * This method updates the teams table with the odd of that team winning
     * @param championOddsMap - a map of teams and their odds of winning to be stored in the teams table
     * @param teamsList - the list of teams whose names are stored in the table
     * @throws SQLException
     */
    public void updateWinningOdds(Map<Team, Double> championOddsMap, List<Team> teamsList) throws SQLException {
        for (int i = 0; i < championOddsMap.size(); i++) {
            statement.execute("UPDATE " + TABLE_TEAMS +
                    " SET " + COLUMN_TEAMS_WINNING_ODD + " = " + championOddsMap.get(teamsList.get(i)) +
                    " WHERE " + COLUMN_TEAMS_ID + " = " + teamsList.get(i).getIndex());
        }
    }

    /**
     * This method performs the query of the odds from the teams table
     * @return - a list of QueryResults which is composed of each teams and its' winning odd
     */
    public List<QueryResult> queryByTeamsOdds() {
        List<QueryResult> queryResultsList = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(QUERY_ODDS_BY_TEAM)) {
            while (results.next()) {
                QueryResult queryResult = new QueryResult();
                queryResult.setTeamName(results.getString(1));
                queryResult.setOdd(results.getDouble(2));
                queryResultsList.add(queryResult);
            }
        }
        catch(SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
        return queryResultsList;
    }

}
