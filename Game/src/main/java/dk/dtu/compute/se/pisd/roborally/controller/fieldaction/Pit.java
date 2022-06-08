package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Pit extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        return false;
    }
}
