package model;

import java.util.Random;

/**
 * model.Match class
 */
public class Match {

    private Team team1;
    private Team team2;

    /**
     * model.Match constructor
     * @param team1 - the 1st team in the match
     * @param team2 - the 2nd team in the match
     */
    public Match(Team team1, Team team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    /**
     * Getter for team 1 in a match
     * @return - team 1 in a match
     */
    public Team getTeam1() {
        return team1;
    }

    /**
     * Getter for team 2 in a match
     * @return - team 2 in a match
     */
    public Team getTeam2() {
        return team2;
    }

    /**
     * This method checks if two matches are equal
     * @param o - The object to be compared to this
     * @return - true or false, according to whether the 2 matches are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match match = (Match) o;
        if ((team1.getIndex() == match.getTeam1().getIndex()) && (team2.getIndex() == match.getTeam2().getIndex()))
            return true;
        if ((team1.getIndex() == match.getTeam2().getIndex()) && (team2.getIndex() == match.getTeam1().getIndex()))
            return true;
        return false;
    }

    /**
     * This method runs the match
     * @param odd - the odd of team 1 beating team 2
     * @return - the winning team in a match
     */
    public Team run(int odd) {
        Random random = new Random();
        int outcome = random.nextInt(101);
        if (outcome >= 0 && outcome <= odd)
            return team1;
        else
            return team2;
    }
}
