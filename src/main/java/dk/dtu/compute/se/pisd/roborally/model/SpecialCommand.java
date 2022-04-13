package dk.dtu.compute.se.pisd.roborally.model;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import java.util.List;

public enum SpecialCommand {

    ENERGYEOUTINE("Eneryg routine"),
    SANDBOXROUTINE("Sandbox routine",Command.MOVE1,Command.MOVE2,Command.MOVE3,Command.MOVEBACK,
            Command.LEFT,Command.RIGHT,Command.UTURN),
    WEASELROUTINE("Weasel routine",Command.LEFT,Command.RIGHT,Command.UTURN),
    SPEEDROUTINE("Speed routine",Command.MOVE3),
    SPAMFOLDER("Spam folder"),
    REPEATROUTINE("Repeat Routine");



    final public String displayName;
    final private List<Command> options;


    SpecialCommand(String displayName, Command... options){
       this.displayName = displayName;
       this.options = List.of(options);
    }

    public boolean isInteractive(){
        return !options.isEmpty();
    }

    public List<Command> getOptions() {
        return options;
    }
}
