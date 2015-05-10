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
import etu.polytech.optim.genetic.strategies.mutation.MultipointMutation;
import etu.polytech.optim.genetic.utils.ChromosomePair;
import etu.polytech.optim.layout.AbstractCuttingPackager;
import etu.polytech.optim.layout.CuttingPackager;
import etu.polytech.optim.layout.guillotine.Guillotine;
import etu.polytech.optim.layout.guillotine.GuillotinePackager;
import etu.polytech.optim.layout.guillotine.split.MinimizeArea;
import etu.polytech.optim.layout.maxrect.MaxRectPackager;
import etu.polytech.optim.layout.maxrect.choice.BestShortSideFit;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Created by Morgan on 11/03/2015.
 */
public class MainController implements CuttingEngineObserver, Initializable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PseudoClass TEXTFIELD_ERROR_PSEUDOCLASS = PseudoClass.getPseudoClass("error");
    private final PseudoClass COMBOBOX_ERROR_PSEUDOCLASS = PseudoClass.getPseudoClass("error");

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

    /** Layout config **/

    @FXML
    public ComboBox<String> layoutChooser;

    @FXML
    public HBox layoutParameters;

    @FXML
    public ComboBox<String> selectorChooser;

    @FXML
    public ComboBox<String> splitterChooser;

    /** Solution **/

    @FXML
    private VBox solutionRoot;

    public TabPane tabPane;

    @FXML
    public GridPane solutionDisplayer;

    /** Charts **/
    @FXML
    public LineChart<Long, Double> generationStats;
    private ObservableList<XYChart.Data<Long, Double>> series;

    @FXML
    public PieChart hitsChart;

    private CuttingConfiguration configuration;
    private AtomicInteger runCounter = new AtomicInteger(0);

    /**
     * Handle the click on the run button
     * @param actionEvent
     */
    public void handleRun(ActionEvent actionEvent) {
        LOGGER.debug("Clicked Run");

        try {
            if (validateInputs()) {
                long stopValue = Long.parseLong(stoppingValueInput.getText());

                GeneticCuttingRunner runner = new GeneticCuttingRunner.Builder()
                        .setConfiguration(configuration)
                        .setPackager(parsePackager())
                        .setCrossoverPolicy(new SinglePointCrossover())
                        .setMutationPolicy(new MultipointMutation(4, 0.25d))
                        .setSelectionPolicy(population -> new ChromosomePair(population.fittestChromosome(), population.getRandom()))
                        .setStoppingCondition(
                                stoppingChooser.selectionModelProperty().get().getSelectedItem().equals(ITERATIONS_STOP) ?
                                        new IterationStrategyObservable(stopValue) : new DurationStrategyObservable(stopValue, TimeUnit.SECONDS))
                        .build();

                series = FXCollections.observableArrayList();
                generationStats.getData().add(new XYChart.Series<>("Run #" + runCounter.incrementAndGet(), series));

                runner.addObserver(this);

                changeProgressVisibility(true);
                new Thread(() -> {
                    try {
                        runner.start();
                    } catch (Exception e) {
                        Platform.runLater(() -> showError(e.getMessage()));
                    }
                }, "Genetic Algorithm Thread").start();
            }
        }catch (IllegalArgumentException e){
            showError(e.getMessage());
        }
    }

    private CuttingPackager parsePackager() {

        AbstractCuttingPackager.Factory factory;

        if (layoutChooser.getSelectionModel().getSelectedItem().toLowerCase().equals("guillotine")) {
            factory = new GuillotinePackager.GuillotineFactory();
        } else {
            factory = new MaxRectPackager.MaxRectFactory();
        }

        return factory.createPackager(
                configuration,
                selectorChooser.getSelectionModel().getSelectedItem(),
                splitterChooser.getSelectionModel().getSelectedItem()
        );
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
                return false;
            }

            stoppingValueInput.pseudoClassStateChanged(TEXTFIELD_ERROR_PSEUDOCLASS, false);
        }

        if(layoutChooser.getSelectionModel().isEmpty()) {
            layoutChooser.pseudoClassStateChanged(COMBOBOX_ERROR_PSEUDOCLASS, true);
            LOGGER.warn("Layout algorithm not defined");

            return false;
        }else {
            layoutChooser.pseudoClassStateChanged(COMBOBOX_ERROR_PSEUDOCLASS, false);
        }


        for (Node node : layoutParameters.getChildren()) {
            if(node instanceof ComboBox){
                if(((ComboBox) node).getSelectionModel().isEmpty()){
                    node.pseudoClassStateChanged(COMBOBOX_ERROR_PSEUDOCLASS, true);
                    LOGGER.warn("Layout parameters not defined");

                    return false;
                }else{
                    node.pseudoClassStateChanged(COMBOBOX_ERROR_PSEUDOCLASS, false);
                }
            }
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

        layoutChooser.setItems(FXCollections.observableArrayList("Guillotine", "MaxRect"));
        layoutChooser.setOnAction((event) -> {
            String selectedItem = layoutChooser.getSelectionModel().getSelectedItem();
            System.out.println("Changed " + selectedItem);

            if (selectedItem.equals("Guillotine")) {
                selectorChooser.setItems(FXCollections.observableArrayList(GuillotinePackager.GuillotineFactory.AVAILABLE_SELECTOR));
                splitterChooser.setItems(FXCollections.observableArrayList(GuillotinePackager.GuillotineFactory.AVAILABLE_SPLITTER));
            } else {
                selectorChooser.setItems(FXCollections.observableArrayList(MaxRectPackager.MaxRectFactory.AVAILABLE_SELECTOR));
                splitterChooser.setItems(FXCollections.observableArrayList(MaxRectPackager.MaxRectFactory.AVAILABLE_SPLITTER));

                Tooltip t = new Tooltip("Be Careful, this algorithm doesn't follow the guillotine constraint !");
                t.setAutoHide(true);
                Bounds screen = layoutChooser.localToScreen(layoutChooser.getLayoutBounds());
                t.show(layoutChooser, screen.getMinX(), screen.getMinY() - t.getHeight());
            }
        });
    }

    @Override
    public void onNewSolution(long iteration, @NotNull double fitness) {
        Platform.runLater(() -> {
            XYChart.Data<Long, Double> point = new XYChart.Data<>(iteration, fitness);
            series.add(point);

            Node node = point.getNode();
            final Text dataText = new Text(String.valueOf(point.getYValue()));
            node.parentProperty().addListener((ov, oldParent, parent) -> {
                Group parentGroup = (Group) parent;
                parentGroup.getChildren().add(dataText);
            });

            node.boundsInParentProperty().addListener((ov, oldBounds, bounds) -> {
                dataText.setLayoutX(
                        Math.round(
                                bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2
                        )
                );
                dataText.setLayoutY(
                        Math.round(
                                bounds.getMaxY() - dataText.prefHeight(-1) * 0.5
                        )
                );
            });
        });
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
        tabPane.setVisible(true);
        solutionDisplayer.getChildren().clear();
        new CuttingSolutionDisplayer(solutionDisplayer).render(configuration, bestSolution);


        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        bestSolution.getHitsMap().entrySet().stream()
                .sorted(Map.Entry.comparingByValue((l1, l2) -> -Long.compare(l1, l2)))
                .limit(Math.min(bestSolution.getHitsMap().size(), 10))
                .forEach(e -> pieChartData.add(new PieChart.Data(e.getKey(), e.getValue())));

        Platform.runLater(() -> {
            hitsChart.setData(pieChartData);

            hitsChart.setTitle("Number of chromosome hits");

            for (Node node : hitsChart.lookupAll(".text.chart-pie-label")) {
                if (node instanceof Text) {
                    for (PieChart.Data data : hitsChart.getData()) {
                        Text text = (Text) node;

                        if (data.getNode() == node) {
                            text.setText(text.getText() + " " + data.getPieValue() + "%");
                        }
                    }
                }
            }
        });
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
