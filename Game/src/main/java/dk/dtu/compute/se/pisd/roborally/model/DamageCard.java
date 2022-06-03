package dk.dtu.compute.se.pisd.roborally.model;

public class DamageCard {
    public final DamageCommand damageCommand;

    public DamageCard(DamageCommand damageCommand) {
        this.damageCommand = damageCommand;
    }

    public String getName(){
        return damageCommand.displayName;
    }
}
