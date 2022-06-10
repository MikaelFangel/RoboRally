package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.SaveLoadGame;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ReadWriteGame;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.Objects;

/**
 * @author Ahmad Sandhu, Mikael Fangel
 */
public class Pit extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        // Find reboot token.
        RebootToken.respawnPlayer(gameController.board, space.getPlayer());

        return true;
    }
}
