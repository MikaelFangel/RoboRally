package dk.dtu.compute.se.pisd.roborally.view;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class PopupBoxes {

    /*public Optional<String> desicionBox(String[] input){
        return Optional<String>;
    }

    public void warningBox(String[] input){

    }*/

    public String getStringInput(String[] input){
        TextInputDialog serverCreation = new TextInputDialog();
        serverCreation.setTitle(input[0]);
        serverCreation.setHeaderText(input[1]);
        if (input.length > 2){
            serverCreation.setHeaderText(input[2]);
        }
        return serverCreation.showAndWait().get();
    }
}
