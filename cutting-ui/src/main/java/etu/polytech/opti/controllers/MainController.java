package etu.polytech.opti.controllers;

import etu.polytech.opti.components.CuttingElementView;
import etu.polytech.opti.components.CuttingSolutionDisplayer;
import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.api.observers.CuttingEngineObserver;
import etu.polytech.optim.api.observers.ProgressObservable;
import etu.polytech.optim.api.readers.ConfigurationReader;
import etu.polytech.optim.api.readers.FileConfigurationReader;
import etu.polytech.optim.cutting.GeneticCuttingRunner;
import etu.polytech.optim.cutting.lang.stop.DurationStrategyObservable;
import etu.polytech.optim.cutting.lang.stop.IterationStrategyObservable;
import etu.polytech.optim.genetic.strategies.crossover.SinglePointCrossover;
import etu.polytech.optim.genetic.strategies.mutation.RandomMutation;
import etu.polytech.optim.genetic.utils.ChromosomePair;
import etu.polytech.optim.layout.maxrect.MaxRectPackager;
import etu.polytech.optim.layout.maxrect.choice.BestShortSideFit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Morgan on 11/03/2015.
 */
public class MainController implements CuttingEngineObserver, Initializable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PseudoClass TEXTFIELD_ERROR_PSEUDOCLASS = PseudoClass.getPseudoClass("error");

    /** Stage **/
    private Stage stage;

    /** Configuration Inputs **/
    @FXML
    public TextField sheetWidthInput;
    @FXML
    public TextField sheetHeightInput;
    @FXML
    public TextField sheetPriceInput;

    /** Stop **/
    private static final String DURATION_STOP = "Duration";
    private static final String ITERATIONS_STOP = "Iterations";

    @FXML
    public ComboBox<String> stoppingChooser;
    @FXML
    public TextField stoppingValueInput;

    /** Buttons **/
    @FXML
    public Button runBtn;

    /** Containers **/
    @FXML
    public ListView<CuttingElement> piecesDisplayer;

    /** Progress **/
    @FXML
    public StackPane generationProgressContainer;

    @FXML
    public ProgressBar generationProgressIndicator;

    @FXML
    public Text generationProgressLabel;

    @FXML
    private VBox solutionRoot;

    @FXML
    public GridPane solutionDisplayer;

    /** Charts **/
    @FXML
    public LineChart<Long, Double> generationStats;
    private ObservableList<XYChart.Data<Long, Double>> series;
    
    private CuttingConfiguration configuration;
    private AtomicInteger runCounter = new AtomicInteger(0);

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

        if(validateInputs()){
            long stopValue = Long.parseLong(stoppingValueInput.getText());

            GeneticCuttingRunner runner = new GeneticCuttingRunner.Builder()
                    .setConfiguration(configuration)
                    .setPackager(new MaxRectPackager(configuration, new BestShortSideFit()))
                    .setCrossoverPolicy(new SinglePointCrossover())
                    .setMutationPolicy(new RandomMutation(4))
                    .setSelectionPolicy(population -> new ChromosomePair(population.fittestChromosome(), population.getRandom()))
                    .setStoppingCondition(
                            stoppingChooser.selectionModelProperty().get().getSelectedItem().equals(ITERATIONS_STOP) ?
                                    new IterationStrategyObservable(stopValue) : new DurationStrategyObservable(stopValue, TimeUnit.SECONDS))
                    .build();

            series = FXCollections.observableArrayList();
            generationStats.getData().add(new XYChart.Series<>("Run #" + runCounter.incrementAndGet(), series));

            runner.addObserver(this);

            changeProgressVisibility(true);
            new Thread(runner::start, "Genetic Algorithm Thread").start();
        }
    }

    private boolean validateInputs() {
        if(sheetPriceInput.getText().trim().length() == 0){
            sheetPriceInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, true);
            LOGGER.warn("Sheet Price not defined");

            return false;
        }else{
            sheetPriceInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, false);
        }

        if(sheetWidthInput.getText().trim().length() == 0){
            sheetWidthInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, true);
            LOGGER.warn("Sheet Width not defined");

            return false;
        }else{
            sheetWidthInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, false);
        }

        if(sheetHeightInput.getText().trim().length() == 0){
            sheetHeightInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, true);
            LOGGER.warn("Sheet Height not defined");

            return false;
        }else{
            sheetHeightInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, false);
        }

        if(piecesDisplayer.getItems().size() == 0){
            piecesDisplayer.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, true);
            LOGGER.warn("No element are defined");

            return false;
        }else{
            piecesDisplayer.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, false);
        }

        if(stoppingValueInput.getText().isEmpty()){
            stoppingValueInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, true);
            LOGGER.warn("Stopping condition not defined");

            return false;
        }else{
            try{
                Long.parseLong(stoppingValueInput.getText());
            }catch (NumberFormatException e){
                stoppingValueInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, true);
                LOGGER.warn("Stopping condition not a valid long {}", stoppingValueInput.getText());
            }

            stoppingValueInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, false);
        }

        return true;
    }

    /**
     * Handle the click on the add piece button
     * @param actionEvent
     */
    public void handleAddPiece(ActionEvent actionEvent) {
        showError("Not implemented yet.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generationProgressLabel.textProperty().bind(generationProgressIndicator.progressProperty().multiply(100).asString("%.2f").concat("%"));

        stoppingChooser.setItems(FXCollections.observableArrayList(ITERATIONS_STOP, DURATION_STOP));
        stoppingChooser.setValue(stoppingChooser.itemsProperty().getValue().get(0));

        piecesDisplayer.setCellFactory(param -> new CuttingElementView());

        solutionDisplayer.prefWidthProperty().bind(solutionRoot.widthProperty());
        solutionDisplayer.prefHeightProperty().bind(solutionRoot.heightProperty().subtract(generationStats.heightProperty()));
    }

    @Override
    public void onNewSolution(long iteration, @NotNull double fitness) {
        Platform.runLater(() -> series.add(new XYChart.Data<>(iteration, fitness)));
    }

    @Override
    public void onGenerationStarted() {
        Platform.runLater(() -> {
            changeProgressVisibility(true);
            generationStats.setVisible(true);
        });
    }

    @Override
    public void onGenerationProgress(final ProgressObservable observable) {
        Platform.runLater(() -> generationProgressIndicator.setProgress(observable.progression()));
    }

    @Override
    public void onGenerationFinished(@NotNull CuttingSolution bestSolution) {
        Platform.runLater(() -> {
            generationProgressIndicator.setProgress(1d);
            changeProgressVisibility(false);
            displaySolution(bestSolution);

            //TODO : Supprimer l'observateur
        });
    }

    @FXML
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
                showError(e.getLocalizedMessage());
            }
        });
    }

    private void displaySolution(CuttingSolution bestSolution) {
        solutionDisplayer.getChildren().clear();
        new CuttingSolutionDisplayer(solutionDisplayer).render(configuration, bestSolution);
    }

    /**
     * Change the visibility of the progress indicator
     */
    private void changeProgressVisibility(final boolean visible){
        if(visible){
            generationProgressIndicator.setProgress(0.0);
            generationProgressContainer.setVisible(true);
        }else{
            generationProgressContainer.setVisible(false);
        }
    }

    /**
     * Update inputs with the current configuration
     * @param config
     */
    private void updateConfig(@NotNull final CuttingConfiguration config) {
        LOGGER.info("Cutting Configuration updated ! (Sheet {}*{}, {})",
                config.sheet().width(), config.sheet().height(), config.elements());

        sheetWidthInput.setText(String.valueOf(config.sheet().width()));
        sheetHeightInput.setText(String.valueOf(config.sheet().height()));

        piecesDisplayer.getItems().clear();
        config.elements().forEach(piecesDisplayer.getItems()::add);

        configuration = config;
    }

    /**
     * Show an alert with the message associated with this exception
     * @param error
     */
    private void showError(final String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR, error, ButtonType.OK);
        alert.setTitle("Woops ");
        alert.setHeaderText("Something went wrong ...");
        alert.showAndWait();
    }
}
