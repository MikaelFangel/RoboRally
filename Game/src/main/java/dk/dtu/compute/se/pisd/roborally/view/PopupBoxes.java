package dk.dtu.compute.se.pisd.roborally.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.util.List;
import java.util.Optional;

/**
 * This class handle all the different styles of popup boxes.
 *
 * @author Christian Andersen
 */
public class PopupBoxes {

    /**
     * Creates an option box using 2 buttons: 'OK' or 'cancel'
     *
     * @param input the data that should be displayed
     * @return the descision of the player
     */
    public Optional<ButtonType> warningBox(String[] input){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(input[0]);
        alert.setContentText(input[1]);
        return alert.showAndWait();
    }

    /**
     *
     * @param input the data that should be displayed
     * @return the descision of the player
     */
    public String getStringInput(String[] input){
        TextInputDialog serverCreation = new TextInputDialog();
        serverCreation.setTitle(input[0]);
        serverCreation.setHeaderText(input[1]);
        Optional<String> disision = serverCreation.showAndWait();
        if (!disision.isEmpty())
            return disision.get();
        return null;
    }

    /**
     * Creates a dropdown menu of the available choices
     *
     * @param list of data that should be selectable
     * @return the descision of the player
     */
    public Optional<Integer> askUserForNumberOfPlayers(List<Integer> list) {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(list.get(0), list);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");

        return dialog.showAndWait();
    }

    /**
     * Creates a dropdown menu of the available choices
     *
     * @param list of data that should be selectable
     * @return the descision of the player
     */
    public Optional<String> askUserForBoardName(List<String> list) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(list.get(0), list);
        dialog.setTitle("CHOOSE BOARD");
        dialog.setHeaderText("Select which board to play");

        return dialog.showAndWait();
    }
}
