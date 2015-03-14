package etu.polytech.opti.components;

import etu.polytech.optim.api.lang.CuttingElement;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import java.util.Map;

/**
 * Created by Morgan on 14/03/2015.
 */
public class CuttingElementView extends ListCell<Map.Entry<CuttingElement, Integer>> {

    @Override
    protected void updateItem(Map.Entry<CuttingElement, Integer> item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setText(null);
            setGraphic(null);
        }else{
            final CuttingElement element = item.getKey();
            setText(element.width() + " x " + element.height() + " -> " + item.getValue());

            Rectangle preview;

            if(element.width() == element.height())
                preview = new Rectangle(50, 50);
            else if(element.width() > element.height())
                preview = new Rectangle(50, 30);
            else
                preview = new Rectangle(30, 50);

            setGraphic(new HBox(preview, new Separator(Orientation.VERTICAL)));
        }
    }
}
