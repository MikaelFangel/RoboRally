package dk.dtu.compute.se.pisd.roborally.model;

public class DamageCard extends Card {
    public DamageCommand damageCommand;

    public DamageCard(String description, String effect, int cost, DamageCommand damageCommand) {
        super(description,effect,cost);
        this.damageCommand = damageCommand;
    }

    public String getDisplayName(){
        return damageCommand.displayName;
    }

    public DamageCommand getDamageCommand() {
        return damageCommand;
    }
}
