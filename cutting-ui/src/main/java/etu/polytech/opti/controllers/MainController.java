package etu.polytech.opti.controllers;

import etu.polytech.opti.components.CuttingElementView;
import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.api.observers.CuttingEngineObserver;
import etu.polytech.optim.api.readers.ConfigurationReader;
import etu.polytech.optim.api.readers.FileConfigurationReader;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Morgan on 11/03/2015.
 */
public class MainController implements CuttingEngineObserver, Initializable {

    private static final Logger LOGGER = LogManager.getLogger();
    /** UI **/
    private Stage stage;

    /** Configuration Inputs **/
    private final PseudoClass TEXTFIELD_ERROR_PSEUDOCLASS = PseudoClass.getPseudoClass("error");
    @FXML
    public TextField sheetWidthInput;

    @FXML
    public TextField sheetHeightInput;

    @FXML
    public TextField sheetPriceInput;

    /** Buttons **/
    @FXML
    public Button runBtn;

    /** Containers **/
    @FXML
    public ListView<Map.Entry<CuttingElement, Integer>> piecesDisplayer;

    @FXML
    public GridPane solutionDisplayer;

    @FXML
    public LineChart generationStats;

    /**
     * Set the stage of this application
     * @param stage
     */
    public void setStage(@NotNull final Stage stage){
        this.stage = stage;
    }

    /**
     * Handle the click on the run button
     * @param actionEvent
     */
    public void handleRun(ActionEvent actionEvent) {
        LOGGER.debug("Clicked Run");
        validateInputs();
    }

    private void validateInputs() {

    }

    /**
     * Handle the click on the add piece button
     * @param actionEvent
     */
    public void handleAddPiece(ActionEvent actionEvent) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        piecesDisplayer.setCellFactory(param -> new CuttingElementView());
        /*ObservableList<XYChart.Data<Integer, Integer>> series = FXCollections.observableArrayList();

        for (int i = 0; i < 100; i++) {
            XYChart.Data<Integer, Integer> data = new XYChart.Data<>();
            data.setXValue(i*10);
            data.setYValue((int) (Math.random() * 5000));
            series.add(data);
        }

        generationStats.getData().add(new XYChart.Series<>("Cutting", series));*/
    }

    @Override
    public void onNewSolution(@NotNull CuttingSolution solution) {

    }

    @Override
    public void onGenerationStarted() {

    }

    @Override
    public void onGenerationProgress(int iteration) {

    }

    @Override
    public void onGenerationFinished(@NotNull CuttingSolution bestSolution) {

    }

    public void handleImport(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Cutting Problem Configuration File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        Optional<File> file = Optional.ofNullable(chooser.showOpenDialog(new Stage()));

        file.ifPresent(f -> {
            LOGGER.info("Importing configuration from {}", f);
            ConfigurationReader reader = new FileConfigurationReader(f);
            try {
                CuttingConfiguration config = reader.readConfiguration();
                updateConfig(config);
            } catch (IOException e) {
                LOGGER.warn("Exception while reading configuration file {} -> {}", file, e.getLocalizedMessage());
                showError(e);
            }
        });
    }

    private void updateConfig(@NotNull final CuttingConfiguration config) {
        LOGGER.info("Cutting Configuration updated ! (Sheet {}*{}, {})",
                config.sheet().width(), config.sheet().height(), config.elements().entrySet());
        sheetWidthInput.setText(String.valueOf(config.sheet().width()));
        sheetHeightInput.setText(String.valueOf(config.sheet().height()));

        piecesDisplayer.getItems().clear();
        config.elements().entrySet().forEach(piecesDisplayer.getItems()::add);
    }

    private void showError(IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getLocalizedMessage(), ButtonType.OK);
        alert.showAndWait();
    }
}
