/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.httpclient.ServerListView;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ReadWriteGame;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.Board;

import javafx.application.Platform;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controls the application before the game is started
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class AppController implements Observer {
    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private RoboRally roboRally;
    private GameController gameController;
    private final Client client = new Client();
    private boolean serverClientMode = false;
    private final ServerListView slv = new ServerListView(client, this);

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Used by the new game menu option to start a new game
     */
    public void newGame() {
        Optional<Integer> numPlayers = askUserForNumberOfPlayers();

        if (numPlayers.isPresent()) {
            // The UI should not allow this, but in case this happens anyway.
            // give the user the option to save the game or abort this operation!
            if (gameController != null && !stopGame()) return;

            createNewGame(numPlayers.get(), false);
        }
    }

    /**
     * Creates a new game and shows the game
     * @param numPlayers the numbers of players to start the game with
     * @param prevFailed if previous board attempt falied
     */
    private void createNewGame(int numPlayers, boolean prevFailed) {
        Optional<String> chosenBoard = askUserWhichDefaultBoard(prevFailed);
        if (chosenBoard.isPresent()) {
            try {
                Board board = SaveLoadGame.newBoard(numPlayers, chosenBoard.get());
                setupGameController(board);
                if (client.isConnectedToServer())
                    client.updateGame(SerializeState.serializeGame(board));
            } catch (BoardNotFoundException e) {
                createNewGame(numPlayers, true);
            }
        }
    }

    /**
     * The menu button to save the game
     */
    public void saveGame() {
        TextInputDialog dialogS = new TextInputDialog();

        dialogS.setTitle("SAVE GAME");
        dialogS.setHeaderText("Enter a Save game name");

        final Optional<String> resultS = dialogS.showAndWait();
        resultS.ifPresent(s -> SaveLoadGame.saveBoardToDisk(gameController.board, s));
    }

    /**
     * Loads a pre-existing game
     */
    public void loadGame() {
        if (gameController == null) {
            createLoadedGame(false);
        }
    }

    private void createLoadedGame(boolean prevFailed) {
        Optional<String> chosenBoard = askUserWhichSavedBoard(prevFailed);

        if (chosenBoard.isPresent()) {
            try {
                if ("Test204".equals(chosenBoard.get())) {
                    System.out.println("Is the same");
                }

                Board board = SaveLoadGame.loadBoard(chosenBoard.get());
                setupGameController(board);
            } catch (BoardNotFoundException e) {
                createLoadedGame(true);
            }
        }
    }

    /**
     * Hosts a new game on the server and starts the game afterwards
     * @param errorMessage errormessage from last earlier
     */
    public void hostGame(String... errorMessage) {
        String[] box = new String[]{"Start game server", "Server name:"};
        if (errorMessage.length != 0)
            box[1] = errorMessage[0] + "\ntry again";
        String result = roboRally.getPopupBoxes().getStringInput(box);
        if (result == null)
            return;
        String response = client.hostGame(result);
        if (!Objects.equals(response, "success"))
            hostGame(response);
        else {
            serverClientMode = true;
            newGame();
        }
    }

    /**
     * Joins the chosen game
     * @param id which id to join
     */
    public void joinGame(String id) {
        String message = client.joinGame(id);
        if (message.equals("ok")) {
            serverClientMode = true;
            Board board = SerializeState.deserializeGame(client.getGameState(), true);
            setupGameController(board);
            gameController.setPlayerNumber(client.getRobotNumber());

        } else {
            showErrorMessage(new String[]{"Error", message, "refresh and try again"});
        }
    }

    /**
     * Gets a list of available servers
     */
    public void connectToServer() {
        String serverList = client.listGames();
        if (serverList.equals("server timeout")) {
            showErrorMessage(new String[]{"error", serverList, "try again"});
            return;
        }
        slv.addServer(serverList);
        slv.viewTable();
    }

    /**
     * Disconnects the player from the server
     */
    public void disconnectFromServer() {
        client.leaveGame();
    }

    /**
     * Sets up a new game and shows the ui
     * @param board which board to create the game with
     */
    private void setupGameController(Board board) {
        gameController = new GameController(this, Objects.requireNonNull(board), serverClientMode ? client : null);
        //initializePlayers(board);
        board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    /**
     * Shows a dialog box for how many players to join a game
     * @return number of players chosen
     */
    private Optional<Integer> askUserForNumberOfPlayers() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");

        return dialog.showAndWait();
    }

    /**
     * Ask the user about which board they wish to use
     * @param prevFailed if previous attempt failed
     * @return board chosen
     */
    private Optional<String> askUserWhichDefaultBoard(boolean prevFailed) {

        List<String> allDefaultBoardNames = ReadWriteGame.getNamesOfDefaultBoard();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(allDefaultBoardNames.get(0), allDefaultBoardNames);

        dialog.setTitle("CHOOSE BOARD");
        if (prevFailed)
            dialog.setHeaderText("The board does not exists. Select another one");
        else
            dialog.setHeaderText("Select which board to play");

        return dialog.showAndWait();
    }

    /**
     * Ask the user which saved board to load
     * @param prevFailed previous attempt failed
     * @return chosen board
     */
    private Optional<String> askUserWhichSavedBoard(boolean prevFailed) {
        // Get all files in resource
        List<String> allSavedBoardNames = ReadWriteGame.getNamesOfSavedBoards();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(allSavedBoardNames.get(0), allSavedBoardNames);

        dialog.setTitle("LOAD GAME");
        if (prevFailed)
            dialog.setHeaderText("The board did not exists. Try another");
        else
            dialog.setHeaderText("Pick a game to load!");

        return dialog.showAndWait();
    }


    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Shows a popup if the player want's to exit the game or not.
     */
    public void exit() {
        if (gameController != null) {
            Optional<ButtonType> result = roboRally.getPopupBoxes().warningBox(new String[]{"Exit RoboRally?", "Are you sure you want to exit RoboRally?"});

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            client.leaveGame();
            Platform.exit();
        }
    }

    /**
     * Tells if the game is running of not
     * @return true if game is running
     */
    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    public void initializePlayers(Board board) {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            board.getPlayer(i).populateCards(gameController);
        }

    }

    public RoboRally getRoboRally() {
        return roboRally;
    }

    public void showErrorMessage(String[] reason) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(reason[0]);
        alert.setHeaderText(reason[1]);
        alert.setContentText(reason[2]);
        alert.showAndWait();
    }

}
