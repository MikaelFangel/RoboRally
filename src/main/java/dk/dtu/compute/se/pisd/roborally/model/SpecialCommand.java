package dk.dtu.compute.se.pisd.roborally.model;
import java.util.List;

public enum SpecialCommand {

    SANDBOXROUTINE("SandboxRoutine",Command.MOVE1,Command.MOVE2,Command.MOVE3,Command.MOVEBACK,
            Command.LEFT,Command.RIGHT,Command.UTURN),
    WEASELROUTINE("WeaselRoutine",Command.LEFT,Command.RIGHT,Command.UTURN),
    SPEEDROUTINE("SpeedRoutine",Command.MOVE3);


    final private String displayName;
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
