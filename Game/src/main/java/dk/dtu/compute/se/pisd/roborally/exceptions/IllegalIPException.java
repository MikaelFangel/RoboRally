package dk.dtu.compute.se.pisd.roborally.exceptions;

/**
 * Thrown when a given IP is not valid
 *
 * @author Mikael Fangel
 */
public class IllegalIPException extends Exception {
    public IllegalIPException() {
        super("Not a valid IP");
    }
}
