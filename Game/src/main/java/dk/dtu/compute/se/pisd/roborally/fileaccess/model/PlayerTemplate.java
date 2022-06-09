package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

/**
 * @author Frederik G. Petersen (s215834)
 */
public class PlayerTemplate {
    public String name;
    public String color;
    public int energyCount;
    public int checkPoints;

    public int priority;

    public int spaceX;
    public int spaceY;
    public String heading;

    public CommandCardFieldTemplate[] program;
    public CommandCardFieldTemplate[] cards;
}
