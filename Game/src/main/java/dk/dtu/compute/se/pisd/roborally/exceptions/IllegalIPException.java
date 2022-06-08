package dk.dtu.compute.se.pisd.roborally.exceptions;

public class IllegalIPException extends Exception {
    public IllegalIPException() {
        super("Not a valid IP");
    }
}
