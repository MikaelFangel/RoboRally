package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class RebootToken extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            RebootToken token = (RebootToken) space.getActions().get(0);
            Player player = space.getPlayer();

            if (player != null) {
                gameController.rmc.moveForward(player, 1);
            }

        }
        return false;
    }
}