package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

public class PlayerTemplate {
    public String name;
    public String color;
    public int energyCount;
    public int checkPoints;

    public int spaceX;
    public int spaceY;
    public String heading;

    public CommandCardFieldTemplate[] program;
    public CommandCardFieldTemplate[] cards;
}
