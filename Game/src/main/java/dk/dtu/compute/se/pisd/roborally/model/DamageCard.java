package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class DamageCard extends Subject {
    public final DamageCommand damageCommand;

    public DamageCard(DamageCommand damageCommand) {
        this.damageCommand = damageCommand;
    }

    public String getName(){
        return damageCommand.displayName;
    }
}
