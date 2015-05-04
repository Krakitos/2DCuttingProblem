package etu.polytech.opti.components;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.api.lang.CuttingSolution;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.Collection;

/**
 * Created by Morgan on 14/03/2015.
 */
public class CuttingSolutionDisplayer {

    private GridPane parent;

    public CuttingSolutionDisplayer(GridPane parent){
        this.parent = parent;
    }

    public void render(CuttingConfiguration configuration, CuttingSolution solution){
        clear();

        final int nbColumns = solution.layout().size() >= 3 ? 3 : solution.layout().size();
        final int nbRows =  solution.layout().size() / nbColumns == 0 ? 1 : solution.layout().size() / nbColumns;

        for (int i = 0; i < nbColumns; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setHgrow(Priority.ALWAYS);
            c.setPercentWidth(100.0f / nbColumns);
            parent.getColumnConstraints().add(c);
        }

        for (int i = 0; i < nbRows; i++) {
            RowConstraints r = new RowConstraints();
            r.setVgrow(Priority.ALWAYS);
            r.setPercentHeight(100.0f / nbRows);
        }


        final DoubleBinding width = parent.widthProperty().divide(nbColumns);
        final DoubleBinding height = parent.heightProperty().divide(nbRows);

        for (int i = 0; i < solution.layout().size(); i++) {
            Collection<CuttingLayoutElement> elements = solution.layout().get(i);

            CuttingPatternView view = new CuttingPatternView();
            view.prefWidthProperty().bind(width);
            view.prefHeightProperty().bind(height);
            parent.add(view, i % nbColumns, i / nbColumns);

            view.render(configuration, elements);
        }
    }

    public void clear() {
        parent.getChildren().clear();
    }
}
