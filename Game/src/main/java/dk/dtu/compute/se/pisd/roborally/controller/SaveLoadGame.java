package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.StartGear;
import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ReadWriteGame;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Frederik G. Petersen (s215834)
 *
 * Manages Saving and Loading games of Roborally to the and from the drive.
 */
public class SaveLoadGame {
    final static private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    private static final String BOARDS_FOLDER = "boards";
    private static final String SAVED_BOARDS_FOLDER = "savedBoards";
    private static final String JSON_EXT = "json";
    private static boolean newBoardCreated = false;

    /**
     * Author: Frederik G. Petersen (s215834)
     *
     * Saves the games state into a file.
     * Captures the players states, board layout and other metadata in the game.
     * @param board the instance of the game board in play.
     * @param name  the file name the game should be saved as.
     */
    public static void saveBoardToDisk(Board board, String name) {
        // Setting up the board template
        String json = SerializeState.serializeGame(board);

        ReadWriteGame.writeGameToDisk(name, json);

    }

    /**
     * Author: Frederik G. Petersen (s215834)
     *
     * Load's a saved Board from a file.
     * Everything gets saved. This includes:
     * Player's state, Board layout, currentPlayer and more
     * @param name the file name of the board to get loaded
     * @return the instance of Board loaded from a file.
     */
    public static Board loadBoard(String name) throws BoardNotFoundException {
        String resourcePath = SAVED_BOARDS_FOLDER + "/" + name + "." + JSON_EXT;
        String json = ReadWriteGame.readGameFromDisk(resourcePath);

        return SerializeState.deserializeGame(json, true);

    }

    /**
     * Author: Frederik G. Petersen (s215834)
     *
     * The function create a totally new board where only the board layout is determined by input.
     * There are no such thing saved a players state in this.
     * @param numPlayers number of players for the new game
     * @param boardName  the filename of the board that should be loaded
     * @return the new Board instance with the board layout of the parameter as well as corresponding player number
     */
    public static Board newBoard(int numPlayers, String boardName) throws BoardNotFoundException {
        newBoardCreated = true;


        String resourcePath = BOARDS_FOLDER + "/" + boardName + "." + JSON_EXT;
        String json = ReadWriteGame.readGameFromDisk(resourcePath);

        Board board = SerializeState.deserializeGame(json, false);

        // Create the players and place them
        for (int i = 0; i < numPlayers; i++) {
            Player newPlayer = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
            board.addPlayer(newPlayer);
        }

        List<Space> startGears = getAllSpacesOfTypeByFieldAction(board, new StartGear());
        placePlayersRandomly(board.getPlayers(), startGears);

        return board;
    }

    /**
     * Author: Frederik G. Petersen (s215834)
     *
     * Places Player randomly on the provided possible spaces.
     * @param players Player to be placed
     * @param possibleSpaces Possible spaces that the players should be able to be placed on.
     */
    private static void placePlayersRandomly(List<Player> players, List<Space> possibleSpaces) {
        // TODO Make it random

        for (Player currentPlayer : players) {
            Space currentSpace = possibleSpaces.get(0);

            currentPlayer.setSpace(currentSpace);
            possibleSpaces.remove(currentSpace);

            // TODO Make players face correct direction on start, based on where they are spawned.
            // For now  its just pre determined since it's always the same with the start layout.
            currentPlayer.setHeading(Heading.EAST);
        }
    }

    /**
     * Author: Frederik G. Petersen (s215834)
     *
     * Gets a List of Spaces that has an instance of FieldAction.
     * @param board Board to search through
     * @param action The action the spaces have that you are looking for.
     * @return List of Spaces that has instances of that FieldAction
     */
    private static List<Space> getAllSpacesOfTypeByFieldAction(Board board, FieldAction action) {
        List<Space> spaces = new ArrayList<>();

        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                Space curSpace = board.getSpace(x, y);
                List<FieldAction> curSpaceActions = curSpace.getActions();

                if (curSpaceActions.size() == 0)
                    continue;

                String curFieldActionName = curSpaceActions.get(0).getClass().getSimpleName();
                if (curFieldActionName.equals(action.getClass().getSimpleName())) {
                    spaces.add(curSpace);
                }
            }
        }
        return spaces;
    }

    public static boolean getNewBoardCreated() {
        return newBoardCreated;
    }
}
