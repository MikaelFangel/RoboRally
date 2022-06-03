package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.StartGear;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveLoadGame {

    final static private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    private static final String BOARDS_FOLDER = "boards";
    private static final String SAVED_BOARDS_FOLDER = "savedBoards";

    private static final String JSON_EXT = "json";

    private static boolean boardLoaded = false;

    /**
     * Saves the games state into a file.
     * Captures the players states, board layout and other metadata in the game.
     * @param board the instance of the game board in play.
     * @param name the file name the game should be saved as.
     */
    public static void saveBoardToDisk(Board board, String name) {
        // Setting up the board template
        String json = SerializeState.serializeGame(board);

        ReadWriteGame.writeGameToDisk(name, json);

    }

    /**
     * Load's a saved Board from a file.
     * Everything gets saved. This includes:
     * Player's state, Board layout, currentPlayer and more
     * @param name the file name of the board to get loaded
     * @return the instance of Board loaded from a file.
     */
    public static Board loadBoard(String name) {
        Board board = null;

        String resourcePath = SAVED_BOARDS_FOLDER + "/" + name + "." + JSON_EXT;
        String json = ReadWriteGame.readGameFromDisk(resourcePath);

        if (json != null){
            board = SerializeState.deserializeGame(json, true);
        }

        // TODO look at this
        //boardLoaded = true;

        return board;
    }

    /**
     * The function create a totally new board where only the board layout is determined by input.
     * There are no such thing saved a players state in this.
     * @param numPlayers number of players for the new game
     * @param boardName the filename of the board that should be loaded
     * @return the new Board instance with the board layout of the parameter as well as corresponding player number
     */
    public static Board newBoard(int numPlayers, String boardName){
        Board board = null;

        String resourcePath = BOARDS_FOLDER + "/" + boardName + "." + JSON_EXT;
        String json = ReadWriteGame.readGameFromDisk(resourcePath);

        if (json != null){
            board = SerializeState.deserializeGame(json, false);
        }

        // Create the players and place them
        for (int i = 0; i < numPlayers; i++) {
            Player newPlayer = new Player(board, PLAYER_COLORS.get(i), "Player " + (i+1));
            board.addPlayer(newPlayer);
        }

        List<Space> startGears = getAllSpacesOfTypeByFieldAction(board, new StartGear());
        placePlayersRandomly(board.getPlayers(), startGears);

        // TODO look at this
        //boardLoaded = true;

        return board;
    }

    /**
     * Describes whether a game has been loaded in or not
     * @return the boardLoaded boolean.
     */
    public static boolean getBoardLoaded(){
        return boardLoaded;
    }

    private static void placePlayersRandomly(List<Player> players, List<Space> possibleSpaces){
        // TODO Make it random

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            Space currentSpace = possibleSpaces.get(0);

            currentPlayer.setSpace(currentSpace);
            possibleSpaces.remove(currentSpace);

            // TODO Make players face correct direction on start, based on where they are spawned.
            // For now  its just pre determined since it's always the same with the start layout.
            currentPlayer.setHeading(Heading.EAST);
        }
    }

    private static List<Space> getAllSpacesOfTypeByFieldAction(Board board, FieldAction action){
        List<Space> spaces = new ArrayList<>();

        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                Space curSpace = board.getSpace(x,y);
                List<FieldAction> curSpaceActions = curSpace.getActions();

                if (curSpaceActions.size() == 0)
                    continue;

                String curFieldActionName = curSpaceActions.get(0).getClass().getSimpleName();
                if (curFieldActionName.equals(action.getClass().getSimpleName())){
                    spaces.add(curSpace);
                }
            }
        }
        return spaces;
    }
}
