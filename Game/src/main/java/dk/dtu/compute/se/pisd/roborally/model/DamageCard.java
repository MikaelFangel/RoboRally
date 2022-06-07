package dk.dtu.compute.se.pisd.roborally.model;

public class DamageCard{
    public DamageCommand damageCommand;

    public DamageCard(DamageCommand damageCommand){
        this.damageCommand = damageCommand;
    }

    public String getDisplayName(){
        return damageCommand.displayName;
    }

    public DamageCommand getDamageCommand() {
        return damageCommand;
    }
}
