package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Checkpoint extends FieldAction {

    private static int highestCheckpointNumber = 0;
    private int checkpointNumber;

    public void setCheckpointNumber(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }

    public int getCheckpointNumber() {
        return checkpointNumber;
    }

    public Checkpoint(){
        highestCheckpointNumber++;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Checkpoint checkpoint = (Checkpoint) space.getActions().get(0);

            Player player = space.getPlayer();

            if (player != null && player.checkPoints + 1 == checkpoint.checkpointNumber) {
                player.checkPoints++;
                if(player.checkPoints == highestCheckpointNumber) {
                    System.out.println("GAME WON!");
                    // TODO Announce Winner and end game
                }
                return true;
            }
        }

        return false;
    }
}
