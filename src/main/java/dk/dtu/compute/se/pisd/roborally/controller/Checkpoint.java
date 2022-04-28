package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Checkpoint extends FieldAction {

    private int checkpointNumber;

    public void setCheckpointNumber(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }

    public int getCheckpointNumber() {
        return checkpointNumber;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Checkpoint checkpoint = (Checkpoint) space.getActions().get(0);

            Player player = space.getPlayer();

            if (player != null && player.checkPoints + 1 == checkpoint.checkpointNumber) {
                player.checkPoints++;
                return true;
            }
        }

        return false;
    }
}
