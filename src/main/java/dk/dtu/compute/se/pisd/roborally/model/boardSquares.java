package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.util.List;

public interface boardSquares{
    Player getPlayer();
    void setPlayer(Player player);
    List<Heading> getWalls();
    List<FieldAction> getActions();
}
