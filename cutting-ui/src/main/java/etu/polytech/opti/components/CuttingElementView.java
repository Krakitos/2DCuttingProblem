package etu.polytech.opti.components;

import etu.polytech.optim.api.lang.CuttingElement;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

/**
 * Created by Morgan on 14/03/2015.
 */
public class CuttingElementView extends ListCell<CuttingElement> {

    @Override
    protected void updateItem(CuttingElement item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setText(null);
            setGraphic(null);
        }else{
            setText(item.width() + " x " + item.height() + " -> " + item.asking());

            Rectangle preview;

            if(item.width() == item.height())
                preview = new Rectangle(50, 50);
            else if(item.width() > item.height())
                preview = new Rectangle(50, 30);
            else
                preview = new Rectangle(30, 50);

            setGraphic(new HBox(preview, new Separator(Orientation.VERTICAL)));
        }
    }
}
