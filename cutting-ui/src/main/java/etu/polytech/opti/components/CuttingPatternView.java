package etu.polytech.opti.components;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.util.Collection;

/**
 * Created by Morgan on 20/04/2015.
 */
public class CuttingPatternView extends Pane {

    public void render(CuttingConfiguration config, Collection<CuttingLayoutElement> elements) {
        getChildren().clear();

        renderBackground();
        renderRectangles(config, elements);
    }

    private void renderRectangles(CuttingConfiguration config, Collection<CuttingLayoutElement> elements) {
        DoubleBinding scaleX, scaleY;

        int grayStep = 256 / config.elements().size();

        //Same orientation
        if(config.sheet().width() > config.sheet().height() && getWidth() > getHeight()){
            scaleX = widthProperty().divide(config.sheet().width());
            scaleY = heightProperty().divide(config.sheet().height());

        }else{ //Rotated
            scaleY = widthProperty().divide(config.sheet().width());
            scaleX = heightProperty().divide(config.sheet().height());
        }

        elements.forEach(e -> {
            Color c = Color.grayRgb(grayStep * e.element().id());
            Label l = new Label(String.valueOf(e.element().id()));
            l.layoutXProperty().bind(scaleX.multiply(e.x()));
            l.layoutYProperty().bind(scaleY.multiply(e.y()));
            l.prefWidthProperty().bind(scaleX.multiply(e.isRotate() ? e.height() : e.width()));
            l.prefHeightProperty().bind(scaleY.multiply(e.isRotate() ? e.width() : e.height()));
            l.setStyle("-fx-background-color: " + c.toString().replace("0x", "#") + "; "
                    + " -fx-border-radius: 2px; -fx-border-color: black");
            l.textAlignmentProperty().set(TextAlignment.CENTER);
            getChildren().add(l);
        });
    }

    private void renderBackground() {
        setStyle("-fx-background-color: lightgray");
    }


    private static class CuttingPiece extends StackPane {
        private Rectangle rectangle;
        private Label label;

        public CuttingPiece(CuttingLayoutElement e, Color color, DoubleBinding scaleX, DoubleBinding scaleY){
            super();

            rectangle = new Rectangle();
            rectangle.setStroke(Color.BLACK);
            rectangle.setFill(color);


            scaleX.addListener((o, oldValue, newValue) -> {
                rectangle.relocate(newValue.doubleValue() * e.x(), rectangle.getLayoutY());
            });

            scaleY.addListener((o, oldValue, newValue) -> {
                rectangle.relocate(rectangle.getLayoutX(), newValue.doubleValue() * e.y());
            });

            rectangle.widthProperty().bind(scaleX.multiply(e.width()));
            rectangle.heightProperty().bind(scaleY.multiply(e.height()));

            rectangle.relocate(scaleX.doubleValue() * e.x(), scaleY.doubleValue() * e.y());

            label = new Label(String.valueOf(e.element().id()));

            getChildren().addAll(rectangle, label);
        }
    }
}
