package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * @author Frederik G. Petersen (s215834)
 */
public class PriorityAntenna extends FieldAction {
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        return true;
    }
}
