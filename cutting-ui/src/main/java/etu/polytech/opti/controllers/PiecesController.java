package etu.polytech.opti.controllers;

import etu.polytech.optim.api.lang.CuttingElement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by Morgan on 13/03/2015.
 */
public class PiecesController {

    @FXML
    public TextField widthInput;

    @FXML
    public TextField heightInput;

    private Optional<PieceControllerCallback> callback = Optional.empty();

    public void setCallback(@NotNull final PiecesController.PieceControllerCallback callback){
        this.callback = Optional.of(callback);
    }

    public void handleOk(ActionEvent actionEvent) {
        if(checkInputs()) {
            int width = Integer.parseInt(widthInput.getText());
            int height = Integer.parseInt(heightInput.getText());
            callback.ifPresent(c -> c.onEditionEnded(Optional.of(new CuttingElement(width, height))));
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        callback.ifPresent(c -> c.onEditionEnded(Optional.empty()));
    }

    private boolean checkInputs(){
        try{
            Integer.parseInt(widthInput.getText());
            Integer.parseInt(heightInput.getText());
        }catch (NumberFormatException e){
            return false;
        }

        return true;
    }

    /**
     * Callback for the communication with controller
     */
    public static interface PieceControllerCallback{
        public void onEditionEnded(Optional<CuttingElement> element);
    }
}
