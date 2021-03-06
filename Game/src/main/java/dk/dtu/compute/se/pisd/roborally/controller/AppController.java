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
import dk.dtu.compute.se.pisd.httpclient.GameListView;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ReadWriteGame;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.Board;

import javafx.application.Platform;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    private final GameListView slv = new GameListView(client, this);

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Used by the new game menu option to start a new game
     */
    public void newGame() {
        Optional<Integer> numPlayers = roboRally.getPopupBoxes().askUserForNumberOfPlayers(PLAYER_NUMBER_OPTIONS);

        if (numPlayers.isEmpty())
            client.leaveGame();
        if (numPlayers.isPresent()) {
            // The UI should not allow this, but in case this happens anyway.
            // give the user the option to save the game or abort this operation!
            if (gameController != null && !stopGame()) return;

            createNewGame(numPlayers.get(), false);
        }
    }

    /**
     * Creates a new game and shows the game
     *
     * @param numPlayers the numbers of players to start the game with
     * @param prevFailed if previous board attempt falied
     */
    private void createNewGame(int numPlayers, boolean prevFailed) {
        //Optional<String> chosenBoard = askUserWhichDefaultBoard(prevFailed);


        Optional<String> chosenBoard = roboRally.getPopupBoxes().askUserForBoardName(ReadWriteGame.getNamesOfDefaultBoard());
        if (chosenBoard.isEmpty())
            client.leaveGame();
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
     * The action to be taken when a player click on the saveGame button
     *
     * @author Christian Andersen
     */
    public void saveGame() {
        String[] s = new String[]{"SAVE GAME", "Enter a Save game name"};
        String dialog = roboRally.getPopupBoxes().getStringInput(s);

        if (dialog != null)
            SaveLoadGame.saveBoardToDisk(gameController.board, dialog);
    }

    /**
     * Loads a pre-existing game
     *
     * @author Frederik Petersen
     */
    public void loadGame() {
        if (gameController == null) {
            createLoadedGame();
        }
    }

    /**
     * @author Frederik Petersen
     */
    private void createLoadedGame() {
        Optional<String> chosenBoard = roboRally.getPopupBoxes().askUserForBoardName(ReadWriteGame.getNamesOfSavedBoards());

        if (chosenBoard.isPresent()) {
            try {
                if ("Test204".equals(chosenBoard.get())) {
                    System.out.println("Is the same");
                }

                Board board = SaveLoadGame.loadBoard(chosenBoard.get());
                setupGameController(board);
            } catch (BoardNotFoundException e) {
                createLoadedGame();
            }
        }
    }

    /**
     * Hosts a new game on the server and starts the game afterwards
     *
     * @param errorMessage errormessage from last earlier
     * @author Christian Andersen, Mikael Fangel
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
            box[0] = "Start new or continue?:";
            box[1] = "Start new or continue?:" ;
            List<String> list = new ArrayList<>();
            list.add("new game");
            list.add("load game");
            Optional<String> out = roboRally.getPopupBoxes().askUserForBoardName(list);
            if (out.isEmpty()) {
                client.leaveGame();
                return;
            }
            if (out.get().equals("new game"))
                newGame();
            else
                loadGame();
        }
    }

    /**
     * Joins the chosen game
     *
     * @param id which id to join
     * @author Mikael Fangel, Christian Andersen
     */
    public void joinGame(String id) {
        String message = client.joinGame(id);
        if (message.equals("ok")) {
            serverClientMode = true;
            Board board = SerializeState.deserializeGame(client.getGameState(), true);
            setupGameController(board);
            gameController.setPlayerNumber(client.getRobotNumber());

        } else
            roboRally.getPopupBoxes().warningBox(new String[]{"Error", message, "refresh and try again"});
    }

    /**
     * Gets a list of available servers
     *
     * @author Christian Andersen
     */
    public void connectToServer() {
        String serverList = client.listGames(); //gets the list of servers
        if (serverList.equals("server timeout")) { //if server is unreachable
            roboRally.getPopupBoxes().warningBox(new String[]{"error", serverList, "try again"});
            return;
        }
        slv.addServer(serverList); //adds the servers to the view
        slv.viewTable(); //creates the view
    }

    /**
     * Disconnects the player from the server
     *
     * @author Christian Andersen
     */
    public void disconnectFromServer() {
        client.leaveGame();
    }

    /**
     * Sets up a new game and shows the ui
     *
     * @param board which board to create the game with
     */
    private void setupGameController(Board board) {
        gameController = new GameController(this, Objects.requireNonNull(board), serverClientMode ? client : null);
        board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
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
            String[] s = new String[]{"Exit RoboRally?", "Are you sure you want to exit RoboRally?"};
            Optional<ButtonType> result = roboRally.getPopupBoxes().warningBox(s);

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
     *
     * @return true if game is running
     */
    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    public RoboRally getRoboRally() {
        return roboRally;
    }

}
