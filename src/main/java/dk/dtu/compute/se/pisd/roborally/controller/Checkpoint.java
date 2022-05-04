package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Checkpoint extends FieldAction {

    private static int highestCheckpointNumber = 0;
    private int checkpointNumber;

    public void setCheckpointNumber(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }

    public int getCheckpointNumber() {
        return checkpointNumber;
    }

    public Checkpoint() {
        highestCheckpointNumber++;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Checkpoint checkpoint = (Checkpoint) space.getActions().get(0);

            Player player = space.getPlayer();

            if (player != null && player.checkPoints + 1 == checkpoint.checkpointNumber) {
                player.checkPoints++;
                if (player.checkPoints == highestCheckpointNumber) {
                    // TODO Ask Player to start new game
                    Alert winMsg = new Alert(Alert.AlertType.INFORMATION);
                    winMsg.setTitle("Game Ended");
                    winMsg.setContentText("The winner is: " + space.getPlayer().getName());
                    winMsg.showAndWait();
                    Platform.exit();
                }
                return true;
            }
        }

        return false;
    }
}
