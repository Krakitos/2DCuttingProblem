package etu.polytech.opti.components;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.Collection;

/**
 * Created by Morgan on 20/04/2015.
 */
public class CuttingPatternView extends Pane {

    public void render(CuttingConfiguration config, Collection<CuttingLayoutElement> elements, double hgap, double vgap) {
        getChildren().clear();

        renderBackground();
        renderRectangles(config, elements, hgap, vgap);
    }

    private void renderRectangles(CuttingConfiguration config, Collection<CuttingLayoutElement> elements, double     hgap, double vgap) {
        DoubleBinding scaleX, scaleY;

        int grayStep = 256 / config.elements().size();

        //Same orientation
        //if(config.sheet().width() > config.sheet().height() && getWidth() > getHeight()){
            scaleX = widthProperty().subtract(hgap).divide(config.sheet().width());
            scaleY = heightProperty().subtract(vgap).divide(config.sheet().height());

        /*}else{ //Rotated
            scaleY = widthProperty().divide(config.sheet().width());
            scaleX = heightProperty().divide(config.sheet().height());
        }*/

        elements.forEach(e -> {
            Color c = Color.grayRgb(grayStep * e.element().id());
            Label l = new Label(String.valueOf(e.element().id()));

            l.layoutXProperty().bind(scaleX.multiply(e.x()));
            l.layoutYProperty().bind(scaleY.multiply(e.y()));

            l.maxWidthProperty().bind(scaleX.multiply(e.width()));
            l.minWidthProperty().bind(l.maxWidthProperty());

            l.maxHeightProperty().bind(scaleY.multiply(e.height()));
            l.minHeightProperty().bind(l.maxHeightProperty());

            l.setStyle("-fx-background-color: " + c.toString().replace("0x", "#") + "; "
                    + " -fx-border-radius: 2px; -fx-border-color: black");

            l.setTextAlignment(TextAlignment.CENTER);
            l.setAlignment(Pos.CENTER);
            getChildren().add(l);
        });
    }

    private void renderBackground() {
        setStyle("-fx-background-color: lightgray");
    }
}
