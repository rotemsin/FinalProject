package model;

/**
 * QueryResult class
 */
public class QueryResult {

    private String heuristic;
    private String distribution;
    private double odd;
    private double averageOdd;
    private String teamName;

    /**
     * Setter of the heurisitc
     * @return - the given heuristic
     */
    public void setHeuristic(String heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Setter of the distribution
     * @return - the given distribution
     */
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    /**
     * Setter of the odd
     * @return - the given odd
     */
    public void setOdd(double odd) {
        this.odd = odd;
    }

    /**
     * Setter of the average odd
     * @return - the given average odd
     */
    public void setAverageOdd(double averageOdd) {
        this.averageOdd = averageOdd;
    }

    /**
     * Setter of the team name
     * @return - the given team name
     */
    public void setTeamName(String teamName) { this.teamName = teamName; }

    /**
     * Getter of the heurisitc
     * @return - the heuristic as String
     */
    public String getHeuristic() {
        return heuristic;
    }

    /**
     * Getter of the distribution
     * @return - the distribution as String
     */
    public String getDistribution() {
        return distribution;
    }

    /**
     * Getter of the odd
     * @return - the odd as double
     */
    public double getOdd() {
        return odd;
    }

    /**
     * Getter of the average odd
     * @return - the average odd as double
     */
    public double getAverageOdd() {
        return averageOdd;
    }

    /**
     * Getter of the team name
     * @return - the team name as String
     */
    public String getTeamName() { return teamName; }

}
