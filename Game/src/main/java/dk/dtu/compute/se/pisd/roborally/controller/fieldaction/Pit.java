package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;


/**
 * @author Ahmad Sandhu, Frederik Petersen, Mikael Fangel
 */
public class Pit extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        // Find reboot token.
        RebootToken.respawnPlayer(gameController, space.getPlayer());

        return true;
    }
}
