package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class SpecialCard extends Subject {
    public final SpecialCommand specialCommand;


    public SpecialCard(SpecialCommand specialCommand) {
        this.specialCommand = specialCommand;
    }


    public String getName(){
        return specialCommand.displayName;
    }
}
