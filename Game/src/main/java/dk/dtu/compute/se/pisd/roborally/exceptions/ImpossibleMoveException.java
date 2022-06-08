package dk.dtu.compute.se.pisd.roborally.exceptions;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class ImpossibleMoveException extends Exception {
    public ImpossibleMoveException(Player player, Space space, Heading heading) {
        super("Move impossible");
    }
}
