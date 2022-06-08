package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Energy extends FieldAction {

    private boolean hasEnergyCube;

    public void setHasEnergyCube(boolean hasEnergyCube) {
        this.hasEnergyCube = hasEnergyCube;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        boolean result = false;
        if (space.getActions().size() > 0) {
            Energy energy = (Energy) space.getActions().get(0);
            if (energy.hasEnergyCube || gameController.board.step == 4) {
                space.getPlayer().energyCount++;
                energy.hasEnergyCube = false;
                result = true;
            }
        }
        return result;
    }
}
