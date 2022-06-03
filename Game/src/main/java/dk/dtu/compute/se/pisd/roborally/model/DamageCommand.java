package dk.dtu.compute.se.pisd.roborally.model;

public enum DamageCommand {
    SPAM("SPAM"),
    TROJANHORSE("TROJAN HORSE"),
    WORM("WORM"),
    VIRUS("VIRUS");

    public String displayName;

    DamageCommand(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
