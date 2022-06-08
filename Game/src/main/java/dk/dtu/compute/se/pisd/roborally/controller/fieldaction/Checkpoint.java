package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
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

            // Checks if the player step on the checkpoint in correct order.
            if (player != null && player.checkPoints + 1 == checkpoint.checkpointNumber) {
                player.checkPoints++;
                // If player has won
                if (player.checkPoints == highestCheckpointNumber) {
                    showWinMessage(space);
                    highestCheckpointNumber = 0; // Needs because the static variable is never resat
                    gameController.endGame();
                }
                return true;
            }
        }
        return false;
    }

    private void showWinMessage(Space space){
        Alert winMsg = new Alert(Alert.AlertType.INFORMATION);
        winMsg.setTitle("Game Ended");
        winMsg.setHeaderText("The winner is: " + space.getPlayer().getName());
        winMsg.setContentText("The game has ended and will shutdown");
        winMsg.showAndWait();
    }
}
