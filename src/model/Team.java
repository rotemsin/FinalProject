package model;

/**
 * model.Team class
 */
public class Team {

    private String name;
    private int index;

    /**
     * model.Team constructor
     * @param name - the team's name
     * @param index - the team's index
     */
    public Team(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Getter for the team's name
     * @return - the team's name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the team's index
     * @return - the team's index
     */
    public int getIndex() {
        return index;
    }

}
