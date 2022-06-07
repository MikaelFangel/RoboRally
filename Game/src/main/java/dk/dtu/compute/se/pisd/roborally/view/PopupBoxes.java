package dk.dtu.compute.se.pisd.roborally.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class PopupBoxes {

    /*public Optional<String> desicionBox(String[] input){
        return Optional<String>;
    }
    */
    public Optional<ButtonType> warningBox(String[] input){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(input[0]);
        alert.setContentText(input[1]);
        return alert.showAndWait();
    }

    public String getStringInput(String[] input){
        TextInputDialog serverCreation = new TextInputDialog();
        serverCreation.setTitle(input[0]);
        serverCreation.setHeaderText(input[1]);
        Optional<String> disision = serverCreation.showAndWait();
        if (!disision.isEmpty())
            return disision.get();
        return null;
    }
}
