
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import model.QueryResult;
import model.Run;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Controller {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ComboBox<String> selectActionComboBox;
    @FXML
    private ComboBox<String> favouriteTeamComboBox;
    @FXML
    private ComboBox<String> runsComboBox;
    @FXML
    private ComboBox<String> heuristicsComboBox;
    @FXML
    private ComboBox<String> distributionComboBox;
    @FXML
    private Label statisticsLabel;
    @FXML
    private Label runsLabel;
    @FXML
    private Label singleTournamentLabel;
    @FXML
    private Label heuristicLabel;
    @FXML
    private Label distributionLabel;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private Label farAdversaryLabel;
    @FXML
    private Label farAdversary2Label;
    @FXML
    private Label bestWinLabel;
    @FXML
    private Label closeAdversaryLabel;
    @FXML
    private Label noHeuristicLabel;
    @FXML
    private Label realDataLabel;
    @FXML
    private Label realData2Label;
    @FXML
    private Label uniformLabel;
    @FXML
    private Label normalLabel;

    /**
     * This method initializes the combo boxes and texts in the application screen
     */
    public void initialize() {
        ObservableList<String> teamsList = FXCollections.observableArrayList();
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/teams.txt"))) {
            String input;
            while ((input = reader.readLine()) != null) {
                teamsList.add(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        favouriteTeamComboBox.getItems().addAll(teamsList);
        favouriteTeamComboBox.getSelectionModel().select(0);
        selectActionComboBox.getItems().addAll("Run Statistics", "Run Single Tournament");
        selectActionComboBox.getSelectionModel().select(0);
        runsComboBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "100");
        runsComboBox.getSelectionModel().select(0);
        heuristicsComboBox.getItems().addAll("Far Adversary", "Far Adversary 2", "Best Win", "Close Adversary", "No Heuristic");
        heuristicsComboBox.getSelectionModel().select(0);
        distributionComboBox.getItems().addAll("Real Data", "Real Data 2", "Uniform", "Normal");
        distributionComboBox.getSelectionModel().select(0);
        setHeuristicTexts();
        setDistributionTexts();
    }

    /**
     * This method reads the descriptions of the heuristics from text files and displays it on the right side of the
     * screen
     */
    public void setHeuristicTexts() {
        String farAdversaryText = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/far_adversary.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            farAdversaryText = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        farAdversaryLabel.setText(farAdversaryText);

        String farAdversary2Text = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/far_adversary_2.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            farAdversary2Text = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        farAdversary2Label.setText(farAdversary2Text);

        String bestWinText = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/best_win.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            bestWinText = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bestWinLabel.setText(bestWinText);

        String closeAdversaryText = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/close_adversary.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            closeAdversaryText = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeAdversaryLabel.setText(closeAdversaryText);

        String noHeuristicText = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/no_heuristic.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            noHeuristicText = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        noHeuristicLabel.setText(noHeuristicText);
    }

    /**
     * This method reads the descriptions of the distributions from text files and displays it on the right side of the
     * screen
     */
    public void setDistributionTexts() {
        String realDataText = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/real_data.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            realDataText = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        realDataLabel.setText(realDataText);

        String realData2Text = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/real_data_2.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            realData2Text = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        realData2Label.setText(realData2Text);

        String uniformText = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/uniform.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            uniformText = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uniformLabel.setText(uniformText);

        String normalText = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("text_files/normal.txt"))) {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            normalText = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        normalLabel.setText(normalText);
    }

    /**
     * This method runs the actual application and displays the graphs on the screen
     */
    @FXML
    public void run() {
        if (selectActionComboBox.getSelectionModel().getSelectedItem().equals("Run Statistics")) {
            int numberOfRuns = Integer.parseInt(runsComboBox.getSelectionModel().getSelectedItem());
            String favouriteTeam = favouriteTeamComboBox.getSelectionModel().getSelectedItem();
            Run run = new Run(numberOfRuns, favouriteTeam);
            List<QueryResult> queryResultsList = run.statisticsRun();
            XYChart.Series series = new XYChart.Series();
            for (QueryResult result: queryResultsList) {
                series.getData().add(new XYChart.Data(result.getHeuristic(), result.getAverageOdd()));
            }
            barChart.getData().addAll(series);
            barChart.setAnimated(false);
        }
        else {
            String heuristic = heuristicsComboBox.getSelectionModel().getSelectedItem();
            String distribution = distributionComboBox.getSelectionModel().getSelectedItem();
            String favouriteTeam = favouriteTeamComboBox.getSelectionModel().getSelectedItem();
            Run run = new Run(heuristic, distribution, favouriteTeam);
            List<QueryResult> queryResultsList = run.singleTournamentRun();
            XYChart.Series series = new XYChart.Series();
            for (QueryResult result: queryResultsList) {
                series.getData().add(new XYChart.Data(result.getTeamName(), result.getOdd()));
            }
            barChart.getData().addAll(series);
            barChart.setAnimated(true);
        }
    }

    /**
     * This method enables or disables the buttons on the screen according to the selection of the type of run
     * the user wishes to run
     */
    @FXML
    public void enable() {
        if (selectActionComboBox.getSelectionModel().getSelectedIndex() == 1) {
            singleTournamentLabel.setDisable(false);
            heuristicLabel.setDisable(false);
            heuristicsComboBox.setDisable(false);
            distributionLabel.setDisable(false);
            distributionComboBox.setDisable(false);
            statisticsLabel.setDisable(true);
            runsLabel.setDisable(true);
            runsComboBox.setDisable(true);
        }
        else {
            singleTournamentLabel.setDisable(true);
            heuristicLabel.setDisable(true);
            heuristicsComboBox.setDisable(true);
            distributionLabel.setDisable(true);
            distributionComboBox.setDisable(true);
            statisticsLabel.setDisable(false);
            runsLabel.setDisable(false);
            runsComboBox.setDisable(false);
        }
    }

}
