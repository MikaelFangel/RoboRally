package dk.dtu.compute.se.pisd.roborally.exceptions;

/**
 *  @author Frederik G. Petersen (s215834)
 */
public class BoardNotFoundException extends Exception {
    private final String boardPath;

    /**
     * Exception for when a board does not exists
     * @param boardPath The path for the board attempted to find.
     */
    public BoardNotFoundException(String boardPath){
        this.boardPath = boardPath;
    }

    public String getBoardPath(){
        return this.boardPath;
    }
}
