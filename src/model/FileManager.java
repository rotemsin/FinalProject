package model;

import java.io.*;
import java.util.*;

public class FileManager {

    /**
     * This method writes the details of all groups to a file
     * @throws IOException
     */
    public void writeGroupDetailsToFile(List<Group>groupsList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("text_files/groups_details.txt"))) {
            for (Group group: groupsList) {
                writer.write("model.Group " + group.getNumber() + ":\n");
                for (Team team: group.getTeamsList()) {
                    writer.write(team.getName() + "\n");
                }
            }
        }
    }

    /**
     * This method writes the odds of all teams to go through to the knockout stage, in 1st place and in 2nd place
     * to a file
     * @throws IOException
     */
    public void writeGroupOddsToFile(Map<Team, Double> firstPlaceOddsMap, Map<Team, Double> secondPlaceOddsMap, List<Team> teamsList)
            throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("text_files/group_odds.txt"))) {
            for (int i = 0; i < firstPlaceOddsMap.size(); i++) {
                writer.write("The odds for " + teamsList.get(i).getName() + " to finish in 1st place are " +
                        firstPlaceOddsMap.get(teamsList.get(i)) + "\n");
                writer.write("The odds for " + teamsList.get(i).getName() + " to finish in 2nd place are " +
                        secondPlaceOddsMap.get(teamsList.get(i)) + "\n");
            }
        }
    }

    /**
     * This method writes the odds of all teams to win the tournament to a file
     * @throws IOException
     */
    public void writeOddsToFile(Map<Team, Double> championOddsMap, List<Team> teamsList, Team favouriteTeam) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("text_files/odds.txt", true))) {
            writer.write("matrix no. ");
            for (int i = 0; i < teamsList.size(); i++) {
                if (teamsList.get(i).getIndex() == favouriteTeam.getIndex())
                    writer.write("The odds for your favourite team " + teamsList.get(i).getName() + " are " + championOddsMap.get(teamsList.get(i)) + "\n");
                else
                    writer.write("The odds for " + teamsList.get(i).getName() + " are " + championOddsMap.get(teamsList.get(i)) + "\n");
            }
        }
    }

}
