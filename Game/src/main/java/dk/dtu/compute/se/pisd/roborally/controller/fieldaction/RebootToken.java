package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * @author Mikael Fangel
 */
public class RebootToken extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        return true;
    }
}
