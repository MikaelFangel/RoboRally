package dk.dtu.compute.se.pisd.roborally.model;

public class SpecialCard extends Card {
    public final SpecialCommand specialCommand;

    public SpecialCard(String description, String effect, int cost,SpecialCommand specialCommand) {
        super(description,effect,cost);
        this.specialCommand = specialCommand;
    }

    public String getName(){
        return specialCommand.displayName;
    }

    public SpecialCommand getSpecialCommand() {
        return specialCommand;
    }
}
